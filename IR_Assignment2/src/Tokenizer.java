import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Tokenizer {
	private String dirName;
	private ArrayList<FileStats> fileStatsList;

	public Tokenizer(String dirName) {
		this.dirName = dirName;
		fileStatsList = new ArrayList<FileStats>();
	}

	public void tokenize(String fileContents, String fileName) {

		FileStats fileStats = new FileStats(fileName);
		HashMap<String, Integer> tokenMapData = fileStats.getTokenMap();
		long totalTokenCount = 0;

		// Remove all the '. ' at the end of the line and replace it with space
		fileContents = fileContents.replaceAll("\\. ", " ");

		// Split the tokens into two if there is a space or ,
		// or / or \ or - between the two words

		String[] tokens = fileContents.split("\\s+|\\/|\\\\|\\-|,");

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

			Integer count = tokenMapData.get(token);

			if (count == null) {
				tokenMapData.put(token, 1);
			} else {
				tokenMapData.put(token, count + 1);
			}
			totalTokenCount++;
		}

		fileStats.setTokenMap(tokenMapData);
		fileStats.setTotalTokens(totalTokenCount);
		fileStats.setUniqueTokens(tokenMapData.size());
		fileStatsList.add(fileStats);
	}

	public void execute() {

		File files[] = FileUtil.getFilesList(dirName);
		int fileListSize = files.length;

		// Tokenize each and every file contents and store the token
		// characteristics in the map

		for (File file : files) {
			Parser parser = new Parser();
			String fileContents = parser.parseXml(file);
			tokenize(fileContents, file.getName());
		}
	}
	
	public ArrayList<FileStats> getFileStatsList() {
		return fileStatsList;
	}

}
