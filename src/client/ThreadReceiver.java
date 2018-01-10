package client;

import comServCli.P2PFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.*;
import java.util.Arrays;

public class ThreadReceiver extends Thread  {

    private InetAddress address;
    private int portTCP;

    private P2PFile file;
    private RandomAccessFile fileCreated;

    private int preMorceauInclu;
     private int derMorceauExclu;

    private Socket sockComm = null;
    private ObjectOutputStream sockOs = null;

    DatagramSocket datagramSocketComm = null;
    DatagramPacket pkRequeteReceive = null;

    byte[] bufRequete;

    public ThreadReceiver(InetAddress address, int portTCP, P2PFile file, RandomAccessFile fileCreated, int preMorceauInclu, int derMorceauExclu) {
        this.address = address;
        this.portTCP = portTCP;
        this.file = file;
        this.fileCreated = fileCreated;
        this.preMorceauInclu = preMorceauInclu;
        this.derMorceauExclu = derMorceauExclu;
    }

    public RandomAccessFile getFileCreated() { return fileCreated; }
    public void setFileCreated(RandomAccessFile fileCreated) { this.fileCreated = fileCreated; }

    public void run() {


        try {
            sockComm = new Socket(address.getHostAddress(), portTCP);
            sockOs = new ObjectOutputStream(new BufferedOutputStream(sockComm.getOutputStream()));
            sockOs.flush();


            datagramSocketComm = new DatagramSocket();
            int portUDP = datagramSocketComm.getLocalPort();
            //envoi des infos via socket TCP
//            System.out.println(address.getHostAddress() + ":" + portTCP + ":" + portUDP + ":" +  file.getNameFile() + ":" + file.getSizeFile() + ":" + preMorceauInclu + ":" + derMorceauExclu);

            System.out.println("requete envoyer : " + portUDP + ":" +  file.getNameFile() + ":" + file.getSizeFile() + ":" + preMorceauInclu + ":" + derMorceauExclu);
            sockOs.writeUTF(portUDP + ":" +  file.getNameFile() + ":" + file.getSizeFile() + ":" + preMorceauInclu + ":" + derMorceauExclu);
            sockOs.flush();

            bufRequete = new byte[1024];

            int count = 0;
            int amount;

            if (derMorceauExclu - preMorceauInclu > 0){
                do{
                    pkRequeteReceive = new DatagramPacket(bufRequete, bufRequete.length);
                    datagramSocketComm.receive(pkRequeteReceive);

                    fileCreated.seek((preMorceauInclu + count)*1024);

                    fileCreated.write(bufRequete);

                    System.out.println("n°" + (count +1));
                    System.out.println(((preMorceauInclu + count)*1024) + " / " +( file.getSizeFile() ) );

                    count++;

                }while (count <= derMorceauExclu - preMorceauInclu);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sockOs != null)
                    sockOs.close();
                if (sockComm != null)
                    sockComm.close();
            }
            catch(IOException e) {
                System.out.println("Erreur IO");
            }
        }
    }


}
