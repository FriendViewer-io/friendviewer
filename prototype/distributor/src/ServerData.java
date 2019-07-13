package prototype.distributor;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerData {
    public static HashMap<String, Channel> users = new HashMap<String, Channel>();
}
