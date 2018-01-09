package client;

import java.io.File;
import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;

import comServCli.P2PFile;

public class ListFilesClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<P2PFile> listFiles;

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
