package prototype.distributor;

import io.netty.channel.Channel;
import prototype.distributor.SessionData;
import java.util.ArrayList;
import java.util.HashMap;

public class UsersData {
    private static HashMap<String, Channel> users = new HashMap<String, Channel>();
    private static ArrayList<SessionData> sessions = new ArrayList<SessionData>();

    public synchronized static Channel getUser(String name) {
        return users.get(name);
    }
    /* waht
    public synchronized static String  getUsername(Channel channel)(){
        return users.

        for (Entry<Integer, String> entry : testMap.entrySet()) {
            if (entry.getValue().equals("c")) {
                System.out.println(entry.getKey());
            }
        }s
    }*/
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
    /* no cares
    there could be a more efficient way of storing this, in the handler it can be weird or ugly */
    public static synchronized ArrayList<SessionData> getSessionList(){
        return sessions;
    }
}
