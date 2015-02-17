package commonsense;

import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TableCrawler {
	private static HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap;

	public static void main(String[] args) {
		attMap = new HashMap<String, HashMap<String, HashSet<Pair<String, String>>>>();
		crawlDir(args[0], args[1]);
	}

	private static void crawlDir(String attFileName, String dirName) {
		AttributeFilter aFilter = new AttributeFilter(attFileName);

		File[] fileDir = new File(dirName).listFiles();
		for (File f : fileDir) {
			Integer[] relevantColumns = aFilter.relevance(f);
			if (relevantColumns.length != 0) {
				addToMaps(f, relevantColumns);
			}

		}
		// unit conversion? (google library) ...how does this affect double
		// entity comparison later?
		// database stuff! NoSQL? (ask Dan)
		// create entity table from key bindings of attMap ...also populate
		// attribute table?
		// create superentity table from keyset of attMap ...also populate
		// attribute table?
	}

	/**
	 * Takes a single file and grabs data to populate the global map
	 * 
	 * @param scan
	 * @param releventColumns
	 */
	private static void addToMaps(File f, Integer[] relevantCols) {
		try {
			Scanner scan = new Scanner(f);
			HashMap<String, HashSet<Pair<String, String>>> tableMap = new HashMap<String, HashSet<Pair<String, String>>>();
			Set<Set<String>> freeBaseHits = new HashSet<Set<String>>();
			if (scan.hasNextLine()) {
				String[] attributes = scan.nextLine().split(",");
				
				if(scan.hasNextLine()) {
					String[] line = scan.nextLine().split(",");
					// check if first columns contains entities (numeric == not an entity)
					int entityCol = idEntityColumn(line);

					while (scan.hasNextLine()) {
						tableMap.put(line[entityCol], processLine(line, entityCol, attributes, relevantCols, freeBaseHits));
						line = scan.nextLine().split(",");
					}
					tableMap.put(line[entityCol], processLine(line, entityCol, attributes, relevantCols, freeBaseHits));
				}
				
				// key-value pair
			}
			attMap.put(findIntersect(freeBaseHits), tableMap);
			scan.close();
			
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();
			System.exit(0);
		}
		



	}

	private static HashSet<Pair<String, String>> processLine(
			String[] line, int entityCol, String[] attributes, Integer[] relevantCols,
			Set<Set<String>> freeBaseHits) {
		
		String entity = line[entityCol];
		// do a free base lookup for each entity and add resulting set to file-specific map
		freeBaseHits.add(freeBaseLookup(entity));

		HashSet<Pair<String, String>> attVals = new HashSet<Pair<String, String>>();
		// grab all attributes from columns w/ index in relevantColumns
		for(int i = 0; i < relevantCols.length; i++) {
			attVals.add(new Pair<String,String>((String)attributes[i], (String)line[relevantCols[i]]));
		}
		return attVals;
	}

	/**
	 * Finds the first column with non-numeric entries
	 * 
	 * @return
	 */
	private static int idEntityColumn(String[] line) {
		return 0;
	}

	/**
	 * Looks up the set of identities that freebase ascribes to a given entity
	 * 
	 * @return
	 */
	private static Set<String> freeBaseLookup(String entity) {
		return null;
	}

	/**
	 * Finds the intersection between all sets
	 * 
	 * @return
	 */
	private static String findIntersect(Set<Set<String>> freeBaseHits) {
		// set1.retainAll(set2)
		// return max count? or alphanumeric?
		return null;
	}

}
