package commonsense;

import java.util.*;
import java.io.*;
import java.util.HashMap;
import java.util.Set;
//import com.google.api-client;


public class TableCrawler { //should we make this an object, so it can handle multiple directories? No
	private static HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap;

	public static void main(String[] args) {
		attMap = new HashMap<String, HashMap<String, HashSet<Pair<String, String>>>>();
		if( args.length == 2 ) {
			crawlDir(args[0], args[1]);
		} else {
			System.out.println("Run RelevanceFilterMain --> fileNameOnlyOutput.txt");
			System.out.println("args: fileNameOnlyOutput.txt dir");
			System.exit(0);
		}
	}

	/*
	 * Crawls the directory and processes csv files ...APPEND UNITS TO NUMBERS
	 * 
	 * @param attFileName
	 * @param dirName
	 */
	private static void crawlDir(String filtrateFileName, String dirName) {
		try {
			Scanner filtrateScan = new Scanner(new File(filtrateFileName));
			while( filtrateScan.hasNextLine() ) {
				String fileName = filtrateScan.nextLine().split(":")[0];
				File f = new File(dirName + "/" + fileName);
				addToMap(f, );
			}
			File[] fileDir = new File(dirName).listFiles();
			for (File f : fileDir) {
				Integer[] relevantColumns = aFilter.relevantColumnIndexes(f);
				if (relevantColumns.length != 0) {
					addToMap(f, relevantColumns);
				}
			}
			// unit conversion? (google library) ...how does this affect double
			// entity comparison later?
			// database stuff! NoSQL? (ask Dan)
			// create entity table from key bindings of attMap ...also populate
			// attribute table?
			// create superentity table from keyset of attMap ...also populate
			// attribute table?
		} catch (FileNotFoundException f ) {
			System.out.println("Filtrate file not found.");
		}
		
	}

	/*
	 * Takes a single file and grabs data to populate the global map
	 * 
	 * @param scan
	 * @param releventColumns
	 */
	private static void addToMap(File f, String[] relevantCols) {
		try {
			Scanner scan = new Scanner(f);
			HashMap<String, HashSet<Pair<String, String>>> tableMap = null;
			Set<Set<String>> freeBaseHits = null;
			if (scan.hasNextLine()) {
				String[] attributes = scan.nextLine().split(",");
				if (scan.hasNextLine()) {
					String[] line = scan.nextLine().split(",");
					// check if first columns contains entities (alphabetic chars)
					int entityCol = idEntityColumn(line);
					if (entityCol != -1) { // has a non-numeric entity column
						tableMap = new HashMap<String, HashSet<Pair<String, String>>>();
						freeBaseHits = new HashSet<Set<String>>(); //TODO: call freebasecaller

						while (scan.hasNextLine()) {
							HashSet<Pair<String, String>> entry = processLine(line, entityCol, attributes, 
								 	 relevantCols, freeBaseHits);
							if( entry != null )
								tableMap.put(line[entityCol], entry);
							line = scan.nextLine().split(",");
						}
						tableMap.put(line[entityCol],
									 processLine(line, entityCol, attributes,
											 	 relevantCols, freeBaseHits));
					}
				}
			}
			if (tableMap != null && freeBaseHits != null) {
				String superEntity = findMaxIntersect(freeBaseHits);
				if( attMap.containsKey(superEntity) ) { //append if contains
					Map<String, HashSet<Pair<String,String>>> currentMap = attMap.get(superEntity);
					for( String key : tableMap.keySet() ) {
						currentMap.put(key, tableMap.get(key));
					}
				} else { //create new superentity
					attMap.put(superEntity, tableMap);
				}
			}
			scan.close();

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
			System.exit(0);
		}
	}
	
	

	/*
	 * Processes a single line in a csv file.
	 * 
	 * @param line
	 *            The line from the file as a String array.
	 * @param entityCol
	 *            The index of the column in which entity names reside.
	 * @param attributes
	 *            A String array of the relevant attributes in the table.
	 * @param relevantCols
	 *            An integer array of the relevant column indices.
	 * @param freeBaseHits
	 *            Pointer to collection of possible superentities.
	 * @return The HashSet from the entity to its collection of attributes.
	 */
	private static HashSet<Pair<String, String>> processLine(String[] line,
			int entityCol, String[] attributes, Integer[] relevantCols,
			Set<Set<String>> freeBaseHits) {

		String entity = line[entityCol];
		// do a free base lookup for each entity and add resulting set to
		// file-specific map
		try {
			freeBaseHits.add(FreeBaseCaller.lookup(entity));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashSet<Pair<String, String>> attVals = new HashSet<Pair<String, String>>();
		// grab all attributes from columns w/ index in relevantColumns
		for (int i = 0; i < relevantCols.length; i++) {
			attVals.add(new Pair<String, String>(attributes[i], line[relevantCols[i]]));
		}
		return attVals;
	}



	/*
	 * Finds the maximum intersection between all sets
	 * 
	 * @return -1 if no alphabetic entity column
	 */
	private static String findMaxIntersect(Set<Set<String>> freeBaseHits) {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for(Set<String> set : freeBaseHits) {
			for( String superEntity : set ) {
				if( counts.keySet().contains(superEntity) ) {
					counts.put(superEntity, counts.get(superEntity) + 1);
				} else {
					counts.put(superEntity, 1);
				}
			}
		}
		// set1.retainAll(set2)
		// return max count? or alphanumeric?
		return maxCount(counts);
	}
	
	/*
	 * Returns the string in the map with the highest count
	 * 
	 * @param counts
	 * @return
	 */
	private static String maxCount(Map<String, Integer> counts) {
		String maxEntity = "";
		counts.put(maxEntity, 0);
		for( String superEntity : counts.keySet() ) {
			if( counts.get(superEntity) > counts.get(maxEntity) ) {
				maxEntity = superEntity;
			}
		}
		return maxEntity;
	}
}
