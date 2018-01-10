package client;

import java.io.*;
import java.net.*;

public class ThreadSender extends Thread {

    /**
     * Initialisation de toutes les variables qui seront utilisées dans ce programme
     */
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

            /**
             * attente de la resseption des informations pour la suite du programme
             */
            sockIn = new ObjectInputStream(new BufferedInputStream(sockComm.getInputStream()));
            String info = sockIn.readUTF();

//            System.out.println(info);
//            System.out.println(pathFile + info.split(":")[1]);


            /**
             * création d'un point d'acces du fichier a lire
             * et initialisation de la socket UDP pour la transmition
             */
            outFile = new RandomAccessFile( pathFile + info.split(":")[1],"r" );
            datagramSocketComm = new DatagramSocket();

            bufRequete = new byte[1024];
            InetAddress ip = InetAddress.getByName("");
            int portUDP = Integer.parseInt(info.split(":")[0]);
            int preMorceauInclu = Integer.parseInt(info.split(":")[3]);
            int derMorceauExclu = Integer.parseInt(info.split(":")[4]);

            pkRequeteSend = new DatagramPacket(bufRequete, bufRequete.length, ip, portUDP);

            int count = 0;

            /**
             * demarage de l'envoie des données du fichier
             */
            if (derMorceauExclu - preMorceauInclu > 0) {
                do {
                    /**
                     * positionnement pour la suite de la lecture du fichier
                     * puis, lecture du fichier et enregistrement des donner dans 'bufRequete'
                     */
                    outFile.seek((preMorceauInclu + count)*1024);
                    outFile.read(bufRequete);
                    /**
                     * envoi des donnée du fichier par la socket UDP
                     */
                    pkRequeteSend.setData(bufRequete);
                    datagramSocketComm.send(pkRequeteSend);

                    count++;
                } while (count <= derMorceauExclu - preMorceauInclu);
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
