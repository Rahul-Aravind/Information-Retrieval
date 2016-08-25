package com.ir.searchengine.managedbean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpSession;

import com.ir.searchengine.service.BingSearch;
import com.ir.searchengine.service.DataProvider;
import com.ir.searchengine.service.GoogleAPISearch;
import com.ir.searchengine.service.QueryEngine;
import com.ir.searchengine.service.QueryResultStats;
import com.ir.searchengine.service.SearchResults;
import com.ir.searchengine.vos.PopulateLine;
import com.ir.searchengine.vos.Result;

public class SearchResultsMB {

	private String queryString = "Sharavana";
	private String expandedQuery;
	private List<QueryResultStats> queryExpansionResult = null;
	private List<QueryResultStats> rankingResult = null;
	private List<QueryResultStats> clusteringResult = null;
	private List<QueryResultStats> noRankingResult = null;
	private List<QueryResultStats> hits = null;
	private List<SearchResults> googleResults = null;
	private List<QueryResultStats> bingResults = null;
	private boolean firstTime = true;
	private DataProvider dataProvider;

	/**
	 * Constructor class
	 */
	public SearchResultsMB() {
		System.out.println("Entering next page constructor");
		BingSearch bingSearch = new BingSearch();
	
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpSession httpSession = (HttpSession) externalContext.getSession(true);
		this.setQueryString(httpSession.getAttribute("query").toString());
		if (this.firstTime) {
			this.firstTime = false;
			this.performSearch();
			this.bingResults = bingSearch.getBingResults(this.queryString);
		}
	}

	public String getQueryString() {
		return queryString;
	}

	public List<QueryResultStats> getQueryExpansionResult() {
		return queryExpansionResult;
	}


	public List<QueryResultStats> getRankingResult() {
		return rankingResult;
	}

	public List<QueryResultStats> getClusteringResult() {
		return clusteringResult;
	}

	public List<QueryResultStats> getNoRankingResult() {
		return noRankingResult;
	}

	public List<QueryResultStats> getHits() {
		return hits;
	}

	public List<SearchResults> getGoogleResults() {
		return googleResults;
	}

	public List<QueryResultStats> getBingResults() {
		return bingResults;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setQueryExpansionResult(List<QueryResultStats> queryExpansionResult) {
		this.queryExpansionResult = queryExpansionResult;
	}

	public void setRankingResult(List<QueryResultStats> rankingResult) {
		this.rankingResult = rankingResult;
	}

	public void setClusteringResult(List<QueryResultStats> clusteringResult) {
		this.clusteringResult = clusteringResult;
	}

	public void setNoRankingResult(List<QueryResultStats> noRankingResult) {
		this.noRankingResult = noRankingResult;
	}

	public void setHits(List<QueryResultStats> hits) {
		this.hits = hits;
	}

	public void setGoogleResults(List<SearchResults> googleResults) {
		this.googleResults = googleResults;
	}

	public void setBingResults(List<QueryResultStats> bingResults) {
		this.bingResults = bingResults;
	}

	/**
	 * @return the firstTime
	 */
	public boolean isFirstTime() {
		return firstTime;
	}

	/**
	 * @param firstTime
	 *            the firstTime to set
	 */
	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	/**
	 * @return the expandedQuery
	 */
	public String getExpandedQuery() {
		return expandedQuery;
	}

	/**
	 * @return the dataProvider
	 */
	public DataProvider getDataProvider() {
		return dataProvider;
	}

	/**
	 * @param expandedQuery the expandedQuery to set
	 */
	public void setExpandedQuery(String expandedQuery) {
		this.expandedQuery = expandedQuery;
	}

	/**
	 * @param dataProvider the dataProvider to set
	 */
	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public String performSearch() {
		System.out.println("Inside second search");
		BingSearch bingSearch = new BingSearch();
		this.bingResults = new ArrayList<QueryResultStats>();
		this.bingResults = bingSearch.getBingResults(queryString);
		System.out.println("Bing search complete");
		try {
			this.googleResults = GoogleAPISearch.googleSearch(queryString);
			System.out.println("google search complete");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Calling Driver");
		callDriver();
		
		populateDataForUI();
		return "success";
	}
	
	public void populateDataForUI() {
		this.clusteringResult = dataProvider.getClusterResults();
		this.noRankingResult = dataProvider.getNormalRelvanceResults();
		this.rankingResult = dataProvider.getPageRankResults();
		this.hits = dataProvider.getHitRankResults();
		this.queryExpansionResult = dataProvider.getQueryExpansionResults();
		this.expandedQuery = dataProvider.getExpandedQuery();
	}
	
	public void callDriver() {
		QueryEngine queryEngine = new QueryEngine(queryString, true, true, true, true);		
		queryEngine.executeQuery();
		dataProvider = queryEngine.getDataProvider();
		System.out.println("End of Driver");
	}
}
