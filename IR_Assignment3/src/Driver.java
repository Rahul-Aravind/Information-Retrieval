import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;

public class Driver {
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		
		System.out.println("Enter the directory with full absolute path");
		String dirName = in.next();
		
		System.out.println("Enter the query file name with full absolute path");
		String queryFileName = in.next();
			
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
		InvertedIndexLemma invertedIndexLemma = new InvertedIndexLemma(lemmaProcessor);
		invertedIndexLemma.constructUncompressedIndex();
		endTime = System.currentTimeMillis();
		
		System.out.println("Uncompressed Inverted Index construction process completed................");
		System.out.println("\nTime taken for Uncompressed Index construction: " + (endTime - startTime) + " ms\n");
		System.out.println("**********************************************");
		
		QueryProcessor queryProcessor = new QueryProcessor(queryFileName);
		queryProcessor.processQueryFile();
		ArrayList<QueryStats> queryStatsList = queryProcessor.getQueryStatsList();
		
		int totalQueryTerms = 0;
		for(QueryStats queryStats : queryStatsList) {
			for(int val : queryStats.getQueryTokenMap().values()) {
				totalQueryTerms += val;
			}
		}
		
		int avgQueryDocLen = totalQueryTerms / 20;
		
		HashMap<String, Integer> queryDocLenMap = new HashMap<>();
		
		for(QueryStats queryStats : queryStatsList) {
			for(Entry<String, Integer> entry : queryStats.getQueryTokenMap().entrySet()) {
				Integer occurrence;
				String term = entry.getKey();
				int count = entry.getValue();
				
				
				if((occurrence = queryDocLenMap.get(term)) == null) {
					queryDocLenMap.put(term, count);
				} else {
					queryDocLenMap.put(term, count + occurrence);
				}
			}
		}
		
		
		
		for(QueryStats queryStats : queryStatsList) {
			System.out.println("===============================================================");
			RelevanceModel relevanceModel = new RelevanceModel(invertedIndexLemma, tokenizer, avgQueryDocLen);
			relevanceModel.weightingFunctionW1(queryStats, queryDocLenMap);
			relevanceModel.getTopNDOcsUsingRankSearchFromW1();
			
			relevanceModel.weightingFunctionW2(queryStats, queryDocLenMap);
			relevanceModel.getTopNDOcsUsingRankSearchFromW2();
			System.out.println("================================================================");
		}
		
		in.close();
	}
}
