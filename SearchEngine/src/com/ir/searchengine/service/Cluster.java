package com.ir.searchengine.service;
import java.util.LinkedList;

public class Cluster {

	double[] centroid;
	LinkedList<Integer> docs = new LinkedList<Integer>();
	
	Cluster() {
	}

	Cluster(double[] centroid, LinkedList<Integer> docs) {
		this.centroid = centroid;
		this.docs = docs;
	}

	public double[] getCentroid() {
		return centroid;
	}

	public void setCentroid(double[] centroid) {
		this.centroid = centroid;
	}

	public LinkedList<Integer> getDocs() {
		return docs;
	}

	public void setDocs(LinkedList<Integer> docs) {
		this.docs = docs;
	}

}
