package com.ir.searchengine.service;
import java.util.ArrayList;

public class StopWords {

	public static String[] stopWordList = { "a", "all", "an", "and", "any", "ar", "are", "as", "at", "be", "been", "but", "by",
			"few", "follow", "for", "have", "he", "her", "here", "him", "his", "how", "i", "in", "is", "it", "its", "many", "me",
			"my", "none", "of", "on", "or", "our", "p", "underneath", "under", "she", "some", "to", "the", "their", "them", "there", "they", "that",
			"this", "thus", "us", "we", "was", "what", "when", "through", "where", "which", "who", "why", "will", "with", "you", "your" };
	
	
	public static void main(String args[]) {
		System.out.println(stopWordList.length);
	}
}
