package com.ir.searchengine.service;
import java.util.ArrayList;

import org.apache.solr.common.SolrDocumentList;

public class InformationExtractor {
	
	private SolrDocumentList querySearchResults;
	private ArrayList<QueryResultStats> queryResultStatsList;
	
	public InformationExtractor(SolrDocumentList querySearchResults) {
		this.querySearchResults = querySearchResults;
		queryResultStatsList = new ArrayList<QueryResultStats>();
	}
	
	public void extractInformation() {
		
		int topN = 10;
		
		if(querySearchResults.size() < 10) {
			topN = querySearchResults.size();
		}
		
		for(int i = 0; i < topN; i++) {
			QueryResultStats queryResultStats = new QueryResultStats();
			queryResultStats.setId((String) querySearchResults.get(i).get("id"));
			queryResultStats.setTitle((String) querySearchResults.get(i).get("title"));
			queryResultStats.setUrl((String) querySearchResults.get(i).get("url"));
			queryResultStats.setContent((String) querySearchResults.get(i).get("content"));
			queryResultStats.setScore((Float)querySearchResults.get(i).get("score"));
			
			//float score = (float) querySearchResults.get(i).get("score");
			//System.out.println("#DEBUG score " + score);
			
			queryResultStatsList.add(queryResultStats);
		}
	}
	
	public ArrayList<QueryResultStats> getQueryResultStatsList() {
		return queryResultStatsList;
	}

	public void setQueryResultStatsList(ArrayList<QueryResultStats> queryResultStatsList) {
		this.queryResultStatsList = queryResultStatsList;
	}

}
