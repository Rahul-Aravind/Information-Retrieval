package com.ir.searchengine.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class ClusteringEngine {
	
	private QueryRelevance queryRelevance;
	private DocumentProcessor documentProcessor;
	private HashSet<String> vocabularySet;
	private int[][] vectorMatrix;
	private HashMap<String, Integer> dictMap;
	private static final int clusterSize = 3;
	private static final int iterations = 10;
	private int NDocs;
	private int vocabSize;
	private ArrayList<ClusterResult> clusterResultSet;
	private ArrayList<QueryResultStats> relevantClusterResults;
	
	public ClusteringEngine(QueryRelevance queryRelevance) {
		this.queryRelevance = queryRelevance;
		dictMap = new HashMap<String, Integer>();
		NDocs = queryRelevance.getQueryResultStatsList().size();
		clusterResultSet = new ArrayList<ClusterResult>();
		relevantClusterResults = new ArrayList<QueryResultStats>();
	}
	
	public void clusterSearchResults() {
		documentProcessor = new DocumentProcessor(queryRelevance.getQueryResultStatsList());
		documentProcessor.processQueryResults();
		
		vocabularySet = documentProcessor.getVocabulary();
		vocabSize = vocabularySet.size();
		
		createDictionaryMap();
		createVectorMatrix();
		KMeans();
	}
	
	public void createDictionaryMap() {
		int i = 0;
		for(String vTerm : vocabularySet) {
			dictMap.put(vTerm, i);
			i += 1;
		}
	}
	
	public void createVectorMatrix() {
		
		vectorMatrix = new int[NDocs][vocabSize];
		
		// initializing the vector matrix
		for(int i = 0; i < NDocs; i++) {
			for(String vTerm : vocabularySet) {
				vectorMatrix[i][dictMap.get(vTerm)] = 0;
			}
		}
		
		ArrayList<DocumentStats> documentStatsList = documentProcessor.getDocumentStatsList();
		
		int i = 0;
		for(DocumentStats documentStats : documentStatsList) {
			ArrayList<String> lemmaVector = documentStats.getLemmaVector();
			
			for(String lemma : lemmaVector) {
				vectorMatrix[i][dictMap.get(lemma)] += 1;
			}
			i += 1;
		}
	}
	
	public void KMeans() {
		
		int NDocs = documentProcessor.getDocumentStatsList().size();
		int vocabSize = vocabularySet.size();
		
		double[][] oldCentroids = new double[clusterSize][vocabSize];
		Cluster[] clusterArr = new Cluster[clusterSize];
		
		LinkedList<Integer> docs = new LinkedList<Integer>();
		
		ArrayList<Integer> firstCentroid = new ArrayList<Integer>();
		int temp = -1;
		for(int i = 0; i < clusterSize; i++) {
			do {
				Random rand = new Random();
				temp = (int) rand.nextInt(NDocs - 1 - 1);
			} while(firstCentroid.contains(temp));
			firstCentroid.add(temp);
			int randDoc = temp;
			
			double[] centroid = new double[vocabSize];
			for(int j = 0; j < vocabSize; j++) {
				centroid[j] = vectorMatrix[randDoc][j];
				oldCentroids[i][j] = centroid[j];
			}
			
			Cluster cluster = new Cluster();
			cluster.setCentroid(centroid);
			cluster.setDocs(docs);
			clusterArr[i] = cluster;
		}
		
		for(int i = 0; i < iterations; i++) {
			clusterDocs(clusterArr);
			
			for(int j = 0; j < clusterSize; j++) {
				for(int k = 0; k < vocabSize; k++) {
					oldCentroids[j][k] = clusterArr[j].getCentroid()[k];
				}
			}
			
			getNewCentroids(clusterArr);
		}
		
		attachDocumentsToClusters(clusterArr);
		fetchRelevantClusters();
		
	}
	
	public void attachDocumentsToClusters(Cluster[] clusterArr) {
		
		ArrayList<QueryResultStats> queryResults = queryRelevance.getQueryResultStatsList();
		
		for(int i = 0; i < clusterSize; i++) {
			ClusterResult clusterResult = new ClusterResult(i + 1);
			ArrayList<QueryResultStats> docs = clusterResult.getClusterDocs();
			
			for(int j = 0; j < clusterArr[i].getDocs().size(); j++) {
				int docId = clusterArr[i].getDocs().get(j);
				docs.add(queryResults.get(docId));
			}
			
			clusterResult.setClusterDocs(docs);
			clusterResultSet.add(clusterResult);
		}
	}
	
	public void getNewCentroids(Cluster[] clusterArr) {
		
		for(int i = 0; i < clusterSize; i++){
			for(int j = 0; j < vocabSize; j++){
				double sum = 0.0d;
				for(int l = 0; l < clusterArr[i].getDocs().size(); l++){
					sum += vectorMatrix[clusterArr[i].getDocs().get(l)][j];
				}
				clusterArr[i].getCentroid()[j] = (Double)(sum / clusterArr[i].getDocs().size());
			}	
		}
	}
	
	public void clusterDocs(Cluster[] clusterArr) {
		
		for(int i = 0; i < clusterSize; i++) {
			LinkedList<Integer> ll = new LinkedList<Integer>();
			clusterArr[i].setDocs(ll);
		}
		
		double[] distance = new double[clusterSize];
		
		for(int i = 0; i < NDocs; i++) {
			distance[0] = getDistance(i, clusterArr[0].getCentroid());
			distance[1] = getDistance(i, clusterArr[1].getCentroid());
			distance[2] = getDistance(i, clusterArr[2].getCentroid());
			
			if(distance[0] < distance[1]) {
				if(distance[0] < distance[2]) {
					clusterArr[0].getDocs().add(i);
				} else {
					clusterArr[2].getDocs().add(i);
				}
				
			} else {
					if(distance[1] < distance[2]){
						clusterArr[1].getDocs().add(i);
					} else {
						clusterArr[2].getDocs().add(i);
					}
			}
		}
		
	}
	
	double getDistance(int i, double[] centroid) {
		double sum = 0.0d;
		
		for(int j = 0; j < vocabSize; j++) {
			sum += Math.pow(vectorMatrix[i][j] - centroid[j], 2);
		}
		
		return Math.sqrt(sum);
	}

	public ArrayList<ClusterResult> getClusterResultSet() {
		return clusterResultSet;
	}
	
	public void fetchRelevantClusters() {
		
		ArrayList<QueryResultStats> queryResults = queryRelevance.getQueryResultStatsList();
		ArrayList<ClusterResult> clusterResults = getClusterResultSet();
		
		String relevantId = queryResults.get(0).getId();
		
		int i = 0;
		int cluster = 0;
		boolean flag = false;
		
		for(ClusterResult clusterResult : clusterResults) {
			ArrayList<QueryResultStats> queryRes = clusterResult.getClusterDocs();
			for(QueryResultStats queryResultStats : queryRes) {
				if(queryResultStats.getId().equals(relevantId)) {
					cluster = i;
					flag = true;
				}
			}
			
			if(flag) {
				relevantClusterResults.addAll(queryRes);
				break;
			}
			i++;
		}
		
		if(relevantClusterResults.size() > 0) {
			for(ClusterResult clusterResult : clusterResults) {
				if(clusterResult.getClusterNumber() != cluster) {
					relevantClusterResults.addAll(clusterResult.getClusterDocs());
				}
			}
		} else {
			for(ClusterResult clusterResult : clusterResults) {
				relevantClusterResults.addAll(clusterResult.getClusterDocs());
			}
		}
	}

	public ArrayList<QueryResultStats> getRelevantClusterResults() {
		return relevantClusterResults;
	}
	
	
	
}
