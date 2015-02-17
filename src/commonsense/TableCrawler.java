package commonsense;

import java.io.File;
import java.util.ArrayList;

public class TableCrawler {
	
	public static void main(String[] args) {
		
		crawlDir(args[0], args[1]);
	}

	private static void crawlDir(String attFileName, String dirName) {
        File[] fileDir = new File(dirName).listFiles();
        AttributeFilter aFilter = new AttributeFilter(attFileName);
        for (File f : fileDir) {
            ArrayList<Integer> relevantColumns = aFilter.relevance(f);
            if( relevantColumns.size() != 0 ) {
            	addToMaps(f);
            }
        }
	}
	
	private static void addToMaps(File f) {
		
	}
	
	
}
