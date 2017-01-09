import java.io.File;
import java.util.HashMap;

public class FileStats {
	private File file;
	private HashMap<String, Integer> tokenMap;
	private long totalTokens;
	private long uniqueTokens;
	
	public FileStats(File file) {
		this.file = file;
		tokenMap = new HashMap<String, Integer>();
		totalTokens = 0;
		uniqueTokens = 0;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public HashMap<String, Integer> getTokenMap() {
		return tokenMap;
	}
	
	public void setTokenMap(HashMap<String, Integer> tokenMap) {
		this.tokenMap = tokenMap;
	}
	
	public long getTotalTokens() {
		return totalTokens;
	}
	
	public void setTotalTokens(long totalTokens) {
		this.totalTokens = totalTokens;
	}
	
	public long getUniqueTokens() {
		return uniqueTokens;
	}
	
	public void setUniqueTokens(long uniqueTokens) {
		this.uniqueTokens = uniqueTokens;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "\nFile: " + file;
		s += "\ntokenMap: " + tokenMap;
		s += "\ntotal token count: " + totalTokens;
		s += "\nunique token count: " + uniqueTokens;
		
		return s;
	}

}
