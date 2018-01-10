package client;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import comServCli.P2PFile;

public class ListFilesClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<P2PFile> listFiles;

	/**
	 * Constructeur de la classe permettant d'initialiser la liste des fichiers possédés par un client.
	 * Pour initialiser cette liste, nous parcourons le répertoire passé en paramètre et nous créons les P2PFile au fur et à mesure avant de les ajouter à la liste de fichiers
	 * 
	 * @param repertoire : le repertoire à partir duquel on initialise la liste de fichiers
	 */
	public ListFilesClient(File repertoire) {
		this.listFiles = new ArrayList<P2PFile>();
		File [] files = repertoire.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			P2PFile pFile = new P2PFile(files[i]);
			this.listFiles.add(pFile);
		}
	}

	public ArrayList<P2PFile> getListFiles() {
		return listFiles;
	}

	public void setListFiles(ArrayList<P2PFile> listFiles) {
		this.listFiles = listFiles;
	}

	/**
	 * Fonction permettant d'ajouter un P2PFile dans la liste des fichiers possédés par un client.
	 * Cette fonction sera utilisée après un téléchargement pour mettre à jour la liste des fichiers possédés par le client
	 * 
	 * @param p2pFile : le P2PFile a ajouté dans la liste des fichiers
	 */
	public void addFilesCreated(P2PFile p2pFile){
		this.listFiles.add(p2pFile);
	}

	@Override
	public String toString() {
		String printListFiles = "";
		int i = 1;
		
		for (P2PFile p2pFile : listFiles) {
			printListFiles += "<" + i + "; " + p2pFile.toString() + ">\n";
			i++;
		}
		
		return printListFiles;
	}
}
