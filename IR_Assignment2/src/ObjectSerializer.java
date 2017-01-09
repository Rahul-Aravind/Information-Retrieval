import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectSerializer {

	public static void serializeObject(String indexFileName, Object invertedIndex, Object documentStatsList) {
		File file = new File(indexFileName);
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;

		if (file.exists()) {
			file.delete();
		}

		try {
			fileOutputStream = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(invertedIndex);
			objectOutputStream.writeObject(documentStatsList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
