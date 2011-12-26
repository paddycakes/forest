package forest.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class Console {
	
	private static final String ESC = "\u001b";
	private static final String RESET = ESC + "c";
	private static final String CLEAR = ESC + "[2J";
	private static final String BLUE_BACKGROUND = ESC + "44m";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		PrintStream out = new PrintStream(System.out, true, "UTF-8");
		out.print(RESET + BLUE_BACKGROUND);
		out.print(CLEAR + "\n\n\n\nHello World!");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		while (!(input = in.readLine()).trim().equals("quit")) {
			//out.println(input);
		}
		out.print(RESET);
		
		
//		java.io.Console console = System.console();
//		console.printf("\u062a\u0628\u0644\u06cc\u063a\u0627\u062a");
	}

}
