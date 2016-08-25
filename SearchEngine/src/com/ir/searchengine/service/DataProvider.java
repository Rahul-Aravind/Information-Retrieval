package com.ir.searchengine.service;
import java.util.ArrayList;

public class DataProvider {
	
	private ArrayList<QueryResultStats> normalRelvanceResults;
	private ArrayList<QueryResultStats> pageRankResults;
	private ArrayList<QueryResultStats> hitRankResults;
	private ArrayList<QueryResultStats> clusterResults;
	private ArrayList<QueryResultStats> queryExpansionResults;
	private String expandedQuery;
	
	public DataProvider() {
	}
	
	public ArrayList<QueryResultStats> getNormalRelvanceResults() {
		return normalRelvanceResults;
	}
	
	public ArrayList<QueryResultStats> getPageRankResults() {
		return pageRankResults;
	}
	
	public ArrayList<QueryResultStats> getHitRankResults() {
		return hitRankResults;
	}
	
	public ArrayList<QueryResultStats> getClusterResults() {
		return clusterResults;
	}
	
	public ArrayList<QueryResultStats> getQueryExpansionResults() {
		return queryExpansionResults;
	}
	
	public void setNormalRelvanceResults(ArrayList<QueryResultStats> normalRelvanceResults) {
		this.normalRelvanceResults = normalRelvanceResults;
	}
	
	public void setPageRankResults(ArrayList<QueryResultStats> pageRankResults) {
		this.pageRankResults = pageRankResults;
	}
	
	public void setHitRankResults(ArrayList<QueryResultStats> hitRankResults) {
		this.hitRankResults = hitRankResults;
	}
	
	public void setClusterResults(ArrayList<QueryResultStats> clusterResults) {
		this.clusterResults = clusterResults;
	}
	
	public void setQueryExpansionResults(ArrayList<QueryResultStats> queryExpansionResults) {
		this.queryExpansionResults = queryExpansionResults;
	}

	public String getExpandedQuery() {
		return expandedQuery;
	}

	public void setExpandedQuery(String expandedQuery) {
		this.expandedQuery = expandedQuery;
	}
}
