package prototype.networkProtocol.javaTest;

import java.nio.ByteBuffer;

public class Util {
    public static byte[] generateArray(int length) {
        byte[] len_bytes = ByteBuffer.allocate(4).putInt(length).array();
        byte[] ret = new byte[length + 4];
        ret[0] = len_bytes[0];
        ret[1] = len_bytes[1];
        ret[2] = len_bytes[2];
        ret[3] = len_bytes[3];
        for (int i = 0; i < length; i++) {
            ret[i + 4] = (byte) i;
        }
        return ret;
    }

    public static byte[] merge(byte[] b1, byte[] b2) {
        byte[] ret = new byte[b1.length + b2.length];
        for (int i = 0; i < b1.length; i++) {
            ret[i] = b1[i];
        }
        for (int i = 0; i < b2.length; i++) {
            ret[i + b1.length] = b2[i];
        }
        return ret;
    }
}
