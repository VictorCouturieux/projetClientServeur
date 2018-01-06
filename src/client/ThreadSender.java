package client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadSender extends Thread {

//    private Socket sockComm = null;

    private ListFilesClient lfc = null;

    DatagramSocket pksockComm = null;
    DatagramPacket pkRequete;

    byte[] bufRequete;

    DatagramPacket pkRequeteSend;
    String requete;
    byte[] bufRequeteSend;


    public ThreadSender(ListFilesClient lfc) {
//        this.sockComm = sockComm;
        this.lfc = lfc;
    }

    public ListFilesClient getLfc() { return lfc; }
    public void setLfc(ListFilesClient lfc) { this.lfc = lfc; }

    public void run() {


    }
}
