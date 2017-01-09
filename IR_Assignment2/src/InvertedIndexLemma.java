import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 
 * @author RahulAravind
 * 
 *         Inverted Index version 1
 *
 */
public class InvertedIndexLemma {
	private String unCompressedFileName;
	private String compressedFileName;
	private LemmaProcessor lemmaProcessor;
	private TreeMap<String, InvertedIndexValueEntry> invertedIndex;
	private ArrayList<DocumentStats> documentStatsList;
	private final int kBlockSize = 8;
	private DataProvider dataProvider;

	public InvertedIndexLemma(LemmaProcessor lemmaProcessor, String unCompressedFileName, String compressedFileName) {
		this.lemmaProcessor = lemmaProcessor;
		this.unCompressedFileName = unCompressedFileName;
		this.compressedFileName = compressedFileName;
		invertedIndex = new TreeMap<String, InvertedIndexValueEntry>();
		documentStatsList = new ArrayList<DocumentStats>();
		dataProvider = new DataProvider();
	}

	public void constructUncompressedIndex() {

		ArrayList<LemmaStats> lemmaStatsList = lemmaProcessor.getLemmaStatsList();

		for (LemmaStats lemmaStats : lemmaStatsList) {

			for (Entry<String, Integer> lemmaEntry : lemmaStats.getTokenLemmaMap().entrySet()) {
				String lemma = lemmaEntry.getKey();
				Integer occurrence = lemmaEntry.getValue();

				InvertedIndexValueEntry invertedIndexValueEntry;

				if ((invertedIndexValueEntry = invertedIndex.get(lemma)) == null) {
					invertedIndexValueEntry = new InvertedIndexValueEntry();
					invertedIndexValueEntry.setDocumentFrequency(1);
					invertedIndexValueEntry.setTermFrequency(occurrence);

					long fileIndex = lemmaStats.getFileIndex();
					invertedIndexValueEntry.getPostingsList().add(fileIndex);
					invertedIndexValueEntry.getTermFrequencyList().add(occurrence);

					invertedIndex.put(lemma, invertedIndexValueEntry);
				} else {
					long df = invertedIndexValueEntry.getDocumentFrequency();
					invertedIndexValueEntry.setDocumentFrequency(df + 1);

					long tf = invertedIndexValueEntry.getTermFrequency();
					invertedIndexValueEntry.setTermFrequency(tf + occurrence);

					long fileIndex = lemmaStats.getFileIndex();
					invertedIndexValueEntry.getPostingsList().add(fileIndex);

					invertedIndexValueEntry.getTermFrequencyList().add(occurrence);
				}
			}

			long fileIndex = lemmaStats.getFileIndex();
			long documentLength = lemmaStats.getDocumentLength();
			long maxTermFrequency = lemmaStats.getMaxTermFrequency();

			DocumentStats documentStats = new DocumentStats(fileIndex, documentLength, maxTermFrequency);
			documentStatsList.add(documentStats);

		}

		for (String stopWord : StopWords.stopWordList) {
			if (invertedIndex.remove(stopWord) != null) {
			}
		}

		ObjectSerializer.serializeObject(unCompressedFileName, invertedIndex, documentStatsList);
		//System.out.println("Size of dictionary " + invertedIndex.keySet().size());
	}

	public void constructCompressedIndex() {

		ArrayList<CompressedDictionary> compressedDictionary = new ArrayList<CompressedDictionary>();
		String terms = "";
		int termPtr;
		int blockPtr = 0;

		for (Entry<String, InvertedIndexValueEntry> indexEntry : invertedIndex.entrySet()) {
			InvertedIndexValueEntry invertedIndexValueEntry = indexEntry.getValue();
			String lemma = indexEntry.getKey();
			termPtr = terms.length();
			terms += lemma.length() + lemma;
			CompressedDictionary compressedDictionary1;

			long df = invertedIndexValueEntry.getDocumentFrequency();
			long tf = invertedIndexValueEntry.getTermFrequency();
			ArrayList<Integer> termFrequencyList = invertedIndexValueEntry.getTermFrequencyList();

			if (blockPtr % kBlockSize == 0) {
				compressedDictionary1 = new CompressedDictionary(df, tf, termPtr, termFrequencyList);
			} else {
				compressedDictionary1 = new CompressedDictionary(df, tf, null, termFrequencyList);
			}
			gammaEncode(invertedIndexValueEntry, compressedDictionary1);
			compressedDictionary.add(compressedDictionary1);
			dataProvider.provideDataForLemma(lemma, compressedDictionary1, invertedIndexValueEntry, documentStatsList);
			blockPtr++;
		}

		CompressedIndexEntry compressedIndexEntry = new CompressedIndexEntry();
		compressedIndexEntry.setTerms(terms);
		compressedIndexEntry.setCompressedDictionary(compressedDictionary);

		ObjectSerializer.serializeObject(compressedFileName, compressedIndexEntry, documentStatsList);

	}

	public void gammaEncode(InvertedIndexValueEntry invertedIndexValueEntry, CompressedDictionary compressedDictionary1) {
		
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
			EliasEncoder.EliasEncode(gap, bitOutputStream, true);
		}
		bitOutputStream.close();
		compressedDictionary1.setEncodedBytes(byteOutput.toByteArray());
	}
	
	public TreeMap<String, InvertedIndexValueEntry> getDictionary() {
		return invertedIndex; 
	}
	
	public ArrayList<DocumentStats> getDocumentStatsList() {
		return documentStatsList;
	}

}
