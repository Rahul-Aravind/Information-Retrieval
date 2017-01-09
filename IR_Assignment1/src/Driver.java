import java.util.Scanner;

public class Driver {
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the directory with full absolute path");
		
		String dirName = in.next();
		
		//Tokenization process
		long startTime = System.currentTimeMillis();
		Tokenizer tokenizer = new Tokenizer(dirName);
		tokenizer.execute();
		long endTime = System.currentTimeMillis();
		
		System.out.println("\nTime taken for tokenization process: " + (endTime - startTime) + " ms\n");
		
		//Get the token summary object
		TokenSummary tokenSummary = tokenizer.getTokenSummary();
		
		//Stemming process
		StemmerDriver stemmerDriver = new StemmerDriver(tokenSummary);
		stemmerDriver.execute();
		
		in.close();
	}

}
