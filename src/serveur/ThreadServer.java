package serveur;

import client.ListFilesClient;

import java.io.*;
import java.net.Socket;

public class ThreadServer extends Thread {

    private Socket sockComm = null;

    private ListFilesServer lfs = null;

    private InputStream ins = null;
    private OutputStream outs = null;

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    ThreadServer(Socket sockComm, ListFilesServer lfs) {
        this.sockComm = sockComm;
        this.lfs = lfs;
    }

    public Socket getSockComm() {
        return sockComm;
    }
    public void setSockComm(Socket sockComm) {
        this.sockComm = sockComm;
    }

    public void run() {
        try {

            System.out.println(getVert() + "Debut de connection avec le client : IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort()+ getBlanc());

            ins = sockComm.getInputStream();
            outs = sockComm.getOutputStream();

            oos = new ObjectOutputStream(new BufferedOutputStream(outs));
            oos.flush();
            ois = new ObjectInputStream(new BufferedInputStream(ins));

            ListFilesClient lfc = (ListFilesClient) ois.readObject();

//            sockComm.getLocalSocketAddress();
//            sockComm.getRemoteSocketAddress();

//            lfs.addListFiles(lfc, sockComm.getRemoteSocketAddress(););





        } catch (EOFException e){
            System.out.println(getRouge() + "Fin de la connection avec le  client : " +
                    " IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort() + getBlanc());
        } catch (IOException e) {
            System.out.println("Error : " + e);
        } catch (NullPointerException e){
            System.out.println(getRouge() +"Fin de la connection avec le  client (bug NullPointerException) : " +
                    " IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort() + getBlanc());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            try {
                if (oos != null)
                    oos.close();
                if (ois != null)
                    ois.close();
                if (sockComm != null)
                    sockComm.close();
            }
            catch(IOException e) {
                e.printStackTrace();
                System.out.println("Erreur IO2");
            }
        }
    }

    public static String getRouge() {
        return "\033[31m";
    }

    public static String getVert() {
        return "\033[32m";
    }

    public static String getBlanc() {
        return "\033[0m";
    }

}
