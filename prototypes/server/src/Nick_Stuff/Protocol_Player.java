package Nick_Stuff;

import Nick_Stuff.Protocols.Test_Protocol;
import com.google.protobuf.ByteString;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

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

    public static void main(String[]args){
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
}
