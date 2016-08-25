package com.ir.searchengine.service;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryStats {
	
	private String queryStr;
	private ArrayList<String> indexedQuery;
	private HashMap<String, Integer> queryTokenMap;
	
	public QueryStats(String queryStr) {
		this.queryStr = queryStr;
		indexedQuery = new ArrayList<String>();
		queryTokenMap = new HashMap<String, Integer>();
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public ArrayList<String> getIndexedQuery() {
		return indexedQuery;
	}

	public void setIndexedQuery(ArrayList<String> indexedQuery) {
		this.indexedQuery = indexedQuery;
	}

	public HashMap<String, Integer> getQueryTokenMap() {
		return queryTokenMap;
	}

	public void setQueryTokenMap(HashMap<String, Integer> queryTokenMap) {
		this.queryTokenMap = queryTokenMap;
	}

}
