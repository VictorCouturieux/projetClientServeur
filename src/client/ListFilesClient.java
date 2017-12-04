package client;

import java.io.File;
import java.util.ArrayList;

import comServCli.P2PFile;

public class ListFilesClient {
	
	private ArrayList<P2PFile> listFiles;

	public ListFilesClient(File repertoire) {
		this.listFiles = new ArrayList<P2PFile>();
		String [] files = repertoire.list();
		
		for (int i = 0; i < files.length; i++) {
			File r = new File(files[i]);
			P2PFile pFile = new P2PFile(r);
			this.listFiles.add(pFile);
		}
	}

	public ArrayList<P2PFile> getListFiles() {
		return listFiles;
	}

	public void setListFiles(ArrayList<P2PFile> listFiles) {
		this.listFiles = listFiles;
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
