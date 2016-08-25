package com.ir.searchengine.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DocumentProcessor {
	
	private ArrayList<QueryResultStats> queryResultStatsList;
	private HashMap<String, HashSet<String>> derivationallyRelatedTerms;
	private static ArrayList<String> stopWords;
	private static Lemmatizer lemmatizer;
	private HashSet<String> vocabulary;
	private ArrayList<DocumentStats> documentStatsList;
	
	public DocumentProcessor(ArrayList<QueryResultStats> queryResultStatsList) {
		this.queryResultStatsList = queryResultStatsList;
		derivationallyRelatedTerms = new HashMap<String, HashSet<String>>();
		stopWords = new ArrayList<String>();
		lemmatizer = new Lemmatizer();
		documentStatsList = new ArrayList<DocumentStats>();
		vocabulary = new HashSet<String>();
	}
	
	private void loadStopWords() {
		for(String word : StopWords.stopWordList) {
			stopWords.add(word);
		}
	}
	
	public void processQueryResults() {
		loadStopWords();
		
		for(QueryResultStats queryResultStats : queryResultStatsList) {
			tokenize(queryResultStats);
		}
	}
	
	public void tokenize(QueryResultStats queryResultStats) {
		
		
		DocumentStats documentStats = new DocumentStats();
		ArrayList<String> docVector = documentStats.getDocVector();
		ArrayList<String> lemmaVector = documentStats.getLemmaVector();
		
		String contents = queryResultStats.getContent();

		// Remove all the '. ' at the end of the line and replace it with space
		contents = contents.replaceAll("\\. ", " ");

		// Split the tokens into two if there is a space or ,
		// or / or \ or - between the two words

		String[] tokens = contents.split("\\s+|\\/|\\\\|\\-|,|_");

		for (String token : tokens) {
			token = token.trim();

			// Replace all the 's with a "" character
			token = token.replaceAll("'s", "");

			// Replace all the special characters except meta characters and .
			token = token.replaceAll("[^\\w.]", "");

			// Delete the . in the token if it doesn't correspond in a valid
			// token
			// 2.3, j.y, C1.25 are valid tokens. . should not be removed.
			// 1.2.... - invalid token

			// The numbers are not considered as token.
			if (token.matches("[\\d]+"))
				continue;
			if (token.contains(".")) {
				if (!token.matches("^(\\w+)([\\.])(\\w+)+")) {
					continue;
				}
			}

			if (token.isEmpty())
				continue;

			// converting all the letters in the token to lowercase
			token = token.toLowerCase();
			token = token.trim();
			
			// exclude the stop words
			if(stopWords.contains(token)) continue;
			
			docVector.add(token);

			// Lemmatize the token
			String lemmaToken = lemmatizer.lemmatize(token).get(0);
			
			if(token.equals("dallas")) {
				lemmaToken = "dallas";
			}
			//System.out.println("#DEBUG token " + token + " lemma token " + lemmaToken);
			
			// add to the vocabulary set
			if(lemmaToken == null || lemmaToken.length() == 0) {
				lemmaToken = token;
			}
			
			lemmaVector.add(lemmaToken);
			vocabulary.add(lemmaToken);
			
			// add all the terms that correspond to the lemma. This 
			// data structure is maintained for normalization
			
			HashSet<String> termList;
			if((termList = derivationallyRelatedTerms.get(lemmaToken)) == null) {
				termList = new HashSet<String>();
				termList.add(token);
			} else {
				termList.add(token);
			}
			
			derivationallyRelatedTerms.put(lemmaToken, termList);
			
			
		}

		documentStats.setId(queryResultStats.getId());
		documentStats.setDocVector(docVector);
		documentStats.setLemmaVector(lemmaVector);
		documentStatsList.add(documentStats);
	}

	public HashMap<String, HashSet<String>> getDerivationallyRelatedTerms() {
		return derivationallyRelatedTerms;
	}

	public HashSet<String> getVocabulary() {
		return vocabulary;
	}

	public ArrayList<DocumentStats> getDocumentStatsList() {
		return documentStatsList;
	}
	
}
