package com.ir.searchengine.service;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class GoogleAPISearch {

	public static List<SearchResults> googleSearch(String query) throws IOException, UnsupportedEncodingException {
		
		System.out.println(query);
		List<SearchResults> searchResultsList = new ArrayList<SearchResults>();
		
		try{

		for (int iter = 0; iter < 12; iter += 4) {

			String address = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0&start=" + iter + "&q=";
			String charset = "UTF-8";

			URL url = new URL(address + URLEncoder.encode(query, charset));
			Reader reader = new InputStreamReader(url.openStream(), charset);
			
			GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
			
			if(results == null) continue;

			int total = results.getResponseData().getResults().size();
			//System.out.println("total: " + total);

			// Show title and URL of each results
			for (int i = 0; i <= total - 1; i++) {
				SearchResults searchResults = new SearchResults();
				
				searchResults.setTitle(results.getResponseData().getResults().get(i).getTitle());
				searchResults.setUrl(results.getResponseData().getResults().get(i).getUrl());
				
				//System.out.println("Title: " + results.getResponseData().getResults().get(i).getTitle());
				//System.out.println("URL: " + results.getResponseData().getResults().get(i).getUrl() + "\n");
				searchResultsList.add(searchResults);

			}
		}
		}catch(Exception e){
			System.err.println("Unable to search Google");
		}
		System.out.println("***********************************************");
		System.out.println("Google search results for query " + query);
		for(SearchResults searchResults : searchResultsList) {
			System.out.println(searchResults);
		}
		System.out.println("************************************************");
		
		return searchResultsList;

	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		GoogleAPISearch.googleSearch("da vinci");
	}

}

class GoogleResults {

	private ResponseData responseData;

	public ResponseData getResponseData() {
		return responseData;
	}

	public void setResponseData(ResponseData responseData) {
		this.responseData = responseData;
	}

	public String toString() {
		return "ResponseData[" + responseData + "]";
	}

	static class ResponseData {
		private List<Result> results;

		public List<Result> getResults() {
			return results;
		}

		public void setResults(List<Result> results) {
			this.results = results;
		}

		public String toString() {
			return "Results[" + results + "]";
		}
	}

	static class Result {
		private String url;
		private String title;

		public String getUrl() {
			return url;
		}

		public String getTitle() {
			return title;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String toString() {
			return "Result[url:" + url + ",title:" + title + "]";
		}
	}
}
