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

public class TestNagleRetrieve {
    @Test
    public void testAllChunks() {
        MessageManager mgr = new MessageManager();
        byte[] n1 = Util.generateArray(0);
        byte[] n2 = Util.generateArray(255);
        byte[] n3 = Util.generateArray(0);
        byte[] n4 = Util.generateArray(3000);
        byte[] data = Util.merge(Util.merge(n1, n2), Util.merge(n3, n4));
        byte[] out_pkt;

        n1 = Arrays.copyOfRange(n1, 4, n1.length);
        n2 = Arrays.copyOfRange(n2, 4, n2.length);
        n3 = Arrays.copyOfRange(n3, 4, n3.length);
        n4 = Arrays.copyOfRange(n4, 4, n4.length);

        mgr.parseData(data);

        assertEquals(0, mgr.nextPacketLength());
        assertNotNull(out_pkt = mgr.nextPacket());
        assertArrayEquals(n1, out_pkt);

        assertEquals(255, mgr.nextPacketLength());
        assertNotNull(out_pkt = mgr.nextPacket());
        assertArrayEquals(n2, out_pkt);

        assertEquals(0, mgr.nextPacketLength());
        assertNotNull(out_pkt = mgr.nextPacket());
        assertArrayEquals(n3, out_pkt);

        assertEquals(3000, mgr.nextPacketLength());
        assertNotNull(out_pkt = mgr.nextPacket());
        assertArrayEquals(n4, out_pkt);

        assertNull(mgr.nextPacket());
    }
}
