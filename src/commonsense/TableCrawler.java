package commonsense;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//import com.google.api-client;


public class TableCrawler { //should we make this an object, so it can handle multiple directories? No
	private static HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> attMap;
	private static UnitConverter converter;

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
					
					// unpack tableInfo
					Integer[] relevantCols = info.getRelevantIndexes();
					String[] dimensions    = info.getColDims();
					String[] attributes    = info.getColNames();
					String[] units         = info.getColUnits();
					
					//process each line of the file according to relevant columns
					while (scan.hasNextLine()) {
						String[] line = scan.nextLine().split(",");
						String entity = line[info.getEntityIndex()];
						HashSet<Pair<String, BigDecimal>> entry = processLine(line, entity, relevantCols, dimensions, 
																																	attributes, units, freeBaseHits);
						if( entry != null ) {
							entityData.put(entity, entry);					
						}
						line = scan.nextLine().split(",");
					}
				}
				if (entityData != null && freeBaseHits != null) {
					Set<String> superEntities = intersect(freeBaseHits);
					if( superEntities.size() == 0 ) {
						superEntities.add(findMaxIntersect(freeBaseHits));
					}
					for( String superEntity : superEntities ) {
						if( attMap.containsKey(superEntity) ) { // append entity if superentity exists
							Map<String, HashSet<Pair<String,BigDecimal>>> currentMap = attMap.get(superEntity);
							for( String key : entityData.keySet() ) { 
								if( currentMap.containsKey(key) ) { // append attributes to entity if entity exists w/in superentity 
									Set<Pair<String,BigDecimal>> currentAtts = currentMap.get(key);
									Set<Pair<String,BigDecimal>> appendAtts  = entityData.get(key);
									for( Pair<String,BigDecimal> att : appendAtts ) {
										currentAtts.add(att);
									}
								} else { // append entity to superentity mapping
									currentMap.put(key, entityData.get(key));									
								}
							}
						} else { //create new superentity
							attMap.put(superEntity, entityData);
						}
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
	
	/*TODO: steer  calves == no hits, steer calf = cattle...Stanford parser?
	 * Processes a single line in a csv file.
	 * @return The HashSet from the entity to its collection of attributes.
	 */
	private static HashSet<Pair<String, BigDecimal>> processLine(String[] line, String entity, 
																															 Integer[] relevantCols, String[] dimensions, 
																															 String[] attributes, String[] units, 
																															 Set<Set<String>> freeBaseHits) {
		
		// do a free base lookup for each entity and add resulting set to file-specific map
		Set<String> possibleSuperEntities = FreeBaseCaller.lookup(entity);
		if( possibleSuperEntities != null ) {
			freeBaseHits.add(possibleSuperEntities);
		}
		
		HashSet<Pair<String, BigDecimal>> attVals = new HashSet<Pair<String, BigDecimal>>();
		// grab all attributes from columns w/ index in relevantColumns
		for (int i = 0; i < relevantCols.length; i++) {
			String[] data = line[relevantCols[i]].split("-");
			BigDecimal datum;
			if( data.length == 1 ) { //TODO conversions
				datum = extractData(data[0]);
			} else {
				BigDecimal sum = new BigDecimal(0);
				int numData = data.length;
				int wrongData = 0;
				for( int j = 0; j < numData; j++ ) {
					BigDecimal addend = extractData(data[j]);
					if( addend != null ) {
						sum.add(addend);
					} else {
						wrongData++;
					}
				}
				datum = sum.divide(new BigDecimal(numData-wrongData));
			}
			datum = converter.convert(dimensions[i], datum, units[i]);
			if( datum != null ) {
				attVals.add(new Pair<String, BigDecimal>(attributes[i], datum));				
			}
		}
		if( attVals.size() > 0 )
			return attVals;
		else
			return null;
	}

	private static BigDecimal extractData(String data) {
		String number = data.replaceAll("[^\\d\\.]", "");
		if( number.matches("^\\d*\\.?\\d*$")) {
			return new BigDecimal(number);
		} else if( number.matches("^\\d*\\.$") ) {
			return new BigDecimal(number+"0");
		} else {
			return null;
		}
	}
	
	/*
	 * Returns the intersect of a set of sets
	 */
	private static Set<String> intersect(Set<Set<String>> freeBaseHits) {
		Set<String> intersect = new HashSet<String>();
		int size = freeBaseHits.size();
		if( size == 0 ) {
			return intersect;
		} else {
			Iterator<Set<String>> iter = freeBaseHits.iterator();
			if( size == 1 ) {
				return iter.next();
			} else {
				Set<String> set0 = iter.next();
				for( String s : set0 ) {
					intersect.add(s);
				}
				while( iter.hasNext() ) {
					intersect.retainAll(iter.next());
					if( intersect.size() == 0 ) {
						return intersect;
					}
				}
				return intersect;
			}
		}
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
