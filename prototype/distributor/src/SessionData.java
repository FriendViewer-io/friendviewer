package prototype.distributor;

import prototype.protobuf.Session;
import java.util.ArrayList;

public class SessionData {
    private String hostUser;
    private ArrayList<String> clientUsers;
    /*private int videoWidth, videoHeight, frameNum, frameDenom; // numerator, denominator
    private byte[] ppsSps;*/
    private Session.VideoParams videoParams;
    private Session.AudioParams audioParams;

    // might not want to require a client user? seems ok for prototype
    public SessionData(String hostUser, String clientUser) {
        this.hostUser = hostUser;

        clientUsers = new ArrayList<String>();

        clientUsers.add(clientUser);
    }

    public String getHostUser() {
        return hostUser;
    }

    public ArrayList<String> getClientList() {
        return clientUsers;
    }

    public synchronized void addClientUser(String clientUserName) { clientUsers.add(clientUserName); }

    public void setVideoParams(Session.VideoParams videoParams) {
        this.videoParams = videoParams;
    }

    public Session.VideoParams getVideoParams() {
        return videoParams;
    }

    public void setAudioParams(Session.AudioParams audioParams) {
        this.audioParams = audioParams;
    }

    public Session.AudioParams getAudioParams() {
        return audioParams;
    }
}