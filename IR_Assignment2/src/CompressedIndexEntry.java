import java.io.Serializable;
import java.util.ArrayList;

public class CompressedIndexEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String terms;
	private ArrayList<CompressedDictionary> compressedDictionary;
	
	public CompressedIndexEntry() {
		compressedDictionary = new ArrayList<CompressedDictionary>();
	}
	
	public String getTerms() {
		return terms;
	}
	
	public void setTerms(String terms) {
		this.terms = terms;
	}
	
	public ArrayList<CompressedDictionary> getCompressedDictionary() {
		return compressedDictionary;
	}
	
	public void setCompressedDictionary(ArrayList<CompressedDictionary> compressedDictionary) {
		this.compressedDictionary = compressedDictionary;
	}	
	
	@Override
	public String toString() {
		String s = "";
		s += "\nterms: " + terms;
		s += "\ncompressed dictionary " + compressedDictionary;
		
		return s;
	}

}
