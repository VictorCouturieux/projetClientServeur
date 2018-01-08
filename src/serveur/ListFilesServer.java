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

	private HashMap<P2PFile, ArrayList<SocketAddress>> listFiles;
	private HashMap<SocketAddress, Integer> listPortServeurSocket;

	public ListFilesServer() {
		this.listFiles = new HashMap<P2PFile, ArrayList<SocketAddress>>();
		this.listPortServeurSocket = new HashMap<SocketAddress, Integer>();
	}
	
	public void addListFiles(ListFilesClient lfc, SocketAddress sa, int portNumber) {
		for (P2PFile p2pFile : lfc.getListFiles()) {
			if (!listFiles.containsKey(p2pFile)) {
				ArrayList<SocketAddress> listSA = new ArrayList<SocketAddress>();
				listFiles.put(p2pFile, listSA);
			}
			listFiles.get(p2pFile).add(sa);
			
			if(!listPortServeurSocket.containsKey(sa)) {
				Integer port = new Integer(portNumber);
				listPortServeurSocket.put(sa, port);
			}
		}
	}
	
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
