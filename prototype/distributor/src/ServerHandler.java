package prototype.distributor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network_protocol.MessageManager;
import prototype.protobuf.Control;
import prototype.protobuf.General;
import prototype.protobuf.Session;
import java.util.Date;

/*Questions I have
    - ChannelActive is called twice when I run my testing client
        - The things the client sends are only handled by one instance, so it is not *Currently* a problem
        - Why does this happen?
        - Will this have any future consequences?
*/

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private String selfName = "";
    private MessageManager msgmgr = new MessageManager();
    private DatabaseHandler dbmgr = new DatabaseHandler("Users.db");     //Can change where the database is stored later
    private long lastHeartbeat;
    // TODO: THIS
    private Thread heartbeatThread;
    private boolean running = true;


    private synchronized long getLastHeartbeat() {
        return lastHeartbeat;
    }

    private synchronized void setLastHeartbeat(long hb) {
        lastHeartbeat = hb;
    }

    //Updated to do the outer packet 'packaging' inside of the sendProto methods for ease of use
    public void sendProto(ByteString inner, General.FvPacketType type) {
        //Create outer packet
        General.FvPacket outerPacket = General.FvPacket.newBuilder()
                .setType(type)
                .setInnerPacket(inner)
                .build();

        byte[] bytes = outerPacket.toByteArray();
        ByteBuf buf = channel.alloc().buffer(bytes.length + 4);
        buf.writeIntLE(bytes.length);
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);
    }

    public void sendProto(ByteString inner, General.FvPacketType type, Channel c) {
        //Create outer packet
        General.FvPacket outerPacket = General.FvPacket.newBuilder()
                .setType(type)
                .setInnerPacket(inner)
                .build();

        byte[] bytes = outerPacket.toByteArray();
        ByteBuf buf = channel.alloc().buffer(bytes.length + 4);
        buf.writeIntLE(bytes.length);
        buf.writeBytes(bytes);
        c.writeAndFlush(buf);
    }

    public boolean onHandshake(Control.Handshake handshake_packet) {
        if (handshake_packet.getMagicNumber() != 0x12341234ABCDABCDl) {
            return false;
        }
        Control.Handshake reply =
                Control.Handshake.newBuilder().setMagicNumber(0xABCDABCABA123456l).build();

        sendProto(reply.toByteString(), General.FvPacketType.HANDSHAKE);
        return true;
    }

    private void loginFailure(){
        Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                .setType(Control.ServerMessageType.LOGIN_FAILED)
                .setSuccess(false)
                .build();

        sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE);
    }

    private void onLoginAux(Control.Login user_packet){
        if (UsersData.getUser(user_packet.getUsername()) == null) {
            UsersData.addUser(user_packet.getUsername(), channel);
            Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                    .setType(Control.ServerMessageType.SUCCESS)
                    .setSuccess(true)
                    .build();
            sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE);

            //Sends out an updated user list to all connected clients
            Control.UserList userListProto =
                    Control.UserList.newBuilder().addAllUsers(UsersData.keySet()).build();

            for (Channel target : UsersData.values()) {
                sendProto(userListProto.toByteString(), General.FvPacketType.USER_LIST, target);
            }

            selfName = user_packet.getUsername();
        } else {
            loginFailure();
        }
    }

    //Handles the database to check whether the user exists/can be created
    //Old code handling handling the new user has been moved to the auxiliary method
    public void onLogin(Control.Login user_packet) {
        if (user_packet.getNewUser()){
            //Is username already taken?
            if (dbmgr.hasUser(user_packet.getUsername())){
                loginFailure();
                return;
            }
            //Add the user to the databse
            dbmgr.addUser(user_packet.getUsername(), user_packet.getPassword());
            onLoginAux(user_packet);
        }else {
            if (!dbmgr.checkCredentials(user_packet.getUsername(), user_packet.getPassword())){
                loginFailure();
                return;
            }
            onLoginAux(user_packet);
        }
    }

    public void onHeartbeat(Control.Heartbeat heartbeat_in) {
        setLastHeartbeat(new Date().getTime());
        Control.Heartbeat heartbeat =
                Control.Heartbeat.newBuilder().setUtcTime(lastHeartbeat).build();

        sendProto(heartbeat.toByteString(), General.FvPacketType.HEARTBEAT);
    }

    public void onSessionRequest(Control.SessionRequest sr) {
        Control.ServerMessage reply;

        System.out.println("Session Request packet");

        // if user not found
        if (!UsersData.keySet().contains(sr.getName())) {
            reply = Control.ServerMessage.newBuilder()
                    .setType(Control.ServerMessageType.USER_NOT_FOUND)
                    .setSuccess(false)
                    .build();
            sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE);
            return;
        }

        //If the user is already in a session
        for (SessionData session : UsersData.getSessionList()){
             if (session.getClientList().contains(sr.getName())) {
                reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.USER_ALREADY_IN_SESSION)
                        .setSuccess(false)
                        .build();
                sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE);

                return;
            }
        }

        //Sends a SESSION_REQUEST packet to the host containing the name of the user trying to connect
        Control.SessionRequest.Builder srbuilder = Control.SessionRequest.newBuilder();
        srbuilder.setName(selfName);
        Control.SessionRequest newRequest = srbuilder.build();
        sendProto(newRequest.toByteString(), General.FvPacketType.SESSION_REQUEST, UsersData.getUser(sr.getName()));
    }

    public void onSessionResponse(Control.SessionResponse sr) {
        //Check that the "client" still exists
        if (UsersData.getUser(sr.getName()) == null)
            return;

        Control.ServerMessage reply;

        System.out.println("Session Response packet");

        //Connection refused by host
        if (!sr.getResponse()){
            reply = Control.ServerMessage.newBuilder()
                    .setType(Control.ServerMessageType.SESSION_REJECTED)
                    .setSuccess(false)
                    .build();
            sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE, UsersData.getUser(sr.getName()));
            return;
        }

        for (SessionData session : UsersData.getSessionList()) {
            // if user is already hosting a session
            if (session.getHostUser().equals(selfName)) {
                // add requesting user to current user's session
                session.addClientUser(sr.getName());

                // user is available: send session starting to requester
                reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.SESSION_STARTING)
                        .setSuccess(true)
                        .build();
                sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE, UsersData.getUser(sr.getName()));

                // send new user audio and video params
                sendProto(session.getVideoParams().toByteString(), General.FvPacketType.VIDEO_PARAMS, UsersData.getUser(sr.getName()));
                sendProto(session.getAudioParams().toByteString(), General.FvPacketType.AUDIO_PARAMS, UsersData.getUser(sr.getName()));

                return;
            }
        }

        //User is not hosting a session, a new session is crated
        UsersData.getSessionList().add(new SessionData(selfName, sr.getName()));

        // user is available: send session starting to requester    (Does not send to the host client)
        reply = Control.ServerMessage.newBuilder()
                .setType(Control.ServerMessageType.SESSION_STARTING)
                .setSuccess(true)
                .build();
        sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE, UsersData.getUser(sr.getName()));
    }

    public void onSessionClose(Control.SessionClose sc) {
        Control.ServerMessage reply;
        SessionData toRemove = null;

        for (SessionData session : UsersData.getSessionList()) {
            // if from host
            if (session.getHostUser().equals(selfName)) {
                // send sessionclose to all clients
                for (String user : session.getClientList()) {
                    sendProto(sc.getReasonBytes(), General.FvPacketType.SESSION_CLOSE, UsersData.getUser(user));
                }

                // it can just get rid of the session right dw about client list cuz
                // that garbo is only known about in the sessiondata
                toRemove = session;

            } else if (session.getClientList().contains(selfName)) { // if from client
                session.getClientList().remove(selfName);

                // send success message
                reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.SUCCESS)
                        .setSuccess(true)
                        .build();
                sendProto(reply.toByteString(), General.FvPacketType.SERVER_MESSAGE);

                return;
            }
        }

        if (toRemove != null) {
            UsersData.getSessionList().remove(toRemove);
        }
    }

    public void onVideoParams(Session.VideoParams vp) {
        System.out.println("Got VideoParams");
        for (SessionData session : UsersData.getSessionList()) {
            if (session.getHostUser().equals(selfName)) {
                // store params
                session.setVideoParams(vp);

                // must send clients the params, normally should be just 1
                for (String user : session.getClientList()) {
                    sendProto(vp.toByteString(), General.FvPacketType.VIDEO_PARAMS, UsersData.getUser(user));
                }
            }
        }
    }

    public void onAudioParams(Session.AudioParams ap) {
        System.out.println("Got AudioParams");
        for (SessionData session : UsersData.getSessionList()) {
            if (session.getHostUser().equals(selfName)) {
                // store params
                session.setAudioParams(ap);

                // must send clients the params, normally should be just 1
                for (String user : session.getClientList()) {
                    sendProto(ap.toByteString(), General.FvPacketType.AUDIO_PARAMS, UsersData.getUser(user));
                }
            }
        }
    }

    public void handlePacket(byte[] data) throws InvalidProtocolBufferException {
        General.FvPacket outer_packet = General.FvPacket.parseFrom(data);
        General.FvPacketType type = outer_packet.getType();

        switch (type) {
            case UNSPECIFIED:
                System.out.println("Unspecified packet");
                break;
            case HANDSHAKE:
                Control.Handshake hs = Control.Handshake.parseFrom(outer_packet.getInnerPacket());
                System.out.println("Handshake value: " + hs.getMagicNumber());
                if (!onHandshake(hs)) {
                    channel.close();
                }
                break;
            case LOGIN:
                Control.Login user_packet =
                        Control.Login.parseFrom(outer_packet.getInnerPacket());
                System.out.println("New User/Login Attempt: " + user_packet.getUsername());
                onLogin(user_packet);
                break;
            case SESSION_REQUEST:
                onSessionRequest(Control.SessionRequest.parseFrom(outer_packet.getInnerPacket()));
                break;
            case SESSION_RESPONSE:
                onSessionResponse(Control.SessionResponse.parseFrom(outer_packet.getInnerPacket()));
                break;
            case SESSION_CLOSE:
                Control.SessionClose sc =
                        Control.SessionClose.parseFrom(outer_packet.getInnerPacket());
                System.out.println("Session Close packet");

                onSessionClose(sc);
                break;
            case HEARTBEAT:
                //System.out.println("Heartbeat packet");
                Control.Heartbeat hb = Control.Heartbeat.parseFrom(outer_packet.getInnerPacket());
                //System.out.println("Timestamp: " + hb.getUtcTime());
                onHeartbeat(hb);
                break;
            case VIDEO_PARAMS:
                Session.VideoParams vp =
                        Session.VideoParams.parseFrom(outer_packet.getInnerPacket());

                onVideoParams(vp);
                break;
            case AUDIO_PARAMS:                             //Empty
                Session.AudioParams ap = Session.AudioParams.parseFrom(outer_packet.getInnerPacket());

                onAudioParams(ap);
                break;
            case DATA:
                Session.Data dp = Session.Data.parseFrom(outer_packet.getInnerPacket());
                for (SessionData session : UsersData.getSessionList()) {
                    if (session.getHostUser().equals(selfName)) { //
                        for (String user : session.getClientList()) { // send to all users
                            sendProto(dp.toByteString(), type, UsersData.getUser(user));
                        }
                    }
                }

                break;
            case CONTROL_INPUT:
                Session.ControlInput input = Session.ControlInput.parseFrom(outer_packet.getInnerPacket());
                for (SessionData session : UsersData.getSessionList()) {
                    if (session.getClientList().contains(
                            selfName)) { // find session that sender is in
                        sendProto(input.toByteString(), type, UsersData.getUser(session.getHostUser()));
                    }
                }

                break;
            default:                                //Need to add new packet types
                System.out.println("Protocol not defined");
                break;
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buffer = (ByteBuf) msg;
        byte[] raw_packet = new byte[buffer.readableBytes()];
        //System.out.println("Received raw packet size " + raw_packet.length);
        buffer.readBytes(raw_packet);
        long ctm = System.currentTimeMillis();
        msgmgr.parseData(raw_packet);
        long elapsed = System.currentTimeMillis() - ctm;
        buffer.release();

        while (msgmgr.hasPacket()) {
            try {
                handlePacket(msgmgr.nextPacket());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        channel = ctx.channel();
        lastHeartbeat = (new Date()).getTime();
        //System.out.println("Channel Active: " + lastHeartbeat);
        heartbeatThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long timeMs = (new Date()).getTime();
                    if (timeMs - getLastHeartbeat() > 70000) {
                        removeSelf();
                        channel.close();
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        heartbeatThread.start();
    }

    private void removeSelf() {
        running = false;
        if (selfName.equals("")) {
            return;
        }

        // if host loses connection
        SessionData toRemove = null;

        for (SessionData session : UsersData.getSessionList()) {
            // from host
            if (session.getHostUser().equals(selfName)) {
                Control.SessionClose sc =
                        Control.SessionClose.newBuilder().setReason("Host Disconnected").build();

                // send sessionclose to all clients
                for (String user : session.getClientList()) {
                    sendProto(sc.getReasonBytes(), General.FvPacketType.SESSION_CLOSE, UsersData.getUser(user));
                }

                toRemove = session;
                break;
            } else if (session.getClientList().contains(selfName)) {
                session.getClientList().remove(selfName);
                if (session.getClientList().size() == 0) {
                    toRemove = session;
                }
                break;
            }
        }

        // remove session
        if (toRemove != null) {
            UsersData.getSessionList().remove(toRemove);
        }

        UsersData.remove(selfName);

        //Sends out an updated user list to all connected clients
        Control.UserList userListProto =
                Control.UserList.newBuilder().addAllUsers(UsersData.keySet()).build();

        for (Channel target : UsersData.values()) {
            sendProto(userListProto.toByteString(), General.FvPacketType.USER_LIST, target);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        removeSelf();
        ctx.close();
    }
}
