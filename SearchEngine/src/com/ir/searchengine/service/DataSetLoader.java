package com.ir.searchengine.service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author RahulAravind
 * 
 * This is a one time execution. Read the file, load the contents into hash map and serialize the object
 *
 */

public class DataSetLoader {
	
	private static String fileName = "C:\\Users\\RahulAravind\\workspace\\java\\IR_Project\\Rankings.txt";
	private static HashMap<String, Float> rankingsMap = new HashMap<String, Float>();
	
	public static void main(String[] args) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			String str;
			while((str = br.readLine()) != null) {
				String[] token = str.split("=");
				String url = token[0].trim();
				String score = token[1].trim();
				
				Float rank = Float.parseFloat(score);
				
				rankingsMap.put(url, rank);
			}
		} catch(FileNotFoundException foe) {
			foe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		ObjectSerializer.serializeObject("Rankings", rankingsMap);
		
		HashMap<String, Float> urlhash = (HashMap<String, Float>) ObjectSerializer.readSerializedObject("Rankings");
		
		for(String key : urlhash.keySet()) {
			System.out.println(key + " " + urlhash.get(key));
		}
	}

}
