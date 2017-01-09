import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {
	
	public static boolean chkFileExists(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			return true;
		} else {
			System.err.println("File: " + fileName + " Not present");
			return false;
		}
	}
	
	public static boolean chkDirExists(String dirName) {
		File dir = new File(dirName);
		if(dir.exists() && dir.isDirectory()) {
			return true;
		} else {
			System.err.println("Directory " + dirName + " Not present");
			return false;
		}
	}
	
	public static File[] getFilesList(String dirName) {
		if(chkDirExists(dirName) == false) {
			System.err.println("Directory : " + dirName + " is not present");
			return null;
		}
		
		File file = new File(dirName);
		return file.listFiles();
	}
	
	public static ArrayList<String> getFileContents(String fileName) throws IOException {
		ArrayList<String> contents = new ArrayList<>();
		
		if(chkFileExists(fileName)) {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String queryStr;
			while((queryStr = br.readLine()) != null) {
				queryStr = queryStr.trim();
				
				if(queryStr.isEmpty()) continue;
				contents.add(queryStr);
			}
			
			br.close();
			return contents;
		}
		
		return null;
	}

}
