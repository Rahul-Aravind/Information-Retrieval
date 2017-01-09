import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class QueryProcessor {
	
	private String queryFileName;
	private ArrayList<QueryStats> queryStatsList;
	private static Lemmatizer lemmatizer = new Lemmatizer();
	
	public QueryProcessor(String queryFileName) {
		this.queryFileName = queryFileName;
		queryStatsList = new ArrayList<>();
	}
	
	public void processQueryFile() {
		try {
			ArrayList<String> queryFileContents = FileUtil.getFileContents(queryFileName);
			
			for(String queryStr : queryFileContents) {
				queryTokenizer(queryStr);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void queryTokenizer(String query) {
		
		QueryStats queryStats = new QueryStats(query);
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
			
			token = lemmatizer.lemmatize(token).get(0);

			Integer count = queryTokenMap.get(token);
			if (count == null) {
				queryTokenMap.put(token, 1);
			} else {
				queryTokenMap.put(token, count + 1);
			}
		}
		
		//remove stop words in the query
		for(String stopWord : StopWords.stopWordList) {
			if(queryTokenMap.remove(stopWord.toLowerCase()) != null){
			}
		}

		queryStats.setQueryTokenMap(queryTokenMap);
		queryStats.setMaxTf(Collections.max(queryTokenMap.values()));
		queryStatsList.add(queryStats);
	}

	public ArrayList<QueryStats> getQueryStatsList() {
		return queryStatsList;
	}

}
