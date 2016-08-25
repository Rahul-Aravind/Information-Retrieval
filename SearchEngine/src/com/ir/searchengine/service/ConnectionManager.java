package com.ir.searchengine.service;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class ConnectionManager {
	
	private static final String url = "http://localhost:8983/solr/collection1";
	private HttpSolrServer httpSolrServer;
	
	public ConnectionManager() {
		httpSolrServer = null;
	}
	
	public HttpSolrServer getConnection() {
		
		if(httpSolrServer == null) {
			httpSolrServer = new HttpSolrServer(url);
		}
		
		return httpSolrServer;
	}

}
