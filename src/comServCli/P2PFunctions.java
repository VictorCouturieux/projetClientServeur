package comServCli;

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
}
