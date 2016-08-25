package com.ir.searchengine.service;

public class ClusterDriver {
	
	public static void main(String[] args) {
		
		String query = "Mona";
		
		QueryEngine queryEngine = new QueryEngine(query, false, false, true, false);
		queryEngine.executeQuery();
		
	}

}
