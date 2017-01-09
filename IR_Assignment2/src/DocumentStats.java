import java.io.Serializable;

public class DocumentStats implements Serializable {
	private static final long serialVersionUID = 1L;
	private long fileIndex;
	private long documentLength;
	private long maxTermFrequency;

	public DocumentStats(long fileIndex, long documentLength, long maxTermFrequency) {
		this.fileIndex = fileIndex;
		this.documentLength = documentLength;
		this.maxTermFrequency = maxTermFrequency;
	}

	public long getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(long fileIndex) {
		this.fileIndex = fileIndex;
	}

	public long getDocumentLength() {
		return documentLength;
	}

	public void setDocumentLength(long documentLength) {
		this.documentLength = documentLength;
	}

	public long getMaxTermFrequency() {
		return maxTermFrequency;
	}

	public void setMaxTermFrequency(long maxTermFrequency) {
		this.maxTermFrequency = maxTermFrequency;
	}

	@Override
	public String toString() {
		String s = "";
		s += "\nFileIndex: " + fileIndex;
		s += "\nDocument length: " + documentLength;
		s += "\nMax Term frequency: " + maxTermFrequency;

		return s;
	}

}
