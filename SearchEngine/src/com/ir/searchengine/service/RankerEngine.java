package com.ir.searchengine.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;


public class RankerEngine {
	
	private QueryRelevance queryRelevance;
	private static final String SOLR_DATA_FOLDER = "C:\\Users\\RahulAravind\\Desktop\\Books\\Spring 16\\Information Retrieval\\Project\\solr-4.10.4\\solr-4.10.4\\example\\solr\\collection1\\data\\";
	private static HashMap<String, Float> rankingsMap;
	private HashMap<String, Float> authorityScore;
	private static final String rankingsFile = "C:\\Users\\RahulAravind\\workspace\\java\\SearchEngine\\Rankings";
	private ArrayList<QueryResultStats> pageRankSearchResults;
	private static final String HUB_FILE = "external_Hitshub.txt";
	private static final String AUTHORITY_FILE = "external_Hitsauthority.txt";
	private ArrayList<QueryResultStats> hitRankSearchResults; 
	private static final String PART = "C:\\Users\\RahulAravind\\workspace\\java\\SearchEngine\\part-00000";
	
	public RankerEngine(QueryRelevance queryRelevance) {
		this.queryRelevance = queryRelevance;
		pageRankSearchResults = new ArrayList<QueryResultStats>();
		authorityScore = new HashMap<String, Float>();
		hitRankSearchResults = new ArrayList<QueryResultStats>();
	}
	
	public void orderResultsBasedOnHitScore() throws RuntimeException {
		
		//filterSearchResultsBasedOnPageRankScores();
		
		ArrayList<QueryResultStats> queryResultStatsList = queryRelevance.getQueryResultStatsList();
		constructWebGraph(queryResultStatsList);
		filterSearchResultsBasedOnHitRankScores();
		
		
	}
	
	public void constructWebGraph(ArrayList<QueryResultStats> queryResultStatsList) {
		
		ArrayList<String> topicUrls = new ArrayList<String>();
		
		for(QueryResultStats queryResultStats : queryResultStatsList) {
			String url = queryResultStats.getUrl();
			topicUrls.add(url);
		}
		
		long noOfEdges = 0;
		long noOfNodes = 0;
		
		Graph<String, Long> webg = new DirectedSparseMultigraph< String,Long>();
		
		File f1 = new File(PART);
		try {
			BufferedReader b1 = new BufferedReader(new FileReader(f1));
			String line = null;
			String[] url = new String[2];
			while((line = b1.readLine()) != null){
				
				String[] url1 = new String[6];
				
				if(line.contains("Inlinks:")){
					url = line.split("Inlinks:");
					//System.out.println(url[0]);
					if(topicUrls.contains(url[0])){
						if(!webg.containsVertex(url[0].trim())){
							webg.addVertex(url[0].trim());
							noOfNodes++;
						}
					}
				}						
				
				else if(line.contains("fromUrl:")){
					 url1= line.split(" ");
					 if(topicUrls.contains(url[0])){
						 if(!webg.containsVertex(url1[2].trim())){
							 webg.addVertex(url1[2].trim());
							 noOfNodes++;
						 }
					 noOfEdges++;
					 webg.addEdge(noOfEdges,url[0],url1[2]);
					 }
					 else if (topicUrls.contains(url1[2])){
						 
						 if(!webg.containsVertex(url1[2].trim())){
							 webg.addVertex(url1[2].trim());
							 noOfNodes++;
						 }
						 if(!webg.containsVertex(url[0].trim())){
								webg.addVertex(url[0].trim());
								noOfNodes++;
							}
						 noOfEdges++;
						 webg.addEdge(noOfEdges,url1[2],url[0]);
						 
					 }
				}
			
			}
					
			hitRanker(webg);
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("The file " + f1.getName() + " is not found....");
		} catch (IOException ioe) {
			throw new RuntimeException("IO Exception occurred!!!.");
		}
	}
	
