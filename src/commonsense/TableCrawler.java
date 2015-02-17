package commonsense;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

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
            	addToMaps(f, relevantColumns);
            }
        }
	}
	
	/**
	 * Takes a single file and grabs data to populate the global map
	 * @param f
	 * @param releventColumns
	 */
	private static void addToMaps(File f, ArrayList<Integer> releventColumns) {
		// check if first columns contains entities (numeric == not an entity)
		// do a free base lookup for each entity and add resulting SET to local map(?)
			//map.add( freebaselookup() );
		// grab all attributes from columns w/ index in relevantColumns and add to key-value pair, relevantAttribute HashMap<Pair<String, String>,ArrayList<Pair<String, String>>>

		// find intersect from free base hits, modify all superEntity values in key-value pair

	}
	
	/**
	 * Finds the first column with non-numeric entries
	 * @return
	 */
	private static int idEntityColumn() {
		return 0;
	}
	
	/**
	 * Looks up the set of identities that freebase ascribes to a given entity
	 * @return
	 */
	private static Set<String> freeBaseLookup() {
		return null;
	}
	
	/**
	 * Finds the intersection between all sets
	 * @return
	 */
	private static Set<String> findIntersect(Set<Set<String>> freeBaseHits) {
		//set1.retainAll(set2)
		return null;
	}
	
	private class Pair<K,V> {
		K key;
		V value;
		
		Pair(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		Pair(K key) {
			this(key, null);
		}
	}
	
}
