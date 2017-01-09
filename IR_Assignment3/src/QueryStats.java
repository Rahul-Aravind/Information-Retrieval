import java.util.HashMap;

public class QueryStats {
	
	private String queryStr;
	private HashMap<String, Integer> queryTokenMap;
	private int maxTf;
	
	public QueryStats(String queryStr) {
		this.queryStr = queryStr;
		queryTokenMap = new HashMap<>();
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public HashMap<String, Integer> getQueryTokenMap() {
		return queryTokenMap;
	}

	public void setQueryTokenMap(HashMap<String, Integer> queryTokenMap) {
		this.queryTokenMap = queryTokenMap;
	}
	
	public int getMaxTf() {
		return maxTf;
	}
	
	public void setMaxTf(int maxTf) {
		this.maxTf = maxTf;
	}

}
