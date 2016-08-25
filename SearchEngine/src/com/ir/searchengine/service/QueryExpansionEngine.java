package com.ir.searchengine.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

public class QueryExpansionEngine {
	
	private QueryRelevance queryRelevance;
	private HashMap<String, HashMap<String, Double>> metricClusterMap;
	private DocumentProcessor documentProcessor;
	private static final int clusterSize = 3;
	private ArrayList<String> expandedQuery;
	private ArrayList<QueryResultStats> expandedQuerySearchResults;
	private String preparedExpandedQuery;
	
	public QueryExpansionEngine(QueryRelevance queryRelevance) {
		this.queryRelevance = queryRelevance;
		metricClusterMap = new HashMap<String, HashMap<String, Double>>();
		expandedQuery = new ArrayList<String>();
	}
	
	public void expandQuery() {
		
		documentProcessor = new DocumentProcessor(queryRelevance.getQueryResultStatsList());
		documentProcessor.processQueryResults();
		
		computeMetricCorrelations();
		
	}
	
	/**
	 *  metric cluster formation
	 */
	public void computeMetricCorrelations() {
		ArrayList<String> queryTerms = queryRelevance.getIndexedQuery();
		HashSet<String> vocabulary = documentProcessor.getVocabulary();
		HashMap<String, HashSet<String>> termVocabulary = documentProcessor.getDerivationallyRelatedTerms();
		ArrayList<DocumentStats> documentStatsList = documentProcessor.getDocumentStatsList();
		
		for(String queryTerm : queryTerms) {
			HashSet<String> queryTermVocab = termVocabulary.get(queryTerm);
			
			if(queryTermVocab == null) continue;
			
			for(String dicTerm : vocabulary) {
				HashSet<String> dicTermVocab = termVocabulary.get(dicTerm);
				
				double val = 0.0d;
				
				// Iterate over the vocab set of query term
				for(String qTerm : queryTermVocab) {
					
					// Iterate over the vocab set of term in the dictionary
					for(String dTerm : dicTermVocab) {
						
						// Iterate over all documents
						for(DocumentStats documentStats : documentStatsList) {
							ArrayList<String> docVector = documentStats.getDocVector();
							
							if(qTerm.equals(dTerm)) {
								//System.out.println("#DEBUG " + qTerm + " " + dTerm);
								continue;
							}
							
							int r1 = docVector.indexOf(qTerm);
							int r2 = docVector.indexOf(dTerm);
							
							if(r1 != -1 && r2 != -1) {
								double distance = Math.abs(r1 - r2);
								val += (1 / distance);
							}
						}
					}
				}
				
				//Normalize the metric cluster values
				double qVecSize = termVocabulary.get(queryTerm).size();
				double dVecSize = termVocabulary.get(dicTerm).size();
				val = val / (qVecSize * dVecSize);
				
				//System.out.println("#DEBUG val " + val + " product " + (qVecSize * dVecSize));
				if(val != 0.0d) {
					
					HashMap<String, Double> hash;
					
					if((hash = metricClusterMap.get(queryTerm)) == null) {
						hash = new HashMap<String, Double>();
						hash.put(dicTerm, val);
						metricClusterMap.put(queryTerm, hash);
					} else {
						hash.put(dicTerm, val);
						metricClusterMap.put(queryTerm, hash);
					}
				}
			}
		}
	}
	
	public void prepareExpandedQuery() {
		ArrayList<String> queryTerms = queryRelevance.getIndexedQuery();
		
		ArrayList<String> expandedQueryTerms = new ArrayList<String>();
		
		for(String queryTerm : queryTerms) {
			LinkedList<Entry<String, Double>> sortedLL = CollectionUtils.convHashMapToSortedLL(metricClusterMap.get(queryTerm));
			
			if(!expandedQuery.contains(queryTerm)) {
				expandedQuery.add(queryTerm);
			}
			
			for(int i = 0; i < clusterSize; i++) {
				Entry<String, Double> entry = sortedLL.get(i);
				if(!expandedQueryTerms.contains(entry.getKey())) {
					expandedQueryTerms.add(entry.getKey());
				}
				//System.out.println(queryTerm + "_" + entry.getKey() + " " + entry.getValue());
			}
		}
		
		for(String queryTerm : expandedQueryTerms) {
			if(!expandedQuery.contains(queryTerm)) {
				expandedQuery.add(queryTerm);
			}
		}
		
		preparedExpandedQuery = "";
		
		for(String query : expandedQuery) {
			preparedExpandedQuery += query + " ";
		}
		
		QueryEngine queryEngine = new QueryEngine(preparedExpandedQuery, false, false, false, false);
		queryEngine.executeQuery();
		
		expandedQuerySearchResults = queryEngine.getQueryRelevance().getQueryResultStatsList();
		
		System.out.println("********************************************************");
		System.out.println("Query search results after query expansion............");
		/*for(QueryResultStats queryResultStats : expandedQuerySearchResults) {
			System.out.println(queryResultStats);
		}
		System.out.println("*********************************************************");*/
	}

	public ArrayList<QueryResultStats> getExpandedQuerySearchResults() {
		return expandedQuerySearchResults;
	}

	public String getPreparedExpandedQuery() {
		return preparedExpandedQuery;
	}

}
