package prototype.distributor;

import prototype.protobuf.Control;
import prototype.protobuf.General;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", 8080);
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        General.FvPacket.Builder packet_Builder = General.FvPacket.newBuilder();
        packet_Builder.setType(General.FvPacketType.UNSPECIFIED);

        //Inner Packer
        Control.ServerMessage.Builder message_builder = Control.ServerMessage.newBuilder();
        message_builder.setType(Control.ServerMessageType.SUCCESS);
        message_builder.setSuccess(true);
        message_builder.setMessage("This is a message");

        packet_Builder.setInnerPacket(message_builder.build().toByteString());

        //Deconstructing the packet and retrieving data
        General.FvPacket outer_packer = packet_Builder.build();
        System.out.println(outer_packer.getType());

        Control.ServerMessage inner_packer = Control.ServerMessage.parseFrom(outer_packer.getInnerPacket());
        System.out.println(inner_packer.getMessage());

        //Sending the packet
        byte[] data = outer_packer.toByteArray();
        System.out.println(data.length);
        ByteBuffer buf = ByteBuffer.allocate(data.length + 4);
        buf.putInt(data.length);
        buf.put(data);
        out.write(buf.array());
    }
}