import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

public class LemmaProcessor {
	private Tokenizer tokenizer;
	private Lemmatizer lemmatizer;
	private ArrayList<LemmaStats> lemmaStatsList;
	
	public LemmaProcessor(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		lemmatizer = new Lemmatizer();
		lemmaStatsList = new ArrayList<LemmaStats>();
	}
	
	public void lemmatize(FileStats fileStats, int fileIndex) {
		
		File file = fileStats.getFile();
		String fileName = file.getName();
		
		LemmaStats lemmaStats = new LemmaStats(fileName);
		
		lemmaStats.setFileName(fileName);
		lemmaStats.setFileIndex(fileIndex);
		HashMap<String, Integer> tokenLemmaMap = lemmaStats.getTokenLemmaMap();
		
		for(Entry<String, Integer> tokenEntry : fileStats.getTokenMap().entrySet()) {
			
			String token = tokenEntry.getKey();
			int occurrence = tokenEntry.getValue();
			
			String lemma = lemmatizer.lemmatize(token).get(0);
			
			Integer tokenLemmaCount = 0;
			if((tokenLemmaCount = tokenLemmaMap.get(lemma)) == null) {
				tokenLemmaMap.put(lemma, occurrence);
			}
			else {
				tokenLemmaMap.put(lemma, tokenLemmaCount + occurrence);
			}
		}
		
		lemmaStats.setMaxTermFrequency((long) Collections.max(tokenLemmaMap.values()));
		lemmaStats.setDocumentLength(fileStats.getTotalTokens());
		lemmaStats.setTokenLemmaMap(tokenLemmaMap);
		
		lemmaStatsList.add(lemmaStats);
	}
	
	public void execute() {
		ArrayList<FileStats> fileStatsList = tokenizer.getFileStatsList();
		
		int fileIndex = 1;
		for(FileStats fileStats : fileStatsList) {
			lemmatize(fileStats, fileIndex);
			fileIndex++;
		}
		
		/*for(int i = 0; i < 5; i++) {
			System.out.println(lemmaStatsList.get(i));
		}*/
	}
	
	public ArrayList<LemmaStats> getLemmaStatsList() {
		return lemmaStatsList;
	}

}
