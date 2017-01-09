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
	private LemmaProcessor lemmaProcessor;
	private TreeMap<String, InvertedIndexValueEntry> invertedIndex;
	private HashMap<Long, DocumentStats> documentStatsMap;

	public InvertedIndexLemma(LemmaProcessor lemmaProcessor) {
		this.lemmaProcessor = lemmaProcessor;
		invertedIndex = new TreeMap<String, InvertedIndexValueEntry>();
		documentStatsMap = new HashMap<Long, DocumentStats>();
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
			documentStatsMap.put(fileIndex, documentStats);

		}

		for (String stopWord : StopWords.stopWordList) {
			if (invertedIndex.remove(stopWord) != null) {
			}
		}
		
		//System.out.println("Size of dictionary " + invertedIndex.keySet().size());
	}
	
	public TreeMap<String, InvertedIndexValueEntry> getDictionary() {
		return invertedIndex; 
	}
	
	public HashMap<Long, DocumentStats> getDocumentStatsMap() {
		return documentStatsMap;
	}

}
