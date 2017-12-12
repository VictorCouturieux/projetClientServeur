package comServCli;

import java.util.ArrayList;

public class P2PFunctions {
	
	public static String printSearch(ArrayList<P2PFile> result) {
		String print = "";
		int count = 1;
		for (P2PFile p2pFile : result) {
			print += count + " " + p2pFile.toString() + "\n";
			count++;
		}
		
		return print;
	}
}
