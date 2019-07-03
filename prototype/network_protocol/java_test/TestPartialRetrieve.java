package prototype.networkProtocol.javaTest;

import org.junit.Test;

import prototype.networkProtocol.MessageManager;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class TestPartialRetrieve {
    @Test
    public void testAllChunks() {
        byte[] data1, data2;
        byte[] data = Util.merge(data1 = Util.generateArray(60), data2 = Util.generateArray(70));
        data1 = Arrays.copyOfRange(data1, 4, data1.length);
        data2 = Arrays.copyOfRange(data2, 4, data2.length);

        byte[] split1 = Arrays.copyOfRange(data, 0, 33);
        byte[] split2 = Arrays.copyOfRange(data, 33, 65);
        byte[] split3 = Arrays.copyOfRange(data, 65, data.length);

        byte[] expected = Arrays.copyOfRange(data, 4, 254);
        byte[] out_pkt;
        MessageManager mgr = new MessageManager();

        mgr.parseData(split1);
        assertEquals(-1, mgr.nextPacketLength());
        mgr.parseData(split2);
        assertEquals(60, mgr.nextPacketLength());
        mgr.parseData(split3);
        assertEquals(60, mgr.nextPacketLength());

        assertNotNull(out_pkt = mgr.nextPacket());
        assertEquals(60, out_pkt.length);
        assertEquals(70, mgr.nextPacketLength());
        assertArrayEquals(data1, out_pkt);

        assertNotNull(out_pkt = mgr.nextPacket());
        assertEquals(70, out_pkt.length);
        assertEquals(-1, mgr.nextPacketLength());
        assertArrayEquals(data2, out_pkt);
    }
}
