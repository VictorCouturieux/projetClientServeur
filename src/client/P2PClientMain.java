package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PClientMain {
	
	public static void main(String[] args) {
		
		String ipServ;
		int portServ = 0;
		ServerSocket sockConn = null;
		Socket sockComm = null;
		ObjectOutputStream sockOs = null;
		ObjectInputStream sockIn = null;
		
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
			//On crée la socket d'écoute du serveur
			sockConn = new ServerSocket(0);
			
			//On crée la socket qui se connecte au serveur
			sockComm = new Socket(ipServ, portServ);
			
			//On instancie les flux
			sockOs = new ObjectOutputStream(new BufferedOutputStream(sockComm.getOutputStream()));
			sockIn = new ObjectInputStream(new BufferedInputStream(sockComm.getInputStream()));
			
			sockOs.writeObject(listFiles);
			sockOs.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
