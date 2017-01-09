import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class CollectionsUtils {
	
	public static LinkedList<Entry<String, Integer>> convertHashMapToSortedList(HashMap<String, Integer> tokenMapData) {
		
		LinkedList<Entry<String, Integer>> sortedList = new LinkedList<Entry<String, Integer>>(tokenMapData.entrySet());
		Collections.sort(sortedList, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> a, Entry<String, Integer> b) {

				return a.getValue().compareTo(b.getValue());
			}
		});
		
		return sortedList;
	}
	
	public static int getTokenCount(LinkedList<Entry<String, Integer>> tokenEntryList, int occurences) {
		int count = 0;
		
		for(Entry<String, Integer> entry : tokenEntryList) {
			if(entry.getValue() == occurences) count++;
			else if(entry.getValue() > occurences) break;
		}
		
		return count;
	}

}
