package client;

import comServCli.P2PFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.*;

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
//            sockComm = new Socket(address.getHostAddress(), port);
//            sockOs = new ObjectOutputStream(new BufferedOutputStream(sockComm.getOutputStream()));
//            sockOs.flush();


            datagramSocketComm = new DatagramSocket();
            int portUDP = datagramSocketComm.getLocalPort();
            //envoi des infos via socket TCP
            System.out.println(address.getHostAddress() + ":" + portTCP + ":" + portUDP + ":" +  file.getNameFile() + ":" + file.getSizeFile() + ":" + preMorceauInclu + ":" + derMorceauExclu);

            System.out.println("requete envoyer : " + portUDP + ":" +  file.getNameFile() + ":" + file.getSizeFile() + ":" + preMorceauInclu + ":" + derMorceauExclu);
            bufRequete = new byte[1024];

            long totalOctet = 0;

            if (derMorceauExclu - preMorceauInclu != 0){
                do{
                    pkRequeteReceive = new DatagramPacket(bufRequete, bufRequete.length);
                    datagramSocketComm.receive(pkRequeteReceive);

                    fileCreated.write(bufRequete, preMorceauInclu, derMorceauExclu - preMorceauInclu);

                    totalOctet += bufRequete.length;
                }while (totalOctet <= derMorceauExclu - preMorceauInclu);
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
