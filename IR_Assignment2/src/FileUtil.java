import java.io.File;

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

}
