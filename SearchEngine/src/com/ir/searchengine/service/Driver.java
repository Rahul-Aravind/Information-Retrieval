package com.ir.searchengine.service;
import java.util.ArrayList;

public class Driver {
	
	public static void main(String[] args) {
		String query = "mona lisa";
		QueryEngine queryEngine = new QueryEngine(query, true, true, true, true);
		
		queryEngine.executeQuery();
		
		DataProvider dataProvider = queryEngine.getDataProvider();
		
		ArrayList<QueryResultStats> normalRelResults = dataProvider.getNormalRelvanceResults();
		
		System.out.println("***************************************************");
		System.out.println("Normal relevance results..........");
		
		for(QueryResultStats queryResultStats : normalRelResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("***************************************************");
		
		ArrayList<QueryResultStats> pageRankResults = dataProvider.getPageRankResults();
		
		System.out.println("***************************************************");
		System.out.println("Page rank results..........");
		
		for(QueryResultStats queryResultStats : pageRankResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("***************************************************");
		
		ArrayList<QueryResultStats> hitRankResults = dataProvider.getHitRankResults();
		
		System.out.println("***************************************************");
		System.out.println("hit rank results..........");
		
		for(QueryResultStats queryResultStats : hitRankResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("***************************************************");
		
		ArrayList<QueryResultStats> clusterResults = dataProvider.getClusterResults();
		
		System.out.println("***************************************************");
		System.out.println("cluster results..........");
		
		for(QueryResultStats queryResultStats : clusterResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("***************************************************");
		
		ArrayList<QueryResultStats> expandedQueryResults = dataProvider.getQueryExpansionResults();
		
		System.out.println("***************************************************");
		System.out.println("expanded query results..........");
		
		for(QueryResultStats queryResultStats : expandedQueryResults) {
			System.out.println(queryResultStats);
		}
		
		System.out.println("***************************************************");
	}

}
