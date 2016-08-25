package com.ir.searchengine.service;

public class QueryResultStats {
	private String id;
	private String title;
	private String url;
	private String content;
	private Float score;
	
	public QueryResultStats() {
		id = "";
		title = "";
		url = "";
		content = "";
	}
	
	public QueryResultStats(String url, String title, String content) {
		super();
		this.title = title;
		this.url = url;
		this.content = content;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String s = "";
		s += "id: " + id + "\n";
		s += "title: " + title + "\n";
		s += "url: " + url + "\n";
		s += "content: " + content + "\n";
		s += "score: " + score + "\n";
		
		return s;
	}

}
