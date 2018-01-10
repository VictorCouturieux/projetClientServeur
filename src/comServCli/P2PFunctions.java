package comServCli;

import java.net.SocketAddress;

public class P2PFunctions {
	
	/**
	 * Fonction permettant d'afficher le résultat d'une recherche de fichier par un utilisateur.
	 * Cette fonction sera utilisée après la réception du résultat de la recherche dans le fichier P2PClientMain.
	 * 
	 * @param result : Le résultat de la recherche sous forme d'un tableau de P2PFile
	 * @return : La chaîne de caractères à afficher
	 */
	public static String printSearch(P2PFile [] result) {
		String print = "";
		int count = 1;
		for (int i = 0; i < result.length; i++) {
			print += count + " " + result[i].toString() + "\n";
			count++;
		}
		
		return print;
	}

	/**
	 * Fonction permettant d'afficher la liste d'adresse IP qui possède un fichier en particulier ainsi que le numéro de port de la socket d'écoute de chacun de ces clients
	 * 
	 * @param listAdress
	 * @param tblPortSocketServeur
	 * @return : La chaîne de caractères à afficher
	 */
	public static String printGetListAdress(SocketAddress[] listAdress, int[] tblPortSocketServeur) {
		String print = "";
		int count = 1;

		for (int i = 0; i < listAdress.length; i++){
			print += count + " " + listAdress[i].toString() + "\n" +
				"\tport Socket Serveur : " + tblPortSocketServeur[i] + "\n";
			count++;
		}

		return print;
	}
}