	public void hitRanker(Graph webg) throws FileNotFoundException, UnsupportedEncodingException {
		HITS<String, Integer> ranker = new HITS<String, Integer>(webg,0.15);
		ranker.initialize();
		ranker.setMaxIterations(10);
		ranker.evaluate();
		Map<String, Double> result = new HashMap<String, Double>();
		Iterator i = webg.getVertices().iterator();
		
		while(i.hasNext()){
			String v= (String) i.next();
			result.put( v, ranker.getVertexScore(v).hub);
		}
		
		PrintWriter writer = new PrintWriter(SOLR_DATA_FOLDER + "external_Hitshub.txt", "UTF-8");
		
		
		for(Entry<String, Double> e1 : result.entrySet()){
			writer.println(e1.getKey()+"="+e1.getValue());
					
		}
		writer.close();
		PrintWriter writer1 = new PrintWriter(SOLR_DATA_FOLDER + "external_Hitsauthority.txt", "UTF-8");
		
		for(Entry<String, Double> e1 : result.entrySet()){
			writer1.println(e1.getKey()+"="+e1.getValue());					
		}
		writer1.close();		
	}
	
	public void loadAuthorityFile() {
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(SOLR_DATA_FOLDER + AUTHORITY_FILE));
			String str;
			while((str = br.readLine()) != null) {
				String[] token = str.split("=");
				String url = token[0].trim();
				String score = token[1].trim();
				
				Float rank = Float.parseFloat(score);
				
				authorityScore.put(url, rank);
			}
		} catch(FileNotFoundException foe) {
			foe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void filterSearchResultsBasedOnHitRankScores() {
		
		
		hitRankSearchResults.addAll(queryRelevance.getQueryResultStatsList());
		
		HashMap<String, Float> pageRankUrlMap = new HashMap<String, Float>();
		
		// hash the page rank urls.
		for(QueryResultStats searchResult : pageRankSearchResults) {
			String url = searchResult.getUrl();
			pageRankUrlMap.put(url, searchResult.getScore());
		}
		
		for(QueryResultStats searchResult : hitRankSearchResults) {
			String url = searchResult.getUrl().trim();
			Float urlScore = pageRankUrlMap.get(url);
			
			Float score;
			Float total;
			
			if((score = authorityScore.get(url)) != null) {
				total = urlScore + score;
				searchResult.setScore(total);
			}
		}
		
		CollectionUtils.sortSearchResults(hitRankSearchResults);
		
		/*System.out.println("***************************************************");
		System.out.println("Hit ranking algorithm");
		System.out.println("***************************************************");
		
		for(QueryResultStats queryResultStats : hitRankSearchResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("******************************************************");*/
	}
	
	public void filterSearchResultsBasedOnPageRankScores() {
		
		rankingsMap = (HashMap<String, Float>) ObjectSerializer.readSerializedObject(rankingsFile);
		
		
		pageRankSearchResults.addAll(queryRelevance.getQueryResultStatsList());
		
		for(QueryResultStats searchResult : pageRankSearchResults) {
			String url = searchResult.getUrl().trim();
			Float urlScore = searchResult.getScore();
			
			Float score;
			Float total;
			
			if((score = rankingsMap.get(url)) != null) {
				total = urlScore + score;
				searchResult.setScore(total);
			}
		}
		
		// sort the results based on score.
		CollectionUtils.sortSearchResults(pageRankSearchResults);
		
		/*System.out.println("***************************************************");
		System.out.println("Page ranking algorithm");
		System.out.println("***************************************************");
		
		for(QueryResultStats queryResultStats : pageRankSearchResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("******************************************************");*/
	}

	public ArrayList<QueryResultStats> getPageRankSearchResults() {
		return pageRankSearchResults;
	}
	
	public ArrayList<QueryResultStats> getHitRankSearchResults() {
		return hitRankSearchResults;
	}

}
