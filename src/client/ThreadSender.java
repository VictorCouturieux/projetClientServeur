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

//            System.out.println(info);
//            System.out.println(pathFile + info.split(":")[1]);


            outFile = new RandomAccessFile( pathFile + info.split(":")[1],"r" );

            datagramSocketComm = new DatagramSocket();

            bufRequete = new byte[1024];
            InetAddress ip = InetAddress.getByName("");
            int portUDP = Integer.parseInt(info.split(":")[0]);
            int preMorceauInclu = Integer.parseInt(info.split(":")[3]);
            int derMorceauExclu = Integer.parseInt(info.split(":")[4]);

            pkRequeteSend = new DatagramPacket(bufRequete, bufRequete.length, ip, portUDP);

            int count = 0;

//            System.out.println("debut");

            if (derMorceauExclu - preMorceauInclu > 0) {
                do {
                    outFile.seek((preMorceauInclu + count)*1024);
                    outFile.read(bufRequete);

                    pkRequeteSend.setData(bufRequete);
                    datagramSocketComm.send(pkRequeteSend);
                    count++;
                } while (count <= derMorceauExclu - preMorceauInclu);
            }
//            System.out.println("fini");

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
