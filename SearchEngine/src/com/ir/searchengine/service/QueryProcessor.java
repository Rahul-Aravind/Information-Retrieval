package com.ir.searchengine.service;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryProcessor {
	
	private String query;
	private static Lemmatizer lemmatizer;
	private QueryStats queryStats;
	
	public QueryProcessor(String query) {
		this.query = query;
		lemmatizer = new Lemmatizer();
	}
	
	public void processQuery() {
		queryTokenizer(query);
	}
	
	public void queryTokenizer(String query) {
		
		queryStats = new QueryStats(query);
		ArrayList<String> indexedQuery = queryStats.getIndexedQuery();
		HashMap<String, Integer> queryTokenMap = queryStats.getQueryTokenMap();

		// Remove all the '. ' at the end of the line and replace it with space
		query = query.replaceAll("\\. ", " ");

		// Split the tokens into two if there is a space or ,
		// or / or \ or - between the two words

		String[] tokens = query.split("\\s+|\\/|\\\\|\\-|,");

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
			
			if(!token.equals("dallas")) {
				token = lemmatizer.lemmatize(token).get(0);
			}
			
			Integer count = queryTokenMap.get(token);
			if (count == null) {
				queryTokenMap.put(token, 1);
				indexedQuery.add(token);
				
			} else {
				queryTokenMap.put(token, count + 1);
			}
		}
		
		//remove stop words in the query
		for(String stopWord : StopWords.stopWordList) {
			indexedQuery.remove(stopWord);
			if(queryTokenMap.remove(stopWord.toLowerCase()) != null){
			}
		}
		
		System.out.println("#DEBUG indexed Query " + indexedQuery);
		System.out.println("#DEBUG querytokenMap " + queryTokenMap);

		queryStats.setIndexedQuery(indexedQuery);
		queryStats.setQueryTokenMap(queryTokenMap);
	}

	public QueryStats getQueryStats() {
		return queryStats;
	}

}
