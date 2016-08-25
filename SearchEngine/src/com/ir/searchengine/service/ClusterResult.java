package com.ir.searchengine.service;
import java.util.ArrayList;

public class ClusterResult {
	
	private int clusterNumber;
	private String label;
	private ArrayList<QueryResultStats> clusterDocs;
	
	public ClusterResult(int clusterNumber) {
		this.clusterNumber = clusterNumber;
		clusterDocs = new ArrayList<QueryResultStats>();
	}

	public int getClusterNumber() {
		return clusterNumber;
	}

	public String getLabel() {
		return label;
	}

	public ArrayList<QueryResultStats> getClusterDocs() {
		return clusterDocs;
	}

	public void setClusterNumber(int clusterNumber) {
		this.clusterNumber = clusterNumber;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setClusterDocs(ArrayList<QueryResultStats> clusterDocs) {
		this.clusterDocs = clusterDocs;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "";
		s += "id: " + clusterNumber + "\n";
		s += "Query Results " + clusterDocs;
		
		return s;
	}

}
