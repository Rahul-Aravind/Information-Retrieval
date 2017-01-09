import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Tokenizer {
	private String dirName;
	private HashMap<String, Integer> tokenMapData;
	private TokenSummary tokenSummary;
	
	public Tokenizer(String dirName) {
		this.dirName = dirName;
		tokenSummary = new TokenSummary();
		tokenMapData = tokenSummary.getTokenMapData();
	}
	
	public void tokenize(String fileContents) {
		
		//Remove all the '. ' at the end of the line and replace it with space
		fileContents = fileContents.replaceAll("\\. ", " ");
		
		//Split the tokens into two if there is a space or , 
		//or / or \ or - between the two words
		
		String[] tokens = fileContents.split("\\s+|\\/|\\\\|\\-|,");
		
		for(String token : tokens) {
			token = token.trim();
			
			// Replace all the 's with a "" character
			token = token.replaceAll("'s", "");
			
			// Replace all the special characters except meta characters and .
			token = token.replaceAll("[^\\w.]", "");
			
			//Delete the . in the token if it doesn't correspond in a valid token
			// 2.3, j.y, C1.25 are valid tokens. . should not be removed.
			//1.2.... - invalid token
			
			
			// The numbers are not considered as token.			
			if(token.matches("[\\d]+")) continue;
			if(token.contains(".")) { 
				if(!token.matches("^(\\w+)([\\.])(\\w+)+")){
					continue;
				}
			}
			
			if(token.isEmpty()) continue;
			
			// converting all the letters in the token to lowercase
			token = token.toLowerCase();
			token = token.trim();
			
			Integer count = tokenMapData.get(token);
			
			if(count == null) {
				tokenMapData.put(token, 1);
			} else {
				tokenMapData.put(token, count + 1);
			}
			
		}
		
	}
	
	private void calculateTokenStats() {
		//Get the total token count in the whole cranfield collection
		
		long totalTokenCount = 0;
		int fileListSize = tokenSummary.getFileListSize();
		
		for(String key : tokenMapData.keySet()) {
			totalTokenCount += tokenMapData.get(key);
		}
		
		tokenSummary.setTokenMapData(tokenMapData);
		tokenSummary.setTotalTokenCount(totalTokenCount);
		tokenSummary.setUniqueTokenCount(tokenMapData.size());
		tokenSummary.setAvgUniqueTokenCount(totalTokenCount / fileListSize);
		
		LinkedList<Entry<String, Integer>> sortedList = CollectionsUtils.convertHashMapToSortedList(tokenMapData);
		tokenSummary.setSortedList(sortedList);
	}
	
	public void printTokenStats() {
		System.out.println("*******************************************");
		System.out.println("TOKEN SUMMARY OF CRANFIELD DATASET");
		System.out.println("*******************************************");
		
		System.out.println("The number of tokens in the collection: " + tokenSummary.getTotalTokenCount());
		System.out.println("The number of unique tokens in the collections: " + tokenSummary.getUniqueTokenCount());
		System.out.println("The average number of word tokens per document: " + tokenSummary.getAvgUniqueTokenCount());
		
		//get token count that occurred only once
		int tokenCount = CollectionsUtils.getTokenCount(tokenSummary.getSortedList(), 1);
		System.out.println("The number of words that occur only once in the cranfield collection: " + tokenCount);
		
		// get top 30 tokens that occurred frequently
		
		LinkedList<Entry<String, Integer>> list = tokenSummary.getSortedList();
		int listSize = list.size();
		
		System.out.format("\n%-20s%20s\n","Token","Frequency");
		System.out.println("-----------------------------------------------------");
		for(int i = 0; i < 30; i++) {
			Entry<String, Integer> e = list.get(listSize - 1 - i);
			System.out.format("\n%-20s%20s\n", e.getKey(), e.getValue());
		}
		System.out.println();
	}
	
	public void execute() {
	
		File files[] = FileUtil.getFilesList(dirName);
		int fileListSize = files.length;
		
		
		tokenSummary.setFileListSize(fileListSize);
		
		//tokenize each and every file contents and store the token
		//characteristics in the map
		
		for(File file : files) {
			Parser parser = new Parser();
			String fileContents = parser.parseXml(file);
			tokenize(fileContents);
		}
		
		/*TreeMap<String, Integer> hash = new TreeMap<String, Integer>(tokenMapData);
		for(String key : hash.keySet()) {
			System.out.println("Inside Map " + key);
		}*/
		
		//calculating the token statistics
		calculateTokenStats();
		
		//printing the token statistics
		printTokenStats();
	}
	
	public TokenSummary getTokenSummary() {
		return tokenSummary;
	}

}
