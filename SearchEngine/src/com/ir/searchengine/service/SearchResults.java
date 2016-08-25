package com.ir.searchengine.service;

public class SearchResults {
	
	private String url;
	private String title;
	
	SearchResults() {
	}

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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "";
		s += "title: " + title + "\n";
		s += "url: " + url + "\n";
		
		return s;
	}

}
