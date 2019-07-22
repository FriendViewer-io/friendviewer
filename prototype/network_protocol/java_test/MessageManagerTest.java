package prototype.networkProtocol.javaTest;

import prototype.networkProtocol.MessageManager;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestNormalRetrieve.class, TestPartialRetrieve.class, TestNagleRetrieve.class })

public class MessageManagerTest {

}
