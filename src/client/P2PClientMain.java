package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

import comServCli.P2PFile;
import comServCli.P2PFunctions;;

public class P2PClientMain {
	
	public static void main(String[] args) {
		
		/**
		 * Initialisation de toutes les variables qui seront utilisées dans le main du client
		 */
		String ipServ;
		int portServ = 0;
		ServerSocket sockConn = null;
		Socket sockComm = null;
		ObjectOutputStream sockOs = null;
		ObjectInputStream sockIn = null;
		ThreadClient tc = null;
		ThreadReceiver tr = null;

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
		
		//On lève une erreur si le troisième argument de la ligne de commande n'est pas un répertoire
		if (!repository.isDirectory()) {
			System.out.println("Le chemin spécifié n'est pas un répertoire, ARRET DE L'APPLICATION");
			System.exit(1);
		}
		
		/**
		 * On crée la liste des fichiers à partir du répertoire passé en troisième argument de la ligne de commande
		 */
		ListFilesClient listFiles = new ListFilesClient(repository);
		
		System.out.println("Fichier que vous posseder :\n" + listFiles);


		try {

			/**
			 * Affichage des adresses IP différentes des adresses de bouclage
			 */
			System.out.println("Adresse de non boulclage disponible :");
			ipHoteHeberge = printAllIP(false);

			if (Objects.equals(ipHoteHeberge.getHostAddress(), ipServ)){
				System.out.println("\nAdresse IP " + ipServ + " de ma machine (qui n'est pas une adresse de bouclage).\nTerminal Application : ");
			} else {
				System.out.println("\nAdresse IP " + ipServ + " ne correspond a aucune adresse valide.");
				System.exit(0);
			}

			//On crée la socket d'écoute de ce client et on lance le thread de ce client
			sockConn = new ServerSocket(0);
			tc = new ThreadClient(sockConn, listFiles, repository.getAbsolutePath() + "/" );
			tc.start();
			System.out.println("port Serveur : " + sockConn.getLocalPort());

			//On crée la socket qui se connecte au serveur
			sockComm = new Socket(ipServ, portServ);
			System.out.println("port Comm : " + sockComm.getLocalPort());

			//On instancie les flux
			sockIn = new ObjectInputStream(new BufferedInputStream(sockComm.getInputStream()));
			sockOs = new ObjectOutputStream(new BufferedOutputStream(sockComm.getOutputStream()));
			sockOs.flush();

			//On envoie la liste de fichiers que possède ce client au serveur
			sockOs.writeObject(listFiles);
			sockOs.flush();

			//On envoie le numéro de port de la socket d'écoute au serveur pour que celui le stocke
			sockOs.writeInt(sockConn.getLocalPort());
			sockOs.flush();

			/**
			 * Partie permettant de gérer l'interpréteur de commande
			 */
			P2PFile [] currentSearch = new P2PFile[0];

			String saisie = null;
			do {
				try {
					System.out.println("-->");

					/**
					 * Saisie de la commande par le client
					 */
					saisie = clavier.readLine();

					/**
					 * Traitement de la commande si celle-ci n'est pas vide
					 * 
					 * Si la commande saisie est "local" alors le client traite lui-même cette commande en affichant sa liste de fichiers
					 * Dans tous les autres cas un objet Request est créée à partir de la saisie et cet objet est ensuite envoyé au serveur pour qu'il traite la commande
					 */
					if (saisie.length() != 0) {
						Request requete = new Request(saisie);
						if (requete.getCommande().equals("local")) {
							System.out.println(listFiles.toString());
						} else {
							//Envoi de la requête au serveur
							sockOs.writeObject(requete);
							sockOs.flush();
							
							//On récupère le premier argument de la commande
							String commande = requete.getCommande();

							/**
							 * Pour les commandes "help" et "list", le client récupère une string envoyée par le serveur dans le but d'un affichage
							 * "help" affiche un message d'aide pour l'utilisation de l'interpréteur de commande
							 * "list" affiche la liste des fichiers possédés par l'ensemble des clients connectés au serveur
							 * "get" effectue le téléchargement du fichier téléchargé
							 * "search" affiche le résultat de la recherche effectuée, après avoir envoyé cette requête au serveur, celui-ci va envoyer un tableau de P2PFile correspondant à la recherche
							 * "quit" met fin à l'application en levant l'exception EndConnectionException. Lors de la levée de cette exception, le client affiche un message de fermeture
							 */
							switch (commande) {
								case "help":
									String reponse = sockIn.readUTF();
									System.out.println(reponse);
									break;

								case "list":
									String reponseList = sockIn.readUTF();
									System.out.println(reponseList);
									break;

                                case "get":
									int num = Integer.parseInt(requete.getArg());
									P2PFile downThisFile = currentSearch[num-1];

									if (!listFiles.getListFiles().contains(downThisFile)){
										sockOs.writeUTF("valide");
										sockOs.flush();

										SocketAddress[] tblListAdress = (SocketAddress[]) sockIn.readObject();

										int[] tblPortSocketServeur = (int[]) sockIn.readObject();

										System.out.println("Voici la liste des hebergeurs de ce fichier :");
										System.out.println(P2PFunctions.printGetListAdress(tblListAdress, tblPortSocketServeur));

										System.out.println( downThisFile.getNameFile() + ":" + downThisFile.getSizeFile() + "\nnb addr : " + tblListAdress.length );

                                        RandomAccessFile fileCreated = new RandomAccessFile ( repository.getAbsolutePath() + "/" + downThisFile.getNameFile(),"rw" ); //on creer le fichier sur le disque dur


                                        for (int i = 0; i < tblListAdress.length; i++){
											System.out.println("\nlancement du tread vers : " + tblListAdress[i].toString());

											InetAddress iAdd = InetAddress.getByName(tblListAdress[i].toString().split("/")[1].split(":")[0]);
//											System.out.println(iAdd.getHostAddress());
//											Integer.parseInt(tblListAdress[i].toString().split("/")[1].split(":")[1]);
//											System.out.println(portAdd);

											double morceaux = (double) downThisFile.getSizeFile() / 1024;
											System.out.println("nb morceaux : " + morceaux);
											System.out.println("nb moraceaux arrond sup : " + (int) Math.ceil(morceaux));

											double partageMorceauxD = (double) ( Math.ceil(morceaux) / tblListAdress.length );
											System.out.println("taille du partage arondi supp : " + partageMorceauxD);

											int partageMorceaux = (int) Math.ceil( partageMorceauxD )  ;
											System.out.println("taille du partage arondi supp : " + partageMorceaux);

											int preMorceauInclu = partageMorceaux * (i+1) - partageMorceaux;
											System.out.println("prem morceau inclu : " + preMorceauInclu);

											int derMorceauExclu = partageMorceaux * (i+1) - 1;
											System.out.println("dern morceau exclu : " + derMorceauExclu);

											tr = new ThreadReceiver(iAdd, tblPortSocketServeur[i], downThisFile, fileCreated, preMorceauInclu, derMorceauExclu);
											tr.start();
											tr.join();
										}
										// mettre a jour la liste de fichier du client
										listFiles.addFilesCreated(downThisFile);

									}else {
										System.out.println("\nVous posseder deja ce fichiers dans votre banque de fichier.\n");
									}


                                    break;

								case "search":
									currentSearch = (P2PFile [])sockIn.readObject();
									if (currentSearch.length == 0) {
										System.out.println("La liste des résultats est vide");
									} else {
										System.out.println(P2PFunctions.printSearch(currentSearch));
									}
									break;

								case "quit":
									throw new EndConnectionException();

								default:
									System.out.println("Erreur de requête");
									break;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (saisie.length() != 0);

		} catch (ConnectException e){
			System.out.println("\nLa connection au serveur n'a pas pu aboutir");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println();
			e.printStackTrace();
		} catch (EndConnectionException e) {
			System.out.println("Fermeture de l'application");

		}
		finally {
			try {

				//Fermeture des différents flux et sockets
				if (sockConn != null){
					sockConn.close();
				}

				if (tc != null) {
					tc.stop();
				}

				if (sockOs != null){
					sockOs.close();
				}
				
				if (sockIn != null){
					sockIn.close();
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

	/**
	 * Fonction permettant d'afficher toutes les adresses IP disponibles autre que l'adresse de bouclage.
	 * 
	 * @param loopBack : Booleen permettant d'indiquer si on inclut l'adresse de bouclage ou non dans l'affichage
	 * @return L'ensemble des adresses à afficher
	 * @throws SocketException
	 */
    private static InetAddress printAllIP(boolean loopBack) throws SocketException {
        Enumeration<NetworkInterface> en = null;
        // Returns all the interfaces on this machine.
        try{
            en = NetworkInterface.getNetworkInterfaces();
        }
        catch(SocketException e){
            e.printStackTrace();
            throw new SocketException("SocketException levée");
        }
        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            //Convenience method to return an Enumeration with all or a subset of the InetAddresses bound to this network interface.
            Enumeration<InetAddress> en2 = i.getInetAddresses();

            while(en2.hasMoreElements()) {
                InetAddress addr = en2.nextElement();
                if (addr.isLoopbackAddress() == loopBack) {
                    // pour n'afficher que les IP qui ne sont pas des adresses de lien local
                    //if (!addr.isLinkLocalAddress()){}

                    // pour n'afficher que les adresses IPv4
                    if (addr instanceof Inet4Address){
                        System.out.println("\t" + addr.getHostAddress());
						if (!Objects.equals(addr.getHostAddress(), "127.0.0.1")){
							return addr;
						}
                    }
                }
            }
        }
		throw new ExceptionInInitializerError("Vous n'avez pas d'adresse IP");
    }
}
