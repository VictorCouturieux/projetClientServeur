package serveur;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import client.ListFilesClient;
import comServCli.P2PFile;

/**
 * Created by ctx on 04/12/17.
 */
public class ListFilesServer {

	/**
	 * Les deux HashMap permettent d'enregistrer différentes informations.
	 * 
	 * La première HashMap fait office de base de données où sont enregistrés les fichiers ainsi que leurs propriétaires en donnant leurs adresses IP
	 * La deuxième HashMap permet d'enregistrer le numéro de port de chacun des clients présents au moins une fois dans la première HashMap
	 */
	private HashMap<P2PFile, ArrayList<SocketAddress>> listFiles;
	private HashMap<SocketAddress, Integer> listPortServeurSocket;

	/**
	 * Le constructeur initialise les deux HashMap
	 */
	public ListFilesServer() {
		this.listFiles = new HashMap<P2PFile, ArrayList<SocketAddress>>();
		this.listPortServeurSocket = new HashMap<SocketAddress, Integer>();
	}
	
	/**
	 * Fonction permettant de mettre à jour les deux HashMap lorsqu'un client envoie sa liste de fichiers au serveur
	 * 
	 * @param lfc : La liste de fichiers possédés par le client qui se connecte au serveur
	 * @param sa : L'adresse IP de la socket de communication du client qui envoie sa liste de fichiers au serveur
	 * @param portNumber : Le numéro de port de la socket d'écoute du client qui se connecte au serveur
	 */
	public void addListFiles(ListFilesClient lfc, SocketAddress sa, int portNumber) {
		for (P2PFile p2pFile : lfc.getListFiles()) {
			//Si le fichier n'est pas déjà présent dans la HashMap alors on crée une nouvelle entrée dans la première HashMap
			if (!listFiles.containsKey(p2pFile)) {
				ArrayList<SocketAddress> listSA = new ArrayList<SocketAddress>();
				listFiles.put(p2pFile, listSA);
			}
			//On ajoute l'adresse de la socket de communication du client dans la première HashMap
			listFiles.get(p2pFile).add(sa);
			
			//Si la seconde HashMap ne possède pas l'adresse du client alors on l'ajoute dans la HashMap et après on ajoute le numéro de port de la socket d'écoute de ce client
			if(!listPortServeurSocket.containsKey(sa)) {
				Integer port = new Integer(portNumber);
				listPortServeurSocket.put(sa, port);
			}
		}
	}

	/**
	 * Fonction permettant de mettre à jour la première HashMap lors d'un téléchargement de fichier par un client
	 * 
	 * @param p2pFile : le fichier téléchargé
	 * @param sa : l'adresse de la socket de communication du client ayant téléchargé le fichier
	 */
	public void addFilesCreated(P2PFile p2pFile, SocketAddress sa){
		listFiles.get(p2pFile).add(sa);
	}
	
	/**
	 * Fonction permettant de mettre à jour la première HashMap lors de la déconnexion d'un client
	 * 
	 * @param lfc : la liste de fichiers du client se déconnectant
	 * @param sa : l'adresse du client se déconnectant
	 */
	public void deleteClient(ListFilesClient lfc, SocketAddress sa) {
		for (P2PFile p2pFile : lfc.getListFiles()) {
			if (listFiles.containsKey(p2pFile)) {
				ArrayList<SocketAddress> listSA = listFiles.get(p2pFile);
				listSA.remove(sa);
				
				if (listSA.isEmpty()) {
					listFiles.remove(p2pFile);
				}
			}
		}
	}
	
	/**
	 * Fonction permettant de créer un tableau contenant les numéros de port des socket d'écoute des clients dont les adresses sont présentes dans l'ArrayList passée en paramètre
	 * Le premier numéro de port présent dans le tableau correspondra au numéro de port du client possédant le premier élément de l'ArrayList d'adresse passé en paramètre
	 * Le second élément du tableau correspond au second élément de l'ArrayList et ainsi de suite...
	 * 
	 * @param listSa : L'ArrayList contenant les adresses des clients
	 * @return : Le tableau contenant les numéros de port.
	 */
	public int[] createArrayNumberPort(ArrayList<SocketAddress> listSa) {
		int [] numberPortArray = new int [listSa.size()];
		int i = 0;
		
		for (SocketAddress sa : listSa) {
			numberPortArray[i] = listPortServeurSocket.get(sa);
			i++;
		}
		
		return numberPortArray;
	}
	
	public String toString() {
		if (listFiles.isEmpty()) {
			return "La liste de fichiers est vide\n";
		}
		else {
			String print = "";
			for (Entry<P2PFile, ArrayList<SocketAddress>> mapentry : listFiles.entrySet()) {
				print += "\t<" + mapentry.getKey().toString();
				for (SocketAddress sa : mapentry.getValue()) {
					print += " " + sa.toString() + " ";
				}
				print += ">\n";
			}
			return print;
		}
	}

	public HashMap<P2PFile, ArrayList<SocketAddress>> getListFiles() {
		return listFiles;
	}
}
