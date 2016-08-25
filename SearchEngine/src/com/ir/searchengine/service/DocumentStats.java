package com.ir.searchengine.service;
import java.util.ArrayList;

public class DocumentStats {
	
	private String id;
	private ArrayList<String> docVector;
	private ArrayList<String> lemmaVector;
	
	public DocumentStats() {
		docVector = new ArrayList<String>();
		lemmaVector = new ArrayList<String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getDocVector() {
		return docVector;
	}

	public void setDocVector(ArrayList<String> docVector) {
		this.docVector = docVector;
	}
	
	public ArrayList<String> getLemmaVector() {
		return lemmaVector;
	}

	public void setLemmaVector(ArrayList<String> lemmaVector) {
		this.lemmaVector = lemmaVector;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String s = "";
		s += "id: " + id + "\n";
		s += "content: " + docVector + "\n";
		
		return s;
	}

}
