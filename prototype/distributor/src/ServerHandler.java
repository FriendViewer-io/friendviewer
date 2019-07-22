package prototype.distributor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import prototype.distributor.UsersData;
import prototype.distributor.SessionData;
import prototype.networkProtocol.MessageManager;
import prototype.protobuf.Control;
import prototype.protobuf.General;
import prototype.protobuf.Session;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private String selfName = "";
    private MessageManager mgr = new MessageManager();
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

    public void sendProto(Message proto) {
        byte[] bytes = proto.toByteArray();
        ByteBuf buf = channel.alloc().buffer(bytes.length + 4);
        buf.writeIntLE(bytes.length);
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);
    }

    public void sendProto(Message proto, Channel c) {
        byte[] bytes = proto.toByteArray();
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

        // set outer packet
        General.FvPacket replyOuter = General.FvPacket.newBuilder()
                                          .setType(General.FvPacketType.HANDSHAKE)
                                          .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                                          .build();
        sendProto(replyOuter);
        return true;
    }

    public void onNewUser(Control.NewUser user_packet) {
        if (UsersData.getUser(user_packet.getUsername()) == null) {
            UsersData.addUser(user_packet.getUsername(), channel);
            Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                                              .setType(Control.ServerMessageType.SUCCESS)
                                              .setSuccess(true)
                                              .build();
            General.FvPacket replyOuter =
                General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SERVER_MESSAGE)
                    .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                    .build();
            sendProto(replyOuter);
            Control.UserList userListProto =
                Control.UserList.newBuilder().addAllUsers(UsersData.keySet()).build();
            General.FvPacket listOuter =
                General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.USER_LIST)
                    .setInnerPacket(ByteString.copyFrom(userListProto.toByteArray()))
                    .build();

            for (Channel target : UsersData.values()) {
                sendProto(listOuter, target);
            }
            selfName = user_packet.getUsername();
        } else {
            Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                                              .setType(Control.ServerMessageType.NAME_TAKEN)
                                              .setSuccess(false)
                                              .build();
            General.FvPacket replyOuter =
                General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SERVER_MESSAGE)
                    .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                    .build();
            sendProto(replyOuter);
        }
    }

    public void onHeartbeat(Control.Heartbeat heartbeat_in) {
        lastHeartbeat = new Date().getTime();
        Control.Heartbeat heartbeat =
            Control.Heartbeat.newBuilder().setUtcTime(lastHeartbeat).build();

        General.FvPacket replyOuter =
            General.FvPacket.newBuilder()
                .setType(General.FvPacketType.HEARTBEAT)
                .setInnerPacket(ByteString.copyFrom(heartbeat.toByteArray()))
                .build();

        sendProto(replyOuter);
    }

    public void onSessionRequest(Control.SessionRequest sr){
        Control.ServerMessage reply;
        General.FvPacket replyOuter;

        System.out.println("Session Request packet");

        // if user not found
        if(!UsersData.keySet().contains(sr.getName())){
            reply = Control.ServerMessage.newBuilder()
                .setType(Control.ServerMessageType.USER_NOT_FOUND)
                .setSuccess(false)
                .build();
            
            // set outer packet
            replyOuter = General.FvPacket.newBuilder()
                .setType(General.FvPacketType.SERVER_MESSAGE)
                .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                .build();

            sendProto(replyOuter);

            return;
        }else{
            for(SessionData session : UsersData.getSessionList()){
                //if user is already hosting a session
                if(session.getHostUser().equals(sr.getName())){

                    // send the host the same ses req packet
                    UsersData.getUser(sr.getName());
            
                    // set outer packet
                    replyOuter = General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.SESSION_REQUEST)
                        .setInnerPacket(ByteString.copyFrom(sr.toByteArray()))
                        .build();
                    
                    // send to host
                    sendProto(replyOuter, UsersData.getUser(sr.getName()));

                    //add current user to requested user's session
                    session.addClientUser(selfName);
                    
                    //user is available: send session starting to requester
                    reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.SESSION_STARTING)
                        .setSuccess(true)
                        .build();
            
                    // set outer packet
                    replyOuter = General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.SERVER_MESSAGE)
                        .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                        .build();

                    sendProto(replyOuter);

                    // send new user video params
                    replyOuter = General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.SERVER_MESSAGE)
                        .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                        .build();

                    sendProto(replyOuter);

                    return;

                //if user is unavailable
                }else if(session.getClientList().contains(sr.getName())){
                    reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.USER_ALREADY_IN_SESSION)
                        .setSuccess(false)
                        .build();
            
                    // set outer packet
                    replyOuter = General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.SERVER_MESSAGE)
                        .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                        .build();

                    sendProto(replyOuter);

                    return;
                }
            }

            //user is available: send session starting to requester
            reply = Control.ServerMessage.newBuilder()
                .setType(Control.ServerMessageType.SESSION_STARTING)
                .setSuccess(true)
                .build();
    
            // set outer packet
            replyOuter = General.FvPacket.newBuilder()
                .setType(General.FvPacketType.SERVER_MESSAGE)
                .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                .build();

            sendProto(replyOuter);
            
            UsersData.getSessionList().add(new SessionData(sr.getName(), selfName));
        }       
    }

    public void onSessionClose(Control.SessionClose sc){
        Control.ServerMessage reply;
        General.FvPacket replyOuter;
        SessionData toRemove = null;

        for(SessionData session : UsersData.getSessionList()){
            // if from host
            if(session.getHostUser().equals(selfName)){
                
                // send sessionclose to all clients
                replyOuter = General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SESSION_CLOSE)
                    .setInnerPacket(ByteString.copyFrom(sc.toByteArray()))
                    .build();

                for(String user : session.getClientList()){
                    sendProto(replyOuter, UsersData.getUser(user));
                }

                // ica n just get rid of the session right dw about client list cuz
                // that garbo is only known about in the sessiondata
                toRemove = session;
                
            }else if(session.getClientList().contains(selfName)){ // if from client
                session.getClientList().remove(selfName);

                // send success message
                reply = Control.ServerMessage.newBuilder()
                    .setType(Control.ServerMessageType.SUCCESS)
                    .setSuccess(true)
                    .build();
                    
                replyOuter = General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SERVER_MESSAGE)
                    .setInnerPacket(ByteString.copyFrom(sc.toByteArray()))
                    .build();

                return;
            }
        }

        if(toRemove != null){
            UsersData.getSessionList().remove(toRemove);
        }
    }

    public void onVideoParams(Session.VideoParams vp){
        for(SessionData session : UsersData.getSessionList()){
            if(session.getHostUser().equals(selfName)){
                // store params
                session.setVideoParams(vp);

                // must send clients the params, normally should be just 1
                General.FvPacket replyOuter = General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.VIDEO_PARAMS)
                    .setInnerPacket(ByteString.copyFrom(vp.toByteArray()))
                    .build();

                for(String user : session.getClientList()){
                    sendProto(replyOuter, UsersData.getUser(user));
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
            case NEW_USER:
                Control.NewUser user_packet =
                    Control.NewUser.parseFrom(outer_packet.getInnerPacket());
                System.out.println("New User requested name: " + user_packet.getUsername());
                onNewUser(user_packet);
                break;
            case SESSION_REQUEST:
                onSessionRequest(Control.SessionRequest.parseFrom(outer_packet.getInnerPacket()));
                break;
            case SESSION_CLOSE:
                Control.SessionClose sc = Control.SessionClose.parseFrom(outer_packet.getInnerPacket());

                System.out.println("Session Close packet");

                onSessionClose(sc);
                break;
            case HEARTBEAT:
                System.out.println("Heartbeat packet");
                Control.Heartbeat hb = Control.Heartbeat.parseFrom(outer_packet.getInnerPacket());
                System.out.println("Timestamp: " + hb.getUtcTime());
                onHeartbeat(hb);
                break;
            case VIDEO_PARAMS: 
                Session.VideoParams vp = Session.VideoParams.parseFrom(outer_packet.getInnerPacket());
                
                onVideoParams(vp);
                break;
            case DATA:
                for(SessionData session : UsersData.getSessionList()){
                    if(session.getHostUser().equals(selfName)){ //
                        for(String user : session.getClientList()){// send to all users
                            sendProto(outer_packet, UsersData.getUser(user));
                        }
                    }
                }

                break;
            case CONTROL_INPUT:
                for(SessionData session : UsersData.getSessionList()){
                    if(session.getClientList().contains(selfName)){//find session that sender is in
                        for(String user : UsersData.keySet()){
                            sendProto(outer_packet, UsersData.getUser(user));
                        }
                    }
                }

                break;
            default:
                System.out.println("Protocol not defined");
                break;
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buffer = (ByteBuf) msg;
        byte[] raw_packet = new byte[buffer.readableBytes()];
        buffer.readBytes(raw_packet);
        mgr.parseData(raw_packet);
        buffer.release();

        if (mgr.hasPacket()) {
            try {
                handlePacket(mgr.nextPacket());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        channel = ctx.channel();
        lastHeartbeat = (new Date()).getTime();
        heartbeatThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long timeMs = (new Date()).getTime();
                    if (timeMs - getLastHeartbeat() > 7000) {
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
        General.FvPacket replyOuter;
        SessionData toRemove = null;

        for(SessionData session : UsersData.getSessionList()){
            // from host
            if(session.getHostUser().equals(selfName)){

                Control.SessionClose sc = Control.SessionClose.newBuilder()
                    .setReason("Host Disconnected")
                    .build();
                
                // send sessionclose to all clients
                replyOuter = General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SESSION_CLOSE)
                    .setInnerPacket(ByteString.copyFrom(sc.toByteArray()))
                    .build();

                for(String user : session.getClientList()){
                    sendProto(replyOuter, UsersData.getUser(user));
                }

                toRemove = session;
                
            }
        }

        // remove session
        if(toRemove != null){
            UsersData.getSessionList().remove(toRemove);
        }


        UsersData.remove(selfName);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        removeSelf();
        cause.printStackTrace();
        ctx.close();
    }
}
