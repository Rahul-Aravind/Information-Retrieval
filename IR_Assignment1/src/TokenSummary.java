import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class TokenSummary {
	private long totalTokenCount;
	private long uniqueTokenCount;
	private double avgUniqueTokenCount;
	private HashMap<String, Integer> tokenMapData;
	private LinkedList<Entry<String, Integer>> sortedList;
	private int fileListSize;

	public TokenSummary() {
		totalTokenCount = 0;
		uniqueTokenCount = 0;
		avgUniqueTokenCount = 0.0;
		tokenMapData = new HashMap<String, Integer>();
		sortedList = new LinkedList<Entry<String, Integer>>();
		fileListSize = 0;
	}

	public LinkedList<Entry<String, Integer>> getSortedList() {
		return sortedList;
	}

	public void setSortedList(LinkedList<Entry<String, Integer>> sortedList) {
		this.sortedList = sortedList;
	}

	public long getTotalTokenCount() {
		return totalTokenCount;
	}

	public void setTotalTokenCount(long totalTokenCount) {
		this.totalTokenCount = totalTokenCount;
	}

	public long getUniqueTokenCount() {
		return uniqueTokenCount;
	}

	public void setUniqueTokenCount(long uniqueTokenCount) {
		this.uniqueTokenCount = uniqueTokenCount;
	}

	public double getAvgUniqueTokenCount() {
		return avgUniqueTokenCount;
	}

	public void setAvgUniqueTokenCount(double avgUniqueTokenCount) {
		this.avgUniqueTokenCount = avgUniqueTokenCount;
	}

	public HashMap<String, Integer> getTokenMapData() {
		return tokenMapData;
	}

	public void setTokenMapData(HashMap<String, Integer> tokenMapData) {
		this.tokenMapData = tokenMapData;
	}
	
	public int getFileListSize() {
		return fileListSize;
	}

	public void setFileListSize(int fileListSize) {
		this.fileListSize = fileListSize;
	}

}
