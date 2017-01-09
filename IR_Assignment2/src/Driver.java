import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class Driver {
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		String version1UnCompressedFileName = "Index_Version1.uncompress";
		String version1CompressedFileName = "Index_Version1.compressed";
		String version2UnCompressedFileName = "Index_Version2.uncompress";
		String version2CompressedFileName = "Index_Version2.compressed";
		
		System.out.println("Enter the directory with full absolute path");
		String dirName = in.next();
		
		long overallStartTime = System.currentTimeMillis();
		
		//Tokenization process
		
		System.out.println("*********************************************");
		System.out.println("Tokenization process initiated...............");
		
		long startTime = System.currentTimeMillis();
		Tokenizer tokenizer = new Tokenizer(dirName);
		tokenizer.execute();
		long endTime = System.currentTimeMillis();
		
		System.out.println("Tokenization process completed................");
		System.out.println("\nTime taken for tokenization process: " + (endTime - startTime) + " ms\n");
		System.out.println("**********************************************");
		
		// lemmatization process
		
		System.out.println("*********************************************");
		System.out.println("Lemmatization process initiated...............");
		
		startTime = System.currentTimeMillis();
		LemmaProcessor lemmaProcessor = new LemmaProcessor(tokenizer);
		lemmaProcessor.execute();
		endTime = System.currentTimeMillis();
		
		System.out.println("Lemmatization process completed................");
		System.out.println("\nTime taken for lemmatization process: " + (endTime - startTime) + " ms\n");
		System.out.println("**********************************************");
		
		// uncompressed inverted index version 1 construction
		
		System.out.println("*****************************************************************");
		System.out.println("Uncompressed Inverted Index version 1 construction 1 initiated................");
		
		startTime = System.currentTimeMillis();
		InvertedIndexLemma invertedIndexLemma = new InvertedIndexLemma(lemmaProcessor, version1UnCompressedFileName, version1CompressedFileName);
		invertedIndexLemma.constructUncompressedIndex();
		endTime = System.currentTimeMillis();
		
		System.out.println("Uncompressed Inverted Index construction process completed................");
		System.out.println("\nTime taken for Uncompressed Index construction (uncompressed and compressed): " + (endTime - startTime) + " ms\n");
		System.out.println("**********************************************");
		
		// Compressed inverted index version 1 construction
		
		System.out.println("*****************************************************************");
		System.out.println("Compressed Inverted Index version 1 construction 1 initiated................");
		
		startTime = System.currentTimeMillis();
		invertedIndexLemma.constructCompressedIndex();
		endTime = System.currentTimeMillis();
		
		System.out.println("Compressed Inverted Index construction version 1 process completed................");
		System.out.println("\nTime taken for Compressed Index version 1 construction : " + (endTime - startTime) + " ms\n");
		System.out.println("**********************************************");
		
		// stemmer process
		
		System.out.println("*********************************************");
		System.out.println("Stemming process initiated...............");
		
		StemmerDriver stemmerDriver = new StemmerDriver(tokenizer);
		startTime = System.currentTimeMillis();
		stemmerDriver.execute();
		endTime = System.currentTimeMillis();
		
		System.out.println("Stemming process completed.................");
		System.out.println("Time taken for Stemming: " + (endTime - startTime) + " ms");
		System.out.println("**************************************************");
		
		// uncompressed inverted index version 2 construction
		
		System.out.println("*****************************************************************");
		System.out.println("Uncompressed Inverted Index version 2 construction initiated................");
		
		startTime = System.currentTimeMillis();
		InvertedIndexStem invertedIndexStem = new InvertedIndexStem(stemmerDriver, version2UnCompressedFileName, version2CompressedFileName);
		invertedIndexStem.constructUncompressedIndex();
		endTime = System.currentTimeMillis();
		
		System.out.println("Uncompressed Inverted Index version 2 construction process completed................");
		System.out.println("\nTime taken for Uncompressed Index version 2 construction: " + (endTime - startTime) + " ms\n");
		System.out.println("**********************************************");
		
		// Compressed inverted index version 2 construction
		
		System.out.println("*****************************************************************");
		System.out.println("Compressed Inverted Index version 2 construction initiated................");
		
		startTime = System.currentTimeMillis();
		invertedIndexStem.constructCompressedIndex();
		endTime = System.currentTimeMillis();
		
		System.out.println("Compressed Inverted Index construction version 2 process completed................");
		System.out.println("\nTime taken for Compressed Index version 2 construction : " + (endTime - startTime) + " ms\n");
		System.out.println("*******************************************************************");
		
		System.out.println("********************************************************************");
		System.out.println("Index size (Version 1 uncompressed) in bytes " + new File(version1UnCompressedFileName).length());
		System.out.println("Index size (Version 1 compressed) in bytes " + new File(version1CompressedFileName).length());
		System.out.println("Index size (Version 2 uncompressed) in bytes " + new File(version2UnCompressedFileName).length());
		System.out.println("Index size (Version 2 compressed) in bytes " + new File(version2CompressedFileName).length());
		
		long overallEndTime = System.currentTimeMillis();
		System.out.println("\n Total Time for Index building: " + (overallEndTime - overallStartTime) + " ms");
		System.out.println("*********************************************************************");
		
		TreeMap<String, InvertedIndexValueEntry> ver1Dict = invertedIndexLemma.getDictionary();
		TreeMap<String, InvertedIndexValueEntry> ver2Dict = invertedIndexStem.getDictionary();
		
		DataProvider dataProvider = new DataProvider();
		
		System.out.println("*********************************************");
		System.out.println("Max DF and Min DF in version 1");
		dataProvider.provideTermWithHighestDF(ver1Dict);
		dataProvider.provideTermWithSmallestDF(ver1Dict);
		System.out.println("*********************************************");
		
		System.out.println("*********************************************");
		System.out.println("Max DF and Min DF in version 2");
		dataProvider.provideTermWithHighestDF(ver2Dict);
		dataProvider.provideTermWithSmallestDF(ver2Dict);
		System.out.println("*********************************************");
		
		System.out.println("*********************************************");
		System.out.println("Document with maxTf and Document with doclen");
		ArrayList<DocumentStats> documentStatsList = invertedIndexLemma.getDocumentStatsList();
		dataProvider.provideDocWithMaxTfAndMaxDocLen(documentStatsList);
		System.out.println("*********************************************");
		
		System.out.println("**********************************************");
		System.out.println("Inverted list size in Index 1 " + ver1Dict.size());
		System.out.println("Inverted list size in Index 2 " + ver2Dict.size());
		System.out.println("**********************************************");
	}
}
