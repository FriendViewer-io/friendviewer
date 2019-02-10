package Nick_Stuff;

import Nick_Stuff.Protocols.AVInitHP;
import Nick_Stuff.Protocols.HostVideoPP;
import Nick_Stuff.Protocols.Test_Protocol;
import Nick_Stuff.Protocols.UserListSP;
import com.google.protobuf.ByteString;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Protocol_Player {

    public static void readData(Test_Protocol.dataChunk data){
        System.out.println("Name: " + data.getName());
        System.out.println("ID: " + data.getId());
        System.out.println("Description: " + data.getDescription());

        for (String text : data.getTextList()) {
            System.out.println(text);
        }

        byte[] image = data.getImage().toByteArray();

        try {
            File path = new File("C:\\Users\\nickz\\Desktop\\Received.jpg");
            OutputStream  out = new BufferedOutputStream(new FileOutputStream(path));
            out.write(image);
            if (out != null) out.close();
        } catch (Exception e) {

        }

    }

    public static void Test_Protocol(){
        Test_Protocol.dataChunk.Builder dataBuilder = Test_Protocol.dataChunk.newBuilder();
        dataBuilder.setName("Example 1");
        dataBuilder.setId(001);
        dataBuilder.setDescription("The very first protobuf sent. How very exciting!");

        File file = new File("C:\\Users\\nickz\\Desktop\\Ichigo.png");
        byte[] fileContents = new byte[10];
        try {
            fileContents = Files.readAllBytes(file.toPath());
        } catch(Exception e){

        }
        ByteString s = ByteString.copyFrom(fileContents);
        dataBuilder.setImage(s);

        ArrayList<String> text = new ArrayList<>();
        text.add("Paragraph 1");
        text.add("Textfield 2");
        text.add("Salaam, ALLAHU AKBAR!");
        text.add("Glory to the faithful! Death to the infidels! ALLAHU AKBAR!!!");
        text.add("pew pew pew");
        dataBuilder.addAllText(text);

        Test_Protocol.dataChunk data = dataBuilder.build();
        readData(data);
    }

    public static void user_list_protocol(){
        UserListSP.data.Builder dataBuilder = UserListSP.data.newBuilder();

        dataBuilder.setProtoId(0);
        dataBuilder.setProtoType(1);
        dataBuilder.setUtcTime(new Date().getTime());

        String[] names = {"a", "b", "c"};
        ArrayList<String> x = new ArrayList<String>(Arrays.asList(names));
        dataBuilder.addAllUsers(x);

        UserListSP.data data = dataBuilder.build();

        System.out.println(data.getProtoId());
        for (String name : data.getUsersList()){
            System.out.println(name);
        }
    }

    public static void HostVideoPP(){
        HostVideoPP.data.Builder dataBuilder = HostVideoPP.data.newBuilder();

        dataBuilder.setProtoId(0);
        dataBuilder.setProtoType(1);
        dataBuilder.setUtcTime(new Date().getTime());

        dataBuilder.setDts(2);
        dataBuilder.setPts(3);
        dataBuilder.setMonitorID(4);

        File file = new File("C:\\Users\\nickz\\Desktop\\Ichigo.png");
        byte[] fileContents = new byte[10];
        try {
            fileContents = Files.readAllBytes(file.toPath());
        } catch(Exception e){

        }
        ByteString s = ByteString.copyFrom(fileContents);
        dataBuilder.addH246VideoData(s);

        file = new File("C:\\Users\\nickz\\Desktop\\Cake.png");
        fileContents = new byte[10];
        try {
            fileContents = Files.readAllBytes(file.toPath());
        } catch(Exception e){

        }
        s = ByteString.copyFrom(fileContents);
        dataBuilder.addH246VideoData(s);

        HostVideoPP.data data = dataBuilder.build();

        System.out.println(data.getDts());
        System.out.println(data.getMonitorID());
        System.out.println(data.getPts());
        System.out.println(data.getH246VideoDataCount());

        byte[] image = data.getH246VideoData(0).toByteArray();

        try {
            File path = new File("C:\\Users\\nickz\\Desktop\\Received.jpg");
            OutputStream  out = new BufferedOutputStream(new FileOutputStream(path));
            out.write(image);
            if (out != null) out.close();
        } catch (Exception e) {

        }

        image = data.getH246VideoData(1).toByteArray();

        try {
            File path = new File("C:\\Users\\nickz\\Desktop\\Received2.jpg");
            OutputStream  out = new BufferedOutputStream(new FileOutputStream(path));
            out.write(image);
            if (out != null) out.close();
        } catch (Exception e) {

        }
    }

    public static void main(String[]args){
        AVInitHP.request.Builder dataBuilder = AVInitHP.request.newBuilder();

        dataBuilder.setProtoId(0);
        dataBuilder.setProtoType(1);
        dataBuilder.setUtcTime(new Date().getTime());

        File file = new File("C:\\Users\\nickz\\Desktop\\file.txt");
        byte[] fileContents = new byte[10];
        try {
            fileContents = Files.readAllBytes(file.toPath());
        } catch(Exception e){

        }
        ByteString s = ByteString.copyFrom(fileContents);
        dataBuilder.setPPS(s);

        //Monitor Stuff
        AVInitHP.request.monitor.Builder monitorBuilder = AVInitHP.request.monitor.newBuilder();
        monitorBuilder.setAvailability(true);
        monitorBuilder.setHeight(10);
        monitorBuilder.setWidth(10);
        dataBuilder.addMonitors(monitorBuilder.build());

        monitorBuilder = AVInitHP.request.monitor.newBuilder();
        monitorBuilder.setAvailability(false);
        monitorBuilder.setHeight(15);
        monitorBuilder.setWidth(15);
        dataBuilder.addMonitors(monitorBuilder.build());


        dataBuilder.setVidBitrate(10);
        dataBuilder.setFramerate(11);
        dataBuilder.setSamplerate(12);
        dataBuilder.setAudioBitrate(13);
        dataBuilder.setChannelcount(14);
        dataBuilder.setChannellayout(15);

        AVInitHP.request data = dataBuilder.build();

        System.out.println(data.getVidBitrate());
        System.out.println(data.getFramerate());
        System.out.println(data.getSamplerate());
        System.out.println(data.getAudioBitrate());
        System.out.println(data.getChannelcount());
        System.out.println(data.getChannellayout());

        for (AVInitHP.request.monitor x : data.getMonitorsList()){
            System.out.println(x.getAvailability());
            System.out.println("Height: " + x.getHeight());
            System.out.println("Width: " + x.getWidth());
        }
    }
}
