import java.util.HashMap;

public class LemmaStats {
	private String fileName;
	private long fileIndex;
	private long maxTermFrequency;
	private long documentLength;
	private HashMap<String, Integer> tokenLemmaMap;
	
	public LemmaStats(String fileName) {
		this.fileName = fileName;
		tokenLemmaMap = new HashMap<String, Integer>();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public long getFileIndex() {
		return fileIndex;
	}
	
	public void setFileIndex(long fileIndex) {
		this.fileIndex = fileIndex;
	}
	
	public long getMaxTermFrequency() {
		return maxTermFrequency;
	}
	
	public void setMaxTermFrequency(long maxTermFrequency) {
		this.maxTermFrequency = maxTermFrequency;
	}
	
	public long getDocumentLength() {
		return documentLength;
	}
	
	public void setDocumentLength(long documentLength) {
		this.documentLength = documentLength;
	}
	
	public HashMap<String, Integer> getTokenLemmaMap() {
		return tokenLemmaMap;
	}
	
	public void setTokenLemmaMap(HashMap<String, Integer> tokenLemmaMap) {
		this.tokenLemmaMap = tokenLemmaMap;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "\nFileName: " + fileName;
		s += "\nFileIndex: " + fileIndex;
		s += "\nMax term frequency: " + maxTermFrequency;
		s += "\nDocument length: " + documentLength;
		s += "\nToken lemma map: " + tokenLemmaMap;
		
		return s;
	}

}
