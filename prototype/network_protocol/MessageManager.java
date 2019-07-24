package prototype.networkProtocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

public class MessageManager {
    LinkedList<byte[]> packets = new LinkedList<byte[]>();
    private ByteBuffer message_buffer = ByteBuffer.allocate(4);
    private boolean reading_length = true;

    public MessageManager() {}

    public boolean hasPacket() {
        return !packets.isEmpty();
    }

    public int nextPacketLength() {
        if (hasPacket()) {
            return packets.peek().length;
        }
        return -1;
    }

    public byte[] nextPacket() {
        if (packets.isEmpty()) {
            return null;
        } else {
            return packets.poll();
        }
    }

    private int unreadBytes(ByteBuffer buf) {
        return buf.limit() - buf.position();
    }

    public void parseData(byte[] data) {
        ByteBuffer data_buffer = ByteBuffer.allocate(data.length);
        data_buffer.put(data);
        data_buffer.position(0);
        while (unreadBytes(data_buffer) > 0) {
            if (reading_length) {
                while (unreadBytes(data_buffer) > 0 && unreadBytes(message_buffer) > 0) {
                    message_buffer.put(data_buffer.get());
                }

                if (unreadBytes(message_buffer) == 0) {
                    reading_length = false;
                    message_buffer.position(0);
                    message_buffer.order(ByteOrder.LITTLE_ENDIAN);
                    int next_len = message_buffer.getInt();
                    // Safeguard against zero-length messages
                    if (next_len == 0) {
                        packets.add(new byte[0]);
                        reading_length = true;
                        message_buffer = ByteBuffer.allocate(4);
                    } else {
                        message_buffer = ByteBuffer.allocate(next_len);
                    }
                }
            } else {
                while (unreadBytes(data_buffer) > 0 && unreadBytes(message_buffer) > 0) {
                    message_buffer.put(data_buffer.get());
                }
                if (unreadBytes(message_buffer) == 0) {
                    reading_length = true;
                    packets.add(message_buffer.array());
                    message_buffer = ByteBuffer.allocate(4);
                }
            }
        }
    }
}
