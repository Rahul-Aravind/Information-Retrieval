import java.io.Serializable;
import java.util.ArrayList;

public class InvertedIndexValueEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	private long documentFrequency;
	private long termFrequency;
	private ArrayList<Long> postingsList;
	private ArrayList<Integer> termFrequencyList;

	public InvertedIndexValueEntry() {
		postingsList = new ArrayList<Long>();
		termFrequencyList = new ArrayList<Integer>();
	}

	public long getDocumentFrequency() {
		return documentFrequency;
	}

	public void setDocumentFrequency(long documentFrequency) {
		this.documentFrequency = documentFrequency;
	}

	public long getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(long termFrequency) {
		this.termFrequency = termFrequency;
	}

	public ArrayList<Long> getPostingsList() {
		return postingsList;
	}

	public void setPostingsList(ArrayList<Long> postingsList) {
		this.postingsList = postingsList;
	}

	public ArrayList<Integer> getTermFrequencyList() {
		return termFrequencyList;
	}

	public void setTermFrequencyList(ArrayList<Integer> termFrequencyList) {
		this.termFrequencyList = termFrequencyList;
	}

	@Override
	public String toString() {
		String s = "";
		s += "\nDocument Frequency " + documentFrequency;
		s += "\nTerm Frequency " + termFrequency;
		s += "\nPostings list " + postingsList;
		s += "\nTerm frequency list " + termFrequencyList;

		return s;
	}

}
