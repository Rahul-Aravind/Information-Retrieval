package com.ir.searchengine.service;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import java.util.ArrayList;

public class QueryEngine {
	
	private String query;
	private boolean checkQueryExpansion;
	private boolean checkClustering;
	private boolean pagerank;
	private boolean hits;
	private HttpSolrServer httpSolrServer;
	private static ConnectionManager connectionManager;
	private final String contentTag = "content:";
	private QueryRelevance queryRelevance;
	private DataProvider dataProvider;
	
	public QueryEngine(String query, boolean checkQueryExpansion, boolean checkClustering, boolean pagerank, boolean hits) {
		this.query = query;
		this.checkQueryExpansion = checkQueryExpansion;
		this.checkClustering = checkClustering;
		this.pagerank = pagerank;
		this.hits = hits;
		connectionManager = new ConnectionManager();
		dataProvider = new DataProvider();
	}
	
	public String prepareQueryStatement(QueryStats queryStats) {
		StringBuilder sb = new StringBuilder();
		
		for(String queryTerm : queryStats.getIndexedQuery()) {
			sb.append(queryTerm + "%");
		}
		
		String preparedQuery = sb.toString();
		preparedQuery = preparedQuery.trim();
		
		if(preparedQuery.length() == 0) {
			return null;
		} else {
			preparedQuery = preparedQuery.substring(0, preparedQuery.length() - 1);
		}
		
		//String finalQuery = "title:" + preparedQuery + "^2" + " OR " + "content:" + preparedQuery;
		String finalQuery = "content:" + preparedQuery;
		//System.out.println("#DEBUG final query " + finalQuery);
		
		return finalQuery;
	}

	public void executeQuery() {
		QueryProcessor queryProcessor = new QueryProcessor(query);
		
		queryProcessor.queryTokenizer(query);
		QueryStats queryStats = queryProcessor.getQueryStats();
		
		String preparedQuery = prepareQueryStatement(queryStats);
		//System.out.println("#DEBUG prepared query " + preparedQuery);
		
		if(preparedQuery == null) {
			throw new RuntimeException("Query object is Null. Please enter a valid query!.");
		}
		
		
		httpSolrServer = connectionManager.getConnection();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(preparedQuery);
		solrQuery.setParam("fl", "id,title,url,content,score");
		solrQuery.setSort("score", ORDER.desc);
		
		QueryResponse queryResponse;
		
		try {
			queryResponse = httpSolrServer.query(solrQuery);
			SolrDocumentList queryDocuments = queryResponse.getResults();
			System.out.println("Documents size " + queryDocuments.size());
			
			retrieveInformationFromQueryDocuments(queryStats, queryDocuments);
			
		} catch(SolrServerException solrServerException) {
			throw new RuntimeException("Connection Establishment to the solr Server failed!!. Please check SolrServer logs.");
		} catch(RemoteSolrException remoteSolrException) {
			throw new RuntimeException("No search results found for the query: " + query +". Please try again with different query!.");
		}
		
	}
	
	private void retrieveInformationFromQueryDocuments(QueryStats queryStats, SolrDocumentList queryDocuments) {
		
		// Search results
		
		InformationExtractor informationExtractor = new InformationExtractor(queryDocuments);
		informationExtractor.extractInformation();
		
		queryRelevance = new QueryRelevance();
		
		queryRelevance.setQuery(query);
		queryRelevance.setIndexedQuery(queryStats.getIndexedQuery());
		queryRelevance.setQueryResultStatsList(informationExtractor.getQueryResultStatsList());
		
		dataProvider.setNormalRelvanceResults(queryRelevance.getQueryResultStatsList());
		
		QueryExpansionEngine queryExpansionEngine;
		if(checkQueryExpansion) {
			
			long startTime = System.currentTimeMillis();
			queryExpansionEngine = new QueryExpansionEngine(queryRelevance);
			queryExpansionEngine.expandQuery();
			queryExpansionEngine.prepareExpandedQuery();
			
			System.out.println("Size " + queryExpansionEngine.getExpandedQuerySearchResults().size());
			
			dataProvider.setQueryExpansionResults(queryExpansionEngine.getExpandedQuerySearchResults());
			dataProvider.setExpandedQuery(queryExpansionEngine.getPreparedExpandedQuery());
			long endTime = System.currentTimeMillis();
			
			System.out.println("Time taken for query expansion: " + (endTime - startTime));
		}
		
		ClusteringEngine clusteringEngine;
		
		if(checkClustering) {
			long startTime = System.currentTimeMillis();
			clusteringEngine = new ClusteringEngine(queryRelevance);
			clusteringEngine.clusterSearchResults();
		
			ArrayList<QueryResultStats> clusterResults = clusteringEngine.getRelevantClusterResults();
			
			dataProvider.setClusterResults(clusterResults);
			
			/*System.out.println("*************************************************");
			System.out.println("Clustering results......");
		
			for(QueryResultStats clusterResult : clusterResults) {
				System.out.println(clusterResult);
			}
			
			System.out.println("*************************************************");*/
			long endTime = System.currentTimeMillis();
			
			System.out.println("Time taken for clustering: " + (endTime - startTime));
		}
		
		RankerEngine rankerEngine = null;
		
		if(pagerank) {
			long startTime = System.currentTimeMillis();
			rankerEngine = new RankerEngine(queryRelevance);
			rankerEngine.filterSearchResultsBasedOnPageRankScores();
			dataProvider.setPageRankResults(rankerEngine.getPageRankSearchResults());
			long endTime = System.currentTimeMillis();
			System.out.println("Time taken for Page ranking: " + (endTime - startTime));
		}
		
		if(hits) {
			long startTime = System.currentTimeMillis();
			//rankerEngine = new RankerEngine(queryRelevance);
			rankerEngine.orderResultsBasedOnHitScore();
			dataProvider.setHitRankResults(rankerEngine.getHitRankSearchResults());
			long endTime = System.currentTimeMillis();
			System.out.println("Time taken for Hit ranking: " + (endTime - startTime));
		}
	}

	public QueryRelevance getQueryRelevance() {
		return queryRelevance;
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	

}
