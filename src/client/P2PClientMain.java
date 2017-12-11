package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;

import comServCli.P2PFile;
import comServCli.P2PFunctions;;

public class P2PClientMain {
	
	public static void main(String[] args) {
		
		String ipServ;
		int portServ = 0;
		ServerSocket sockConn = null;
		Socket sockComm = null;
		ObjectOutputStream sockOs = null;
		ObjectInputStream sockIn = null;

        InetAddress ipHoteHeberge = null;
		
		//Flux permettant de gérer la saisie au clavier
		BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));
		
		//On vérifie le nombre d'arguments
		if (args.length != 3) {
			System.out.println("Le nombre d'arguments est incorrect, ARRET DE L'APPLICATION");
			System.exit(1);
		}
		
		ipServ = args[0];
		
		//On transforme le numéro de port en entier si c'est possible sinon on lève une erreur
		try {
			portServ = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e) {
				System.out.println("Le numéro de port du serveur donné n'est pas valide, ARRET DE L'APPLICATION");
				System.exit(1);
		}
		
		File repository = new File(args[2]);
		
		if (!repository.isDirectory()) {
			System.out.println("Le chemin spécifié n'est pas un répertoire, ARRET DE L'APPLICATION");
			System.exit(1);
		}
		
		ListFilesClient listFiles = new ListFilesClient(repository);
		
		System.out.println(listFiles);


		try {

            printAllIP(false);
            System.out.println("\nToutes les adresse IP de ma machine (qui ne sont pas des adresses de bouclage) : " );

			//On crée la socket d'écoute du serveur
			sockConn = new ServerSocket(0);
			
			//On crée la socket qui se connecte au serveur
			sockComm = new Socket(ipServ, portServ);
			
			//On instancie les flux
			sockOs = new ObjectOutputStream(new BufferedOutputStream(sockComm.getOutputStream()));
			sockIn = new ObjectInputStream(new BufferedInputStream(sockComm.getInputStream()));
			
			sockOs.writeObject(listFiles);
			sockOs.flush();
			
			String saisie;
			ArrayList<P2PFile> currentSearch;
			
			do {
				
				System.out.println("-->");
				
				saisie = clavier.readLine();
				
				if (saisie.length() != 0) {
					Request requete = new Request(saisie);
					if (requete.getCommande() == "local") {
						System.out.println(listFiles.toString());
					} else {
						sockOs.writeObject(requete);
					}
					
					switch (requete.getCommande()) {
					case "list":
						currentSearch = (ArrayList<P2PFile>)sockIn.readObject();
						if (currentSearch.isEmpty()) {
							System.out.println("La liste des résultats est vide");
						} else {
							P2PFunctions.printSearch(currentSearch);
						}
						break;
						
					case "help":
						String reponse = sockIn.readUTF();
						System.out.println(reponse);
						break;
						
					case "search":
						currentSearch = (ArrayList<P2PFile>)sockIn.readObject();
						P2PFunctions.printSearch(currentSearch);
						break;

					default:
						break;
					}
				}
			} while (saisie.length() != 0);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

    private static void printAllIP(boolean loopBack) throws SocketException {
        Enumeration<NetworkInterface> en = null;
        // Returns all the interfaces on this machine.
        try{
            en = NetworkInterface.getNetworkInterfaces();
        }
        catch(SocketException e){
//            System.out.println("SocketException lev�e");
            e.printStackTrace();
            throw new SocketException("SocketException levée");
        }
        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            //Convenience method to return an Enumeration with all or a subset of the InetAddresses bound to this network interface.
            Enumeration<InetAddress> en2 = i.getInetAddresses();

            ArrayList<InetAddress> listIP = null;
            while(en2.hasMoreElements()) {
                InetAddress addr = en2.nextElement();
                if (addr.isLoopbackAddress() == loopBack) {
                    // pour n'afficher que les IP qui ne sont pas des adresses de lien local
                    //if (!addr.isLinkLocalAddress()){}

                    // pour n'afficher que les adresses IPv4
                    if (addr instanceof Inet4Address){
                        System.out.println("\t" + addr.getHostAddress());
//                        listIP.add(addr);
						if (Objects.equals(addr.getHostAddress(), "127.0.0.1")){
							throw new ExceptionInInitializerError("Vous n'avez pas d'adresse IP");
						}
                    }
                }

            }
//            return listIP;
        }
    }

}
