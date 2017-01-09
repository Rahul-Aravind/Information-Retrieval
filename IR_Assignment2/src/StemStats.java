import java.util.HashMap;

public class StemStats {
	
	private String fileName;
	private long fileIndex;
	private long maxTermFrequency;
	private long documentLength;
	private HashMap<String, Integer> stemTokenMap;
	
	public StemStats(String fileName) {
		this.fileName = fileName;
		stemTokenMap = new HashMap<String, Integer>();
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

	public HashMap<String, Integer> getStemTokenMap() {
		return stemTokenMap;
	}

	public void setStemTokenMap(HashMap<String, Integer> stemTokenMap) {
		this.stemTokenMap = stemTokenMap;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "\nfileName: " + fileName;
		s += "\nfileIndex: " + fileIndex;
		s += "\nDocument length: " + documentLength;
		s += "\nMax Term Frequency: " + maxTermFrequency;
		s += "\nStem Token Map : " + stemTokenMap;
		
		return s;
	}

}
