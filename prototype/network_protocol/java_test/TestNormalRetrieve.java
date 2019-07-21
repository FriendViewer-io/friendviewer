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

public class TestNormalRetrieve {
    @Test
    public void testNormalDataChunk() {
        byte[] data = Util.generateArray(250);
        byte[] expected = Arrays.copyOfRange(data, 4, 254);
        byte[] out_pkt;
        MessageManager mgr = new MessageManager();
        mgr.parseData(data);
        assertEquals(250, mgr.nextPacketLength());
        assertNotNull(out_pkt = mgr.nextPacket());
        assertEquals(-1, mgr.nextPacketLength());
        assertEquals(250, out_pkt.length);
        assertArrayEquals(expected, out_pkt);
        assertNull(mgr.nextPacket());
    }

    @Test
    public void testZeroData() {
        byte[] data = Util.generateArray(0);
        byte[] expected = new byte[0];
        byte[] out_pkt;
        MessageManager mgr = new MessageManager();
        mgr.parseData(data);
        assertEquals(0, mgr.nextPacketLength());
        assertNotNull(out_pkt = mgr.nextPacket());
        assertEquals(-1, mgr.nextPacketLength());
        assertEquals(0, out_pkt.length);
        assertArrayEquals(expected, out_pkt);
        assertNull(mgr.nextPacket());
    }
}
