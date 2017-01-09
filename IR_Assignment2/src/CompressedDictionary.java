import java.io.Serializable;
import java.util.ArrayList;

public class CompressedDictionary implements Serializable {
	private static final long serialVersionUID = 1L;
	private long termFrequency;
	private long documentFrequency;
	private Integer termPtr;
	private byte[] encodeBytes;
	private ArrayList<Integer> termFrequencyList;
	
	public CompressedDictionary(long documentFrequency, long termFrequency, Integer termPtr, ArrayList<Integer> termFrequencyList) {
		super();
		this.termFrequency = termFrequency;
		this.documentFrequency = documentFrequency;
		this.termPtr = termPtr;
		this.termFrequencyList = termFrequencyList;
	}

	public long getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(long termFrequency) {
		this.termFrequency = termFrequency;
	}

	public long getDocumentFrequency() {
		return documentFrequency;
	}

	public void setDocumentFrequency(long documentFrequency) {
		this.documentFrequency = documentFrequency;
	}

	public Integer getTermPtr() {
		return termPtr;
	}

	public void setTermPtr(Integer termPtr) {
		this.termPtr = termPtr;
	}

	public byte[] getEncodedBytes() {
		return encodeBytes;
	}

	public void setEncodedBytes(byte[] encodedBytes) {
		this.encodeBytes = encodedBytes;
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
		s += "\nTerm frequency: " + termFrequency;
		s += "\nDocument frequency: " + documentFrequency;
		//s += "\nTerm pointer: " + termPtr;
		//s += "\nTerm frequency list: " + termFrequencyList;
		//s += "\nEncoded bytes: " + encodeBytes;
		
		return s;
	}

}
