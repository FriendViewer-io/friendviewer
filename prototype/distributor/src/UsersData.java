package prototype.distributor;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.HashMap;

public class UsersData {
    private static HashMap<String, Channel> users = new HashMap<String, Channel>();
    public synchronized static Channel getUser(String name) {
        return users.get(name);
    }
    public synchronized static void addUser(String name, Channel channel) {
        users.put(name, channel);
    }
    public synchronized static ArrayList<String> keySet() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.addAll(users.keySet());
        return ret;
    }
    public synchronized static ArrayList<Channel> values() {
        ArrayList<Channel> ret = new ArrayList<Channel>();
        ret.addAll(users.values());
        return ret;
    }
    public synchronized static void remove(String user) {
        users.remove(user);
    }
}
