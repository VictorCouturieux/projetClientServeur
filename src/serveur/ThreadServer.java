package serveur;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import client.ListFilesClient;
import client.Request;
import comServCli.P2PFile;
import comServCli.P2PFunctions;;

public class ThreadServer extends Thread {

    private Socket sockComm = null;

    private ListFilesServer lfs = null;

    private InputStream ins = null;
    private OutputStream outs = null;

    ObjectInputStream sockIn = null;
    ObjectOutputStream sockOs = null;

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

            System.out.println("Debut de connection avec le client : IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort());

            ins = sockComm.getInputStream();
            outs = sockComm.getOutputStream();

            sockOs = new ObjectOutputStream(new BufferedOutputStream(outs));
            sockOs.flush();
            sockIn = new ObjectInputStream(new BufferedInputStream(ins));

            ListFilesClient lfc = (ListFilesClient)sockIn.readObject();
            lfs.addListFiles(lfc, sockComm.getRemoteSocketAddress());
            
            System.out.print(lfs.toString());

            Request requete;
            P2PFile[] currentSearchArray = new P2PFile[0];

            while (true){
                requete = (Request) sockIn.readObject();
                String commande = requete.getCommande();

                switch (commande) {
                    case "help":
                        sockOs.writeUTF(" search <pattern> \n get <num> \n list \n local list \n quit");
                        sockOs.flush();
                        break;

                    case "search":
                        String motif = requete.getArg();
                        HashMap<P2PFile, ArrayList<SocketAddress>> listFiles = lfs.getListFiles();

                        ArrayList<P2PFile> currentSearch = new ArrayList<P2PFile>();

                        for (Entry<P2PFile, ArrayList<SocketAddress>> mapentry : listFiles.entrySet()) {
                            P2PFile file = mapentry.getKey();
                            String nameFile = file.getNameFile();
                            if (nameFile.contains(motif)) {
                                currentSearch.add(file);
                            }
//                            if (nameFile.indexOf(motif) != -1) {
//                                currentSearch.add(file);
//                            }
                        }

                        currentSearchArray = new P2PFile[currentSearch.size()];
                        currentSearch.toArray(currentSearchArray);

                        sockOs.writeObject(currentSearchArray);
                        sockOs.flush();
                        break;


                    case "list":
                        if (currentSearchArray.length == 0) {
                            sockOs.writeUTF("La liste des résultats est vide");
                            sockOs.flush();
                        } else {
                            sockOs.writeUTF(P2PFunctions.printSearch(currentSearchArray));
                            sockOs.flush();
                        }
                        break;

                    case "quit":
                    	lfs.deleteClient(lfc, sockComm.getRemoteSocketAddress());
                        break;

                    case "get":

                        String valide = sockIn.readUTF();
                        if (Objects.equals(valide, "valide")){
                            int num = Integer.parseInt(requete.getArg());
                            P2PFile downThisFile = currentSearchArray[num-1];

                            listFiles = lfs.getListFiles();

                            ArrayList<SocketAddress> listAdress = listFiles.get(downThisFile);

                            SocketAddress[] tblListAdress = new SocketAddress[listAdress.size()];
                            listAdress.toArray(tblListAdress);

                            sockOs.writeObject(tblListAdress);
                            sockOs.flush();
                        }
                        break;

                    default:
                        break;
                }
            } 
        } 
        catch (EOFException e){
            System.out.println("Fin de la connection avec le  client : " +
                    " IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort());
            System.out.println(lfs.toString());
        } catch (IOException e) {
            System.out.println("Error : " + e);
        } catch (NullPointerException e){
            System.out.println("Fin de la connection avec le  client (bug NullPointerException) : " +
                    " IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort());
            System.out.println(lfs.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            try {
                if (sockOs != null)
                    sockOs.close();
                if (sockIn != null)
                    sockIn.close();
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
