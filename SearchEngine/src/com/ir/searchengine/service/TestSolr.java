package com.ir.searchengine.service;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class TestSolr {
	
	public static void main(String args[]) {
		String url = "http://localhost:8983/solr/collection1";
		HttpSolrServer server = new HttpSolrServer(url);
		SolrQuery query = new SolrQuery();
		query.setQuery("content:most%famous%Davinci%painter");
		QueryResponse rsp;
		
		try {
			rsp = server.query(query);
			
		} catch(SolrServerException sse) {
			sse.printStackTrace();
			return;
		}
		
		// Display the query results
		
		SolrDocumentList docs = rsp.getResults();
		
		for(int i = 0; i < docs.size(); i++) {
			System.out.println("Title: " + docs.get(i).get("title").toString() + " Link: " + docs.get(i).get("url").toString());
			System.out.println("Content: " + docs.get(i).get("content"));
		}
	}

}
