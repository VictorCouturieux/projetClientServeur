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

	/**
	 * Initialisation de toutes les variables qui seront utilisées dans ce programme
	 */
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

        	//Affichage d'un message pour montrer que la connexion est initialisée
            System.out.println("Debut de connection avec le client : IP : " + sockComm.getInetAddress() +  " | numero de port :" + sockComm.getPort());

            //Initialisation des flux
            ins = sockComm.getInputStream();
            outs = sockComm.getOutputStream();

            sockOs = new ObjectOutputStream(new BufferedOutputStream(outs));
            sockOs.flush();
            sockIn = new ObjectInputStream(new BufferedInputStream(ins));

            //Réception de la liste des fichiers et du numéro de port de la socket d'écoute du client qui se connecte au serveur
            ListFilesClient lfc = (ListFilesClient)sockIn.readObject();
            int portSocketServeurNewClient = sockIn.readInt();
            
            //Ajout des informations dans la liste des fichiers du serveur
            lfs.addListFiles(lfc, sockComm.getRemoteSocketAddress(), portSocketServeurNewClient);
            
            System.out.print(lfs.toString());

            Request requete;
            P2PFile[] currentSearchArray = new P2PFile[0];

            /**
             * Traitement successif de toutes les commandes qu'enverra le client tant que celui-ci ne ferme pas son application
             */
            while (true){
            	//Réception de la requête
                requete = (Request) sockIn.readObject();
                String commande = requete.getCommande();

                /**
                 * Traitement de la commande en fonction du premier argument de celle-ci
                 * 
                 * "help" : Création et envoi du message d'aide
                 * "search" : Pour effectuer la recherche, on récupère d'abord la HashMap de possession des fichiers du serveur. 
                 * 			  On crée ensuite une ArrayList permettant d'accueillir les fichiers correspondant à la recherche.
                 * 			  On parcourt ensuite la HashMap en parcourant les clés et on regarde si cette clé contient le motif et si c'est le cas, on ajoute le fichier dans l'ArrayList contenant les résultats de la recherche.
                 * "list" : Envoie au client l'ensemble de la liste des fichiers qu'ils possèdent
                 * "quit" : Gestion de la déconnexion du client en mettant à jour la liste des fichiers possédé par le serveur
                 * "get" : Envoi des informations nécessaire au téléchargement pour le client
                 */
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

                        /**
                         * attente de la reception d'une requete permettant la validation du telechargement du fichier
                         */
                        String valide = sockIn.readUTF();
                        if (Objects.equals(valide, "valide")){
                            //selectionne le fichier a telechargeer
                            int num = Integer.parseInt(requete.getArg());
                            P2PFile downThisFile = currentSearchArray[num-1];
                            listFiles = lfs.getListFiles();
                            ArrayList<SocketAddress> listAdress = listFiles.get(downThisFile);

                            //recuperation de la liste des adresse qui possede le fichier a telecharger
                            SocketAddress[] tblListAdress = new SocketAddress[listAdress.size()];
                            listAdress.toArray(tblListAdress);

                            //envoie de la liste des adresse
                            sockOs.writeObject(tblListAdress);
                            sockOs.flush();

                            //envoie de la liste des port de socketServeur crespondant a la liste d'adresse precedament envoyé.
                            int[] tblPortSocketServeur = lfs.createArrayNumberPort(listAdress);
                            sockOs.writeObject(tblPortSocketServeur);
                            sockOs.flush();

                            //mettre a jour la map de fichier
                            lfs.addFilesCreated(downThisFile, sockComm.getRemoteSocketAddress());

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
