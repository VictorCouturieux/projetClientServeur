package comServCli;

import java.net.SocketAddress;
import java.util.ArrayList;

public class P2PFunctions {
	
	public static String printSearch(P2PFile [] result) {
		String print = "";
		int count = 1;
		for (int i = 0; i < result.length; i++) {
			print += count + " " + result[i].toString() + "\n";
			count++;
		}
		
		return print;
	}

	public static String printGetListAdress(SocketAddress[] listAdress, int[] tblPortSocketServeur) {
		String print = "";
		int count = 1;

		for (int i = 0; i < listAdress.length; i++){
			print += count + " " + listAdress[i].toString() + "\n" +
				"\tport Socket Serveur" + tblPortSocketServeur[i] + "\n";
			count++;
		}

		return print;
	}
}
