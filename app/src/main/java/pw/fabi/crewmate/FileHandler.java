package pw.fabi.crewmate;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.spi.CharsetProvider;

public class FileHandler {

    File file;

    public FileHandler(){

    }

    public FileHandler openFile(String fileStr){
        file = new File(fileStr);
        return this;
    }

    public boolean replaceFile(String name, String ip, short port) {
        try {
            long fileSize = file.length();


            byte[] allBytes = new byte[(int) fileSize];

            InputStream istr = new FileInputStream(file);
               istr.read(allBytes);

               String dataDebug = new String(allBytes, StandardCharsets.UTF_8);


               //Log.d("CREWMATE", dataDebug);

            // Clear file

            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();


            OutputStream ostr = new FileOutputStream(file);
            DataOutputStream outd = new DataOutputStream(ostr);
            Integer padding = 0;
            byte[] padByte = {0,0,0,8};
            byte[] ippad = {11};
            byte[] svrc = {17};
            // Server master

            outd.writeInt(0); // Padding
            outd.write(name.getBytes().length); // length name
            outd.writeBytes(name); // Name
            outd.write(ip.getBytes().length); // length ip
            outd.writeBytes(ip); // ip
            outd.write(1); // Server count
            outd.writeInt((name + "-Master-1").getBytes().length);
            // Server Info
            outd.writeBytes(name + "-Master-1");// First Server
            outd.write(InetAddress.getByName(ip).getAddress()); // Ip address
            outd.writeShort(port); // port
            outd.writeInt(0); // Padding

            outd.close();

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
