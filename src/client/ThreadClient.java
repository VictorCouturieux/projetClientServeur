package client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadClient extends Thread {

    private Socket sockComm = null;

    private ListFilesClient lfc = null;

    private InputStream ins = null;
    private OutputStream outs = null;

    ObjectInputStream sockIn = null;
    ObjectOutputStream sockOs = null;

    public ThreadClient(Socket sockComm, ListFilesClient lfc) {
        this.sockComm = sockComm;
        this.lfc = lfc;
    }

    public Socket getSockComm() { return sockComm; }
    public void setSockComm(Socket sockComm) { this.sockComm = sockComm; }

    public void run() {
        
    }


}
