import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class StemmerDriver {
	
	private Tokenizer tokenizer;
	private ArrayList<StemStats> stemStatsList;
	
	public StemmerDriver(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		stemStatsList = new ArrayList<StemStats>();
	}
	
	public void stemTokens(FileStats fileStats, String fileName, long fileIndex) {
		StemStats stemStats = new StemStats(fileName);
		stemStats.setFileIndex(fileIndex);
		
		HashMap<String, Integer> stemTokenMap = stemStats.getStemTokenMap();
		Stemmer stemmer = new Stemmer();
		
		for(Entry<String, Integer> tokenEntry : fileStats.getTokenMap().entrySet()) {
			String token = tokenEntry.getKey();
			Integer occurrence = tokenEntry.getValue();
			
			stemmer.add(token.toCharArray(), token.length());
			stemmer.stem();
			
			String stemToken = stemmer.toString();
			Integer stemCount;
			
			if((stemCount = stemTokenMap.get(stemToken)) == null) {
				stemTokenMap.put(stemToken, occurrence);
			}
			else {
				stemTokenMap.put(stemToken, stemCount + occurrence);
			}
		}
		
		stemStats.setMaxTermFrequency((long) Collections.max(stemTokenMap.values()));
		stemStats.setDocumentLength(fileStats.getTotalTokens());
		
		stemStatsList.add(stemStats);
	}
	
	public void execute() {
		ArrayList<FileStats> fileStatsList = tokenizer.getFileStatsList();
		
		
		long fileIndex = 1;
		for(FileStats fileStats : fileStatsList) {
			stemTokens(fileStats, fileStats.getFileName(), fileIndex);
			fileIndex++;
		}
		
	}
	
	public ArrayList<StemStats> getStemStatsList() {
		return stemStatsList;
	}

}
