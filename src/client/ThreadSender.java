package client;

import java.io.*;
import java.net.*;

public class ThreadSender extends Thread {

    private Socket sockComm = null;
    private ObjectInputStream sockIn = null;

    private ListFilesClient lfc = null;

    private String pathFile;

    DatagramSocket datagramSocketComm = null;

    DatagramPacket pkRequeteSend;
    byte[] bufRequete;

    RandomAccessFile outFile = null;

    public ThreadSender(Socket sockComm, ListFilesClient lfc , String pathFile) {
        this.sockComm = sockComm;
        this.lfc = lfc;
        this.pathFile = pathFile;
    }

    public ListFilesClient getLfc() { return lfc; }
    public void setLfc(ListFilesClient lfc) { this.lfc = lfc; }

    public void run() {
        try {
            sockIn = new ObjectInputStream(new BufferedInputStream(sockComm.getInputStream()));
            String info = sockIn.readUTF();

            outFile = new RandomAccessFile( pathFile + info.split(":")[1],"r" );

            datagramSocketComm = new DatagramSocket();
            InetAddress ip = InetAddress.getByName("");

            bufRequete = new byte[1024];
            int amount;
            while ((amount = outFile.read(bufRequete,
                    Integer.parseInt(info.split(":")[3]),
                    Integer.parseInt(info.split(":")[4]) - Integer.parseInt(info.split(":")[3]) )) != 0){



                pkRequeteSend = new DatagramPacket(bufRequete, bufRequete.length, ip,  Integer.parseInt(info.split(":")[0]));
                datagramSocketComm.send(pkRequeteSend);
//                pkRequeteSend.setData(bufRequete);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sockIn != null)
                    sockIn.close();
                if (sockComm != null)
                    sockComm.close();
            }
            catch(IOException e) {
                System.out.println("Erreur IO");
            }
        }
    }
}
