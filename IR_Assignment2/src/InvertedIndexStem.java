import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class InvertedIndexStem {
	private final int kBlockSize = 1;
	private int prefixLen = 5;
	private String uncompressedFileName;
	private String compressedFileName;
	private StemmerDriver stemmerDriver;
	private TreeMap<String, InvertedIndexValueEntry> invertedIndex;
	private ArrayList<DocumentStats> documentStatsList;
	private DataProvider dataProvider;
	
	public InvertedIndexStem(StemmerDriver stemmerDriver, String uncompressedFileName, String compressedFileName) {
		this.stemmerDriver = stemmerDriver;
		this.uncompressedFileName = uncompressedFileName;
		this.compressedFileName = compressedFileName;
		invertedIndex = new TreeMap<String, InvertedIndexValueEntry>();
		documentStatsList = new ArrayList<DocumentStats>();
		dataProvider = new DataProvider();
	}
	
	public void constructUncompressedIndex() {
		ArrayList<StemStats> stemStatsList = stemmerDriver.getStemStatsList();
		
		for(StemStats stemStats : stemStatsList) {
			
			for(Entry<String, Integer> entry : stemStats.getStemTokenMap().entrySet()) {
				String stem = entry.getKey();
				Integer occurrence = entry.getValue();
				
				InvertedIndexValueEntry invertedIndexValueEntry;
				
				if((invertedIndexValueEntry = invertedIndex.get(stem)) == null) {
					invertedIndexValueEntry = new InvertedIndexValueEntry();
					invertedIndexValueEntry.setDocumentFrequency(1);
					invertedIndexValueEntry.setTermFrequency(occurrence);
					
					long fileIndex = stemStats.getFileIndex();
					invertedIndexValueEntry.getPostingsList().add(fileIndex);
					invertedIndexValueEntry.getTermFrequencyList().add(occurrence);
					
					invertedIndex.put(stem, invertedIndexValueEntry);
				}
				else {
					long df = invertedIndexValueEntry.getDocumentFrequency();
					invertedIndexValueEntry.setDocumentFrequency(df + 1);

					long tf = invertedIndexValueEntry.getTermFrequency();
					invertedIndexValueEntry.setTermFrequency(tf + occurrence);

					long fileIndex = stemStats.getFileIndex();
					invertedIndexValueEntry.getPostingsList().add(fileIndex);

					invertedIndexValueEntry.getTermFrequencyList().add(occurrence);
				}
			}
			
			long fileIndex = stemStats.getFileIndex();
			long documentLength = stemStats.getDocumentLength();
			long maxTermFrequency = stemStats.getMaxTermFrequency();

			DocumentStats documentStats = new DocumentStats(fileIndex, documentLength, maxTermFrequency);
			documentStatsList.add(documentStats);
		}
		
		for (String stopWord : StopWords.stopWordList) {
			if (invertedIndex.remove(stopWord) != null) {
			}
		}
		
		ObjectSerializer.serializeObject(uncompressedFileName, invertedIndex, documentStatsList);
	}
	
	public void constructCompressedIndex() {
		ArrayList<CompressedDictionary> compressedDictionaryList = new ArrayList<CompressedDictionary>();
		String terms = "";
		Integer termPtr = null;
		int blockPtr = 0;
		
		//front coding
		String prefixStem = "";
		String prevPrefixStem = "";
		String prevStem = "";
		
		for(Entry<String, InvertedIndexValueEntry> entry : invertedIndex.entrySet()) {
			InvertedIndexValueEntry invertedIndexValueEntry = entry.getValue();
			
			String stem = entry.getKey();
			
			if(stem.length() >= prefixLen) {
				prefixStem = stem.substring(0, prefixLen);
			}
			else {
				prefixStem = stem;
			}
			
			if(prefixStem.equals(prevPrefixStem)) {
				if(!prevStem.isEmpty()) {
					String suffix = prevStem.substring(prefixLen);
					terms += stem.length() + 1 + prefixStem + "*" + suffix;
					prevStem = "";
				}
				String suffix = stem.substring(prefixStem.length());
				terms += suffix.length() + "<>" + suffix;
			}
			else {
				if(!prevStem.isEmpty()) {
					terms += prevStem.length() + prevStem;
					prevStem = "";
				}
				
				prevPrefixStem = prefixStem;
				prevStem = stem;
				termPtr = terms.length();
			}
			
			CompressedDictionary compressedDictionary;
			long df = invertedIndexValueEntry.getDocumentFrequency();
			long tf = invertedIndexValueEntry.getTermFrequency();
			ArrayList<Integer> termFrequencyList = invertedIndexValueEntry.getTermFrequencyList();
			
			if(blockPtr % kBlockSize == 0) {
				compressedDictionary = new CompressedDictionary(df, tf, termPtr, termFrequencyList);
			}
			else {
				compressedDictionary = new CompressedDictionary(df, tf, null, termFrequencyList);
			}
			
			deltaEncode(invertedIndexValueEntry, compressedDictionary);
			compressedDictionaryList.add(compressedDictionary);
			dataProvider.provideDataForStem(stem, compressedDictionary, invertedIndexValueEntry, documentStatsList);
			blockPtr++;
		}
		
		CompressedIndexEntry compressedIndexEntry = new CompressedIndexEntry();
		compressedIndexEntry.setTerms(terms);
		compressedIndexEntry.setCompressedDictionary(compressedDictionaryList);

		ObjectSerializer.serializeObject(compressedFileName, compressedIndexEntry, documentStatsList);
		
		//System.out.println(compressedIndexEntry.getTerms());
	}
	
	public void deltaEncode(InvertedIndexValueEntry invertedIndexValueEntry, CompressedDictionary compressedDictionary) {
		
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

		BitOutputStream bitOutputStream = new BitOutputStream(byteOutput);

		int gap;
		ArrayList<Long> postingsList = invertedIndexValueEntry.getPostingsList();
		
		for (int i = 0; i < postingsList.size(); i++) {
			if (i == 0) {
				gap = postingsList.get(i).intValue();
			} else {
				gap = postingsList.get(i).intValue() - postingsList.get(i - 1).intValue();
			}
			EliasEncoder.EliasEncode(gap, bitOutputStream, false);
		}
		bitOutputStream.close();
		compressedDictionary.setEncodedBytes(byteOutput.toByteArray());
	}
	
	public TreeMap<String, InvertedIndexValueEntry> getDictionary() {
		return invertedIndex;
	}

}
