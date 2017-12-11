package serveur;

import client.ListFilesClient;
import client.Request;
import comServCli.P2PFile;
import comServCli.P2PFunctions;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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

            System.out.println(getVert() + "Debut de connection avec le client : IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort()+ getBlanc());

            ins = sockComm.getInputStream();
            outs = sockComm.getOutputStream();

            sockOs = new ObjectOutputStream(new BufferedOutputStream(outs));
            sockOs.flush();
            sockIn = new ObjectInputStream(new BufferedInputStream(ins));

            ListFilesClient lfc = (ListFilesClient)sockIn.readObject();
            lfs.addListFiles(lfc, sockComm.getRemoteSocketAddress());
            
            Request requete = (Request)sockIn.readObject();
           
            ArrayList<P2PFile> currentSearch = new ArrayList<P2PFile>();
            String commande = requete.getCommande();
            
            switch (commande) {
			case "list":
				if (currentSearch.isEmpty()) {
					sockOs.writeUTF("Aucun résultat disponible");
				} else {
				   System.out.println(P2PFunctions.printSearch(currentSearch));
				}
				break;
				
			case "help":
				sockOs.writeUTF(" search <pattern> \n get <num> \n list \n local list \n quit");
				break;
				
			case "search":
				String motif = requete.getArg();
				HashMap<P2PFile, ArrayList<SocketAddress>> listFiles = lfs.getListFiles();
				
				for (Entry<P2PFile, ArrayList<SocketAddress>> mapentry : listFiles.entrySet()) {
					P2PFile file = mapentry.getKey();
					String nameFile = file.getNameFile();
					if (nameFile.indexOf(motif) != -1) {
						currentSearch.add(file);
					}
				}
				break;
				
			default:
				break;
			}
        } 
        catch (EOFException e){
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
