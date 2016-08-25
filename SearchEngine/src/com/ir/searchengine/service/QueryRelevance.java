package com.ir.searchengine.service;
import java.util.ArrayList;

/**
 * 
 * a bean class to store the query and its search results
 *
 */
public class QueryRelevance {
	private String query;
	private ArrayList<String> indexedQuery;
	private ArrayList<QueryResultStats> queryResultStatsList;
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public ArrayList<String> getIndexedQuery() {
		return indexedQuery;
	}
	
	public void setIndexedQuery(ArrayList<String> indexedQuery) {
		this.indexedQuery = indexedQuery;
	}
	
	public ArrayList<QueryResultStats> getQueryResultStatsList() {
		return queryResultStatsList;
	}
	
	public void setQueryResultStatsList(ArrayList<QueryResultStats> queryResultStatsList) {
		this.queryResultStatsList = queryResultStatsList;
	}
	
}
