package commonsense;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
//import com.google.api-client;


public class TableCrawler { //should we make this an object, so it can handle multiple directories? No
	private static HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> attMap;

	public static void main(String[] args) {
		attMap = new HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>>();
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
				String line = filtrateScan.nextLine();
				String fileName = line.split(":")[0];
				File f = new File(dirName + "/" + fileName);
				addToMap(f, line);
			}
			// create entity table from key bindings of attMap ...also populate attribute table?
			// create superentity table from keyset of attMap ...also populate attribute table?
			filtrateScan.close();
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
	private static void addToMap(File f, String data) {
		try {
			Scanner scan = new Scanner(f);
			TableInfo info = new TableInfo(f, data);
			if( info.isValid() ) {
				HashMap<String, HashSet<Pair<String, BigDecimal>>> entityData = null;
				Set<Set<String>> freeBaseHits = null;

				if( scan.hasNextLine() ) {
					scan.nextLine();
					entityData = new HashMap<String, HashSet<Pair<String, BigDecimal>>>();
					freeBaseHits = new HashSet<Set<String>>(); //TODO: call freebasecaller
					String entity = "";
					while (scan.hasNextLine()) {
						String[] line = scan.nextLine().split(",");
						HashSet<Pair<String, BigDecimal>> entry = processLine(line, info, freeBaseHits);
						if( entry != null ) {
							entityData.put(entity, entry);					
						}
						line = scan.nextLine().split(",");
					}
				}
				//TODO:
				if (entityData != null && freeBaseHits != null) {
					String superEntity = findMaxIntersect(freeBaseHits);
					if( attMap.containsKey(superEntity) ) { //append if contains
						Map<String, HashSet<Pair<String,BigDecimal>>> currentMap = attMap.get(superEntity);
						for( String key : entityData.keySet() ) {
							currentMap.put(key, entityData.get(key));
						}
					} else { //create new superentity
						attMap.put(superEntity, entityData);
					}
				}
			} else {
				System.out.println("Parse error during processing of output line for entry " + f.getName());
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
	private static HashSet<Pair<String, BigDecimal>> processLine(String[] line, TableInfo info, Set<Set<String>> freeBaseHits) {

		String entity = line[info.getEntityIndex()];
		// do a free base lookup for each entity and add resulting set to
		// file-specific map
		Set<String> possibleSuperEntities = FreeBaseCaller.lookup(entity);
		if( possibleSuperEntities != null ) {
			freeBaseHits.add(possibleSuperEntities);
		}
		HashSet<Pair<String, BigDecimal>> attVals = new HashSet<Pair<String, BigDecimal>>();
		// grab all attributes from columns w/ index in relevantColumns
		Integer[] relevantCols = info.getRelevantIndexes();
		String[] dimensions    = info.getColDims();
		String[] attributes    = info.getColNames();
		String[] units         = info.getColUnits();
		for (int i = 0; i < relevantCols.length; i++) {
			String[] data = line[relevantCols[i]].split("-");
			BigDecimal datum;
			if( data.length == 1 ) { //TODO conversions
				datum = new BigDecimal(data[0].replaceAll("[^\\d]\\.[^\\d]", ""));
			} else {
				double sum = 0;
				for( int j = 0; j < data.length; j++ ) {
					try {
						sum += Double.parseDouble(data[j].replaceAll("[^0-9\\.]", ""));
					} catch (NumberFormatException n ) {
						
					}
				}
				datum = new BigDecimal(sum/data.length);
			}
			attVals.add(new Pair<String, BigDecimal>(attributes[i], datum));
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
