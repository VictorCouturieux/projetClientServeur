package client;

import comServCli.P2PFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class ThreadReceiver extends Thread  {

    private InetAddress address;
    private int port;

    private P2PFile file;

    private int preMorceauInclu;
     private int derMorceauExclu;

    Socket sockComm = null;
    ObjectOutputStream sockOs = null;

    DatagramPacket pkRequete = null;
    DatagramPacket pkRequeteResseve = null;

    byte[] bufRequete;

    public ThreadReceiver(InetAddress address, int port, P2PFile file, int preMorceauInclu, int derMorceauExclu) {
        this.address = address;
        this.port = port;
        this.file = file;
        this.preMorceauInclu = preMorceauInclu;
        this.derMorceauExclu = derMorceauExclu;
    }

    public void run() {
        try {
            sockComm = new Socket(address, port);
            sockOs = new ObjectOutputStream(new BufferedOutputStream(sockComm.getOutputStream()));
            sockOs.flush();





        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sockOs != null){
                    sockOs.close();
                }
                if (sockComm != null){
                    sockComm.close();
                }
            }
            catch(IOException e) {
                System.out.println("Erreur IO");
            }
        }
    }

}
