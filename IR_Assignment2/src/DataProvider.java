import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class DataProvider {
	
	ArrayList<String> terms;
	private Lemmatizer lemmatizer;
	private Stemmer stemmer;
	private HashMap<String, String> lemmaTokenMap;
	private HashMap<String, String> stemTokenMap;
	
	public DataProvider() {
		terms = new ArrayList<String>();
		terms.add("Reynolds");
		terms.add("NASA");
		terms.add("Prandtl");
		terms.add("flow");
		terms.add("pressure");
		terms.add("boundary");
		terms.add("shock");
		
		lemmatizer = new Lemmatizer();
		stemmer = new Stemmer();
		
		lemmaTokenMap = new HashMap<String, String>();
		stemTokenMap = new HashMap<String, String>();
		
		preprocessTerms();
	}
	
	public void preprocessTerms() {
		for(String term : terms) {
			term = term.toLowerCase();
			String lemma = lemmatizer.lemmatize(term).get(0);
			
			//System.out.println("Lemma " + lemma);
			lemmaTokenMap.put(lemma, term);
			
			stemmer.add(term.toCharArray(), term.length());
			stemmer.stem();
			String stemTerm = stemmer.toString();
			//System.out.println("Stem " + stemTerm);
			stemTokenMap.put(stemTerm, term);
		}
	}
	
	public int calculateUncompressedInvertedListSize(InvertedIndexValueEntry invertedIndexValueEntry, int termLen) {
		int size = 8 + 8 + 4 + termLen;
		size += invertedIndexValueEntry.getPostingsList().size() * 8;
		size += invertedIndexValueEntry.getTermFrequencyList().size() * 4;
		
		return size;
	}
	
	public int calculateCompressedInvertedListSize(CompressedDictionary compressedDictionary, int termLength) {
		int size = 8 + 8 + 4 + termLength;
		size += compressedDictionary.getEncodedBytes().length;
		size += compressedDictionary.getTermFrequencyList().size() * 4;
		
		return size;
	}
	
	public void provideDataForLemma(String lemma, CompressedDictionary compressedDictionary, InvertedIndexValueEntry invertedIndexValueEntry, ArrayList<DocumentStats> docStatsList) {
		if(lemmaTokenMap.get(lemma) != null) {
			System.out.print("\nUnCompressed version 1: Term: " + lemma + "->\t" + compressedDictionary);
			System.out.println("\nInvertedList Size in Bytes: " + calculateUncompressedInvertedListSize(invertedIndexValueEntry, lemma.length()));
			System.out.print("\nCompressed version 1:   Term: " + lemma + "->\t" + compressedDictionary);
			System.out.println("\nInvertedList Size in Bytes: " + calculateCompressedInvertedListSize(compressedDictionary, lemma.length()));
			
			if(lemma.equalsIgnoreCase("NASA")) {
				System.out.println("\nNASA" + " statistics");
				provideDocStats(invertedIndexValueEntry, docStatsList);
			}
		}
	}
	
	public void provideDataForStem(String stem, CompressedDictionary compressedDictionary, InvertedIndexValueEntry invertedIndexValueEntry, ArrayList<DocumentStats> docStatsList) {
		if(stemTokenMap.get(stem) != null) {
			System.out.print("\nUnCompressed version 2: Term: " + stem + "->\t" + compressedDictionary);
			System.out.println("\nInvertedList Size in Bytes: " + calculateUncompressedInvertedListSize(invertedIndexValueEntry, stem.length()));
			System.out.print("\nCompressed version 2:   Term: " + stem + "->\t" + compressedDictionary);
			System.out.println("\nInvertedList Size in Bytes: " + calculateCompressedInvertedListSize(compressedDictionary, stem.length()));
			if(stem.equalsIgnoreCase("NASA")) {
				System.out.println("\nNASA" + " statistics");
				provideDocStats(invertedIndexValueEntry, docStatsList);
			}
		}
	}
	
	public void provideDocStats(InvertedIndexValueEntry invertedIndexValueEntry, ArrayList<DocumentStats> docStatsList) {
		//System.out.println(invertedIndexValueEntry.getPostingsList());
		for(int i = 0; i < 3; i++) {
			Long docId = invertedIndexValueEntry.getPostingsList().get(i);
			Integer tf = invertedIndexValueEntry.getTermFrequencyList().get(i);
			System.out.println("DocId: " + docId + " Term Freq " + tf + " Doclen: " + docStatsList.get((int) (docId - 1)).getDocumentLength() + " Max_Tf: " + docStatsList.get((int) (docId - 1)).getMaxTermFrequency());
		}
	}
	
	public void provideTermWithHighestDF(TreeMap<String, InvertedIndexValueEntry> dictMap) {
		long maxDf = -1;
		ArrayList<String> maxOccurTerm = new ArrayList<String>();
		
		for(String term : dictMap.keySet()) {
			long df = dictMap.get(term).getDocumentFrequency();
			if(df > maxDf) {
				maxDf = df;
			}
		}
		
		for(String term : dictMap.keySet()) {
			long df = dictMap.get(term).getDocumentFrequency();
			if(df == maxDf) {
				maxOccurTerm.add(term);
			}
		}
		
		System.out.println("Term with max document frequency ");
		System.out.println("Document Frequency: " + maxDf);
		System.out.println("Term(s):");
		System.out.println(maxOccurTerm);
	}
	
	public void provideTermWithSmallestDF(TreeMap<String, InvertedIndexValueEntry> dictMap) {
		long minDf = Long.MAX_VALUE;
		ArrayList<String> minOccurTerm = new ArrayList<String>();
		
		for(String term : dictMap.keySet()) {
			long df = dictMap.get(term).getDocumentFrequency();
			if(df < minDf) {
				minDf = df;
			}
		}
		
		for(String term : dictMap.keySet()) {
			long df = dictMap.get(term).getDocumentFrequency();
			if(df == minDf) {
				minOccurTerm.add(term);
			}
		}
		
		System.out.println("Term with min document frequency ");
		System.out.println("Document Frequency: " + minDf);
		System.out.println("Term(s):");
		System.out.println(minOccurTerm);
	}
	
	public void provideDocWithMaxTfAndMaxDocLen(ArrayList<DocumentStats> documentStatsList) {
		long gMaxTf = -1;
		long maxTfDocId = 0;
		long gDoclen = -1;
		long maxDoclenDocId = 0;
		
		for(DocumentStats documentStats : documentStatsList) {
			long maxTf = documentStats.getMaxTermFrequency();
			long doclen = documentStats.getDocumentLength();
			
			if(maxTf > gMaxTf) {
				gMaxTf = maxTf;
				maxTfDocId = documentStats.getFileIndex();
			}
			
			if(doclen > gDoclen) {
				gDoclen = doclen;
				maxDoclenDocId = documentStats.getFileIndex();
			}
		}
		
		System.out.println("Document with highest maxTf: " + " DocId: " + maxTfDocId + " Tf: " + gMaxTf);
		System.out.println("Document with highest document length: " + " DocId: " + maxDoclenDocId + " Tf: " + gDoclen);
	}
	
	public static void main(String args[]) {
		DataProvider dataProvider = new DataProvider();
	}

}
