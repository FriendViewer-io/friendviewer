package prototype.network_protocol;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MessageManager {

    LinkedList<byte[]> packets = new LinkedList<byte[]>();
    private ByteBuffer bytes = ByteBuffer.allocate(0);

    public MessageManager(){

    }

    public MessageManager(byte[] data){
        parseData(data);
    }

    public boolean hasPacket(){
        return !packets.isEmpty();
    }

    public int nextPacketLength(){
        if (hasPacket()) {
            return packets.peek().length;
        }
        return -1;
    }

    public byte[] nextPacket(){
        if (packets.isEmpty()){
            return null;
        }
        else{
            return packets.poll();
        }
    }

    private int unreadBytes(ByteBuffer buf){
        return buf.limit() - buf.position();
    }

    public void parseData(byte[] data){
        ByteBuffer data_buf = ByteBuffer.allocate(data.length + unreadBytes(bytes));
        data_buf.put(bytes).put(data);

        if (data_buf.position() < 4){
            bytes = ByteBuffer.allocate(unreadBytes(data_buf));
            bytes.put(data_buf);
            return;
        }

        data_buf.flip();
        int length = data_buf.getInt();
        while (length <= unreadBytes(data_buf)){
            byte[] new_packet = new byte[length];
            data_buf.get(new_packet);
            packets.add(new_packet);
            if (unreadBytes(data_buf) < 4){
                bytes = ByteBuffer.allocate(unreadBytes(data_buf));
                bytes.put(data_buf);
                return;
            }
            length = data_buf.getInt();
        }

        bytes = ByteBuffer.allocate(unreadBytes(data_buf) + 4);
        bytes.putInt(length);
        bytes.put(data_buf);
    }

}
