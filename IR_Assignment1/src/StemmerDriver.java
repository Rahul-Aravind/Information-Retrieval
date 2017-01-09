import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class StemmerDriver {
	
	private TokenSummary tokenSummary;
	private TokenSummary tokenStemSummary;
	
	public StemmerDriver(TokenSummary tokenSummary) {
		this.tokenSummary = tokenSummary;
		this.tokenStemSummary = new TokenSummary();
	}
	
	public void printTokenStemStats() {
		System.out.println("*******************************************");
		System.out.println("TOKEN STEM SUMMARY OF CRANFIELD DATASET");
		System.out.println("*******************************************");
		
		System.out.println("The Number of distinct stems in the cranfield text collection: " + tokenStemSummary.getUniqueTokenCount());
		System.out.println("The average number of word stems per document: " + tokenStemSummary.getAvgUniqueTokenCount());
		
		//Get count of stems that occur only once in the data set
		int count = CollectionsUtils.getTokenCount(tokenStemSummary.getSortedList(), 1);
		
		System.out.println("The Number of stems that occur only once in the document: " + count);
		
		// get top 30 tokens that occurred frequently
		
		LinkedList<Entry<String, Integer>> list = tokenStemSummary.getSortedList();
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
		HashMap<String, Integer> tokenMapData = tokenSummary.getTokenMapData();
		
		//System.out.println("#DEBUG" + tokenMapData.size());
		
		HashMap<String, Integer> tokenStemMapData = tokenStemSummary.getTokenMapData();
		
		for(Entry<String, Integer> tokenEntry : tokenMapData.entrySet()) {
			String token = tokenEntry.getKey();
			int count = tokenEntry.getValue();
			
			Stemmer stemmer = new Stemmer();
			stemmer.add(token.toCharArray(), token.length());
			stemmer.stem();
			String stem = stemmer.toString();
			
			if(tokenStemMapData.get(stem) == null) {
				tokenStemMapData.put(stem, count);
			}
			else {
				tokenStemMapData.put(stem, count + tokenStemMapData.get(stem));
			}
		}
		
		tokenStemSummary.setTokenMapData(tokenStemMapData);
		tokenStemSummary.setUniqueTokenCount(tokenStemMapData.size());
		
		int totalStemCount = 0;
		for(String key : tokenStemMapData.keySet()) {
			totalStemCount += tokenStemMapData.get(key);
		}
		
		tokenStemSummary.setAvgUniqueTokenCount(totalStemCount / tokenSummary.getFileListSize());
		
		LinkedList<Entry<String, Integer>> sortedList = CollectionsUtils.convertHashMapToSortedList(tokenStemMapData);
		tokenStemSummary.setSortedList(sortedList);
		
		printTokenStemStats();
		
	}

}
