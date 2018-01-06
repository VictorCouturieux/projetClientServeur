package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadClient extends Thread {

    private ServerSocket servSock = null;
    private Socket sockComm = null;
    private ThreadSender threadSender;

    private ListFilesClient lfc = null;

    private InputStream ins = null;
    private OutputStream outs = null;

    ObjectInputStream sockIn = null;
    ObjectOutputStream sockOs = null;

    public ThreadClient(ServerSocket servSock, ListFilesClient lfc) {
        this.servSock = servSock;
        this.lfc = lfc;
    }

    public ListFilesClient getLfc() { return lfc; }
    public void setLfc(ListFilesClient lfc) { this.lfc = lfc; }

    public void run() {
        try {
            while (true){
                sockComm = servSock.accept();
                threadSender = new ThreadSender(lfc);
                threadSender.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (sockComm != null)
                    sockComm.close();
            }
            catch(IOException e) {
                e.printStackTrace();
                System.out.println("Erreur IO2");
            }
        }
    }


}
