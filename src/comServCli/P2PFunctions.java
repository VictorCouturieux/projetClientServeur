package comServCli;

import java.util.ArrayList;

public class P2PFunctions {
	
	public static String printSearch(ArrayList<P2PFile> result) {
		String print = "<";
		for (P2PFile p2pFile : result) {
			print += p2pFile.toString() + "\n";
		}
		print += ">";
		
		return print;
	}
}
