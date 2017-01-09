import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RelevanceModel {
	
	private InvertedIndexLemma invertedIndexLemma;
	private Tokenizer tokenizer;
	private static double averageDocLen;
	private static TreeMap<String, InvertedIndexValueEntry> dictionary;
	private static HashMap<Long, DocumentStats> docStatsMap;
	private static int collectionSize;
	private HashMap<Long, Double> queryDocumentWeightFunction1Map;
	private HashMap<Long, Double> queryDocumentWeightFunction2Map;
	private HashMap<String, Double> queryWeightFunction1Map;
	private HashMap<String, Double> queryWeightFunction2Map;
	private HashMap<Long, HashMap<String, Double>> docVectorMap1;
	private HashMap<Long, HashMap<String, Double>> docVectorMap2;
	private static int N = 5;
	private static ArrayList<FileStats> fileStatsList;
	private static int avgQueryDocLen;
	
	public RelevanceModel(InvertedIndexLemma invertedIndexLemma, Tokenizer tokenizer, int avgQueryDocLen) {
		this.invertedIndexLemma = invertedIndexLemma;
		this.tokenizer = tokenizer;
		this.queryDocumentWeightFunction1Map = new HashMap<>();
		this.queryDocumentWeightFunction2Map = new HashMap<>();
		this.queryWeightFunction1Map = new HashMap<>();
		this.queryWeightFunction2Map = new HashMap<>();
		this.avgQueryDocLen = avgQueryDocLen;
		docVectorMap1 = new HashMap<>();
		docVectorMap2 = new HashMap<>();
		
		initialize();
	}
	
	public void initialize() {
		
		// computing average document length
		docStatsMap = invertedIndexLemma.getDocumentStatsMap();
		collectionSize = docStatsMap.size();
		
		double sum = 0.0d;
		for(Entry<Long, DocumentStats> entry : docStatsMap.entrySet()) {
			sum += entry.getValue().getDocumentLength();
		}
		
		averageDocLen = sum / collectionSize;
		docStatsMap = invertedIndexLemma.getDocumentStatsMap();
		dictionary = invertedIndexLemma.getDictionary();
		fileStatsList = tokenizer.getFileStatsList();
	}
	
	public void weightingFunctionW1(QueryStats queryStats, HashMap<String, Integer> queryDocLenMap) {
		
		HashMap<String, Integer> queryTokenMap = queryStats.getQueryTokenMap();
		int queryMaxTf = queryStats.getMaxTf();
		
		for(Entry<String, Integer> queryTokenEntry : queryTokenMap.entrySet()) {
			String queryTerm = queryTokenEntry.getKey();
			int queryTermFreq = queryTokenEntry.getValue();
			
			InvertedIndexValueEntry invertedIndexValueEntry = dictionary.get(queryTerm);
			
			if(invertedIndexValueEntry == null) continue;
			
			ArrayList<Long> postingsList = invertedIndexValueEntry.getPostingsList();
			ArrayList<Integer> termFrequencyList = invertedIndexValueEntry.getTermFrequencyList();
			long df = invertedIndexValueEntry.getDocumentFrequency();
			
			// computing query vector
			
			double queryScore = (0.4 + 0.6 * Math.log10(queryTermFreq + 0.5) / Math.log10(queryMaxTf + 1.0))
					* (Math.log10(20 / queryDocLenMap.get(queryTerm)) / Math.log10(20));
			
			queryWeightFunction1Map.put(queryTerm, queryScore);
			
			for(int i = 0; i < postingsList.size(); i++) {
				long docId = postingsList.get(i);
				long tf = termFrequencyList.get(i);
				long maxTf = docStatsMap.get(docId).getMaxTermFrequency();
				
				/***
				 * W1 = (0.4 + 0.6 * log (tf + 0.5) / log (maxtf + 1.0))
				 		* (log (collectionsize / df)/ log (collectionsize))
				 */
				
				
				
				double queryDocumentScore = (0.4 + 0.6 * Math.log10(tf + 0.5) / Math.log10(maxTf + 1.0)) *
									(Math.log10(collectionSize / df) / Math.log10(collectionSize));
				
				Double documentScore;
				if((documentScore = queryDocumentWeightFunction1Map.get(docId)) == null) {
					queryDocumentWeightFunction1Map.put(docId, queryDocumentScore);
				} else {
					queryDocumentWeightFunction1Map.put(docId, documentScore + queryDocumentScore);
				}
				
				HashMap<String, Double> vecMap;
				if((vecMap = docVectorMap1.get(docId)) == null) {
					vecMap = new HashMap<>();
					vecMap.put(queryTerm, queryDocumentScore);
					docVectorMap1.put(docId, vecMap);
				} else {
					vecMap.put(queryTerm, queryDocumentScore);
					docVectorMap1.put(docId, vecMap);
				}
			}
		}
		
		// Normalizing W(t, d) values
		
		for(Long docId : docVectorMap1.keySet()) {
			HashMap<String, Double> hash = docVectorMap1.get(docId);
			
			double NValue = 0.0d;
			for(double val : hash.values()) {
				NValue += Math.pow(val, 2);
			}
			
			NValue = Math.sqrt(NValue);
			
			for(String term : hash.keySet()) {
				double val = hash.get(term);
				hash.put(term, val / NValue);
			}
		}
		
		// Normalizing query vector values
		
		double NValue = 0.0d;
		for(String queryTerm : queryWeightFunction1Map.keySet()) {
			NValue += Math.pow(queryWeightFunction1Map.get(queryTerm), 2);
		}
		
		NValue = Math.sqrt(NValue);
		
		String queryTermSet = "";
		for(String queryTerm : queryWeightFunction1Map.keySet()) {
			queryTermSet += queryTerm + " ";
			double val = queryWeightFunction1Map.get(queryTerm) / NValue;
			queryWeightFunction1Map.put(queryTerm, val);
		}
		
		System.out.println("**************************************************");
		System.out.println("Query in vector representation based on W1 Function");
		System.out.println("Query Term set: " + queryTermSet);
		System.out.println(queryWeightFunction1Map.values());
		System.out.println("**************************************************");
	}
	
	public void getTopNDOcsUsingRankSearchFromW1() {
		LinkedList<Entry<Long, Double>> scoreList = CollectionUtils.retSortedScoreList(queryDocumentWeightFunction1Map);
		N = scoreList.size() >= N ? N : scoreList.size();
		
		System.out.println("*****************************************************************************");
		System.out.println("Documents retrieved from rank search based on W1 function");
		System.out.format("%-10s%-30s%-30s%-30s\n","Rank","Score","Document Indentifier","Heading");
		
		for(int i = 0; i < N; i++) {
			Entry<Long, Double> scoreEntry = scoreList.get(i);
			
			long docId = scoreEntry.getKey();
			double score = scoreEntry.getValue();
			File file = fileStatsList.get((int) (docId - 1)).getFile();
			XMLContentParser xmlContentParser = new XMLContentParser(file);
			String titleName = xmlContentParser.getTitleName();
			
			titleName = titleName.replace("\n", "");
			titleName = titleName.trim();
			
			System.out.format("%-10s%-30s%-30s%-30s\n", (i + 1), score, file.getName(), titleName);
		}
		
		System.out.println("*************************************************************************");
		System.out.println("Document vector based on W1 Function");
		
		for(int i = 0; i < N; i++) {
			System.out.println("====================================================");
			Entry<Long, Double> scoreEntry = scoreList.get(i);
			Long docId = scoreEntry.getKey();
			HashMap<String, Double> hash = docVectorMap1.get(docId);
			System.out.println("Document vector of query w.r.t to document " + docId);
			for(String term : hash.keySet()) {
				System.out.println("Term " + term + " Weight: " + hash.get(term));
			}
			System.out.println("======================================================");
			
		}
		System.out.println("****************************************************************************");
		
	}
	
	public void weightingFunctionW2(QueryStats queryStats, HashMap<String, Integer> queryDocLenMap) {
		
		HashMap<String, Integer> queryTokenMap = queryStats.getQueryTokenMap();
		
		int queryDocLen = 0;
		for(int val : queryTokenMap.values()) {
			queryDocLen += val;
		}
		
		int queryMaxTf = queryStats.getMaxTf();
		for(Entry<String, Integer> queryTokenEntry : queryTokenMap.entrySet()) {
			String queryTerm = queryTokenEntry.getKey();
			int queryTermFreq = queryTokenEntry.getValue();
			
			InvertedIndexValueEntry invertedIndexValueEntry = dictionary.get(queryTerm);
			
			if(invertedIndexValueEntry == null) continue;
			
			ArrayList<Long> postingsList = invertedIndexValueEntry.getPostingsList();
			ArrayList<Integer> termFrequencyList = invertedIndexValueEntry.getTermFrequencyList();
			long df = invertedIndexValueEntry.getDocumentFrequency();
			
			// computing query vector
			
			double queryScore = (0.4 + 0.6 * (queryTermFreq) / (queryTermFreq + 0.5 + 1.5 * (queryDocLen / avgQueryDocLen))) *
					Math.log10(20/queryDocLenMap.get(queryTerm)) / Math.log10(20);
			
			queryWeightFunction2Map.put(queryTerm, queryScore);
			
			for(int i = 0; i < postingsList.size(); i++) {
				long docId = postingsList.get(i);
				long tf = termFrequencyList.get(i);
				long docLen = docStatsMap.get(docId).getDocumentLength();
				
				/***
				 * W2 = (0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 *
 					(doclen / avgdoclen))) * log (collectionsize / df)/
 					log (collectionsize))

				 */
				
				double queryDocumentScore = (0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 * (docLen / averageDocLen))) * 
									Math.log10(collectionSize / df) / Math.log10(collectionSize));
				
				Double score;
				if((score = queryDocumentWeightFunction2Map.get(docId)) == null) {
					queryDocumentWeightFunction2Map.put(docId, queryDocumentScore);
				} else {
					queryDocumentWeightFunction2Map.put(docId, score + queryDocumentScore);
				}
				
				HashMap<String, Double> vecMap;
				if((vecMap = docVectorMap2.get(docId)) == null) {
					vecMap = new HashMap<>();
					vecMap.put(queryTerm, queryDocumentScore);
					docVectorMap2.put(docId, vecMap);
				} else {
					vecMap.put(queryTerm, queryDocumentScore);
					docVectorMap2.put(docId, vecMap);
				}
			}
		}
		
		// Normalizing W(t, d) values
		
		for(Long docId : docVectorMap2.keySet()) {
			HashMap<String, Double> hash = docVectorMap2.get(docId);
			
			double NValue = 0.0d;
			for(double val : hash.values()) {
				NValue += Math.pow(val, 2);
			}
			
			NValue = Math.sqrt(NValue);
			
			for(String term : hash.keySet()) {
				double val = hash.get(term);
				hash.put(term, val / NValue);
			}
		}
		
		// Normalizing query vector values
		
		double NValue = 0.0d;
		for(String queryTerm : queryWeightFunction2Map.keySet()) {
			NValue += Math.pow(queryWeightFunction2Map.get(queryTerm), 2);
		}
		
		NValue = Math.sqrt(NValue);
		
		String queryTermSet = "";
		for(String queryTerm : queryWeightFunction2Map.keySet()) {
			queryTermSet += queryTerm + " ";
			double val = queryWeightFunction2Map.get(queryTerm) / NValue;
			queryWeightFunction2Map.put(queryTerm, val);
		}
		
		System.out.println("**************************************************");
		System.out.println("Query in vector representation based on W2 Function");
		System.out.println("Query Term set: " + queryTermSet);
		System.out.println(queryWeightFunction2Map.values());
		System.out.println("**************************************************");
		
	}
	
	public void getTopNDOcsUsingRankSearchFromW2() {
		LinkedList<Entry<Long, Double>> scoreList = CollectionUtils.retSortedScoreList(queryDocumentWeightFunction2Map);
		N = scoreList.size() >= N ? N : scoreList.size();
		
		System.out.println("*****************************************************************************");
		System.out.println("Documents retrieved from rank search based on W2 function");
		System.out.format("%-10s%-30s%-30s%-30s\n","Rank","Score","Document Indentifier","Heading");
		
		for(int i = 0; i < N; i++) {
			Entry<Long, Double> scoreEntry = scoreList.get(i);
			
			long docId = scoreEntry.getKey();
			double score = scoreEntry.getValue();
			File file = fileStatsList.get((int) (docId - 1)).getFile();
			XMLContentParser xmlContentParser = new XMLContentParser(file);
			String titleName = xmlContentParser.getTitleName();
			
			titleName = titleName.replace("\n", "");
			titleName = titleName.trim();
			
			System.out.format("%-10s%-30s%-30s%-30s\n", (i + 1), score, file.getName(), titleName);
		}
		
		System.out.println("*************************************************************************");
		System.out.println("Document vector based on W2 Function");
		
		for(int i = 0; i < N; i++) {
			System.out.println("====================================================");
			Entry<Long, Double> scoreEntry = scoreList.get(i);
			Long docId = scoreEntry.getKey();
			HashMap<String, Double> hash = docVectorMap2.get(docId);
			System.out.println("Document vector of query w.r.t to document " + docId);
			for(String term : hash.keySet()) {
				System.out.println("Term " + term + " Weight: " + hash.get(term));
			}
			System.out.println("======================================================");
			
		}
		System.out.println("****************************************************************************");
	}

}
