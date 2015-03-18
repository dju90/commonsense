package commonsense;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
//import com.google.api-client;


public class TableCrawler { //should we make this an object, so it can handle multiple directories? No
	private static EntityTree attMap;
	private static UnitConverter converter;

	public static void main(String[] args) throws FileNotFoundException {
		attMap = new EntityTree();
		if( args.length == 4 ) {
			converter = new UnitConverter(args[1]);
			crawlDir(args[0], args[2]);
			PrintStream output = new PrintStream(new File(args[3]));
			output.println(attMap);
			output.close();
		} else {
			System.out.println("Run RelevanceFilterMain --> fileNameOnlyOutput.txt");
			System.out.println("args: fileNameOnlyOutput.txt unit-conversion.json dir output.txt");
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
			int counter = 0;
			while( filtrateScan.hasNextLine() ) {
				String line = filtrateScan.nextLine();
				String fileName = line.split(":")[0];
				File f = new File(dirName + "/" + fileName);
				if( counter % 10 == 0 ) {
					System.out.println("At file #" + counter + ": " + f.getName());
				}
				addToMap(f, line);
				counter++;
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
			if( info.isValid() && info.size() > 0 ) {
				HashMap<String, HashSet<Pair<String, BigDecimal>>> entityData = null;
				Set<String> entities = null;
				Set<Set<String>> freeBaseHits = null;

				if( scan.hasNextLine() ) {
					scan.nextLine();
					entityData = new HashMap<String, HashSet<Pair<String, BigDecimal>>>();
					entities = new HashSet<String>();
					freeBaseHits = new HashSet<Set<String>>(); //TODO: call freebasecaller
					
					// unpack tableInfo
					Integer[] relevantCols = info.getRelevantIndexes();
					String[] dimensions    = info.getColDims();
					String[] attributes    = info.getColNames();
					String[] units         = info.getColUnits();
//				if( relevantCols.length != dimensions.length || dimensions.length != attributes.length || attributes.length != units.length ) {
//					System.out.println("TableInfo invariant violated!!!");
//				}
					
					//process each line of the file according to relevant columns
					while (scan.hasNextLine()) {
						String[] line = scan.nextLine().split(",");
						for(int i = 0; i < line.length; i++ ) {
							line[i] = line[i].replaceAll("&[A-Za-z]+;", "").replaceAll("[^A-Za-z0-9 ]", "");
						}
						String entity = line[info.getEntityIndex()];
						entities.add(entity);
						
						HashSet<Pair<String, BigDecimal>> entry = processLine(line, entity, relevantCols, dimensions, 
																																	attributes, units);
						if( entry != null ) {
							entityData.put(entity, entry);					
						}
					}
					
					int ct = 0;
					int freq = (int) Math.round(Math.pow(5, Math.log10(entities.size())));
					int adjFreq = freq - (int) Math.round(Math.log10(freq))+1; //lower end has to be a little more frequent
					for( String entity : entities ) {
						if( ct % adjFreq == 1 ) { //don't do it for the first entity
							// do a free base lookup for fraction of entities and add resulting set to file-specific map
							Set<String> possibleSuperEntities = FreeBaseCaller.apiQuery(entity);
							if( possibleSuperEntities != null ) {
								freeBaseHits.add(possibleSuperEntities);
							}
						}
						ct++;
					}
				}
				if (entityData != null && entityData.size() > 0 
						&& freeBaseHits != null && freeBaseHits.size() > 0 ) {
					populateTree(entityData, freeBaseHits);
				}
			} else {
				System.out.println("Parse error during processing of output line for entry " + f.getName());
				System.out.println("output line = " + data);
				System.out.println("TableInfo = " + info);
				System.out.println();
			}
			scan.close();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
			System.exit(0);
		}
	}
	
	private static void populateTree(HashMap<String, HashSet<Pair<String, BigDecimal>>> entityData, 
																	 Set<Set<String>> freeBaseHits) {
		Set<String> superEntities = intersect(freeBaseHits);
		if( superEntities.size() == 0 ) {
			superEntities.add(findMaxIntersect(freeBaseHits));
		}
		for( String superEntity : superEntities ) {
			if( attMap.tree.containsKey(superEntity) ) { // append entity if superentity exists
				Map<String, HashSet<Pair<String,BigDecimal>>> currentMap = attMap.tree.get(superEntity);
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
				attMap.tree.put(superEntity, entityData);
			}
		}
	}
	
	/*TODO: steer  calves == no hits, steer calf = cattle...Stanford parser?
	 * Processes a single line in a csv file.
	 * @return The HashSet from the entity to its collection of attributes.
	 */
	private static HashSet<Pair<String, BigDecimal>> processLine(String[] line, String entity, 
																															 Integer[] relevantCols, String[] dimensions, 
																															 String[] attributes, String[] units) {
		
		HashSet<Pair<String, BigDecimal>> attVals = new HashSet<Pair<String, BigDecimal>>();
		// grab all attributes from columns w/ index in relevantColumns
		for (int i = 0; i < relevantCols.length; i++) {
			int index = relevantCols[i];
			if( index < line.length ) {
				String[] data = line[index].split("-");
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
					int divisor = numData-wrongData;
					if( divisor != 0 )
						datum = sum.divide(new BigDecimal(numData-wrongData));
					else
						datum = null;
				}
				datum = converter.convert(dimensions[i], datum, units[i]);
				if( datum != null ) {
					attVals.add(new Pair<String, BigDecimal>(attributes[i], datum));				
				}
			} else { // malformed file
				return null;
			}
		}
		if( attVals.size() > 0 ) {
			return attVals;
		} else {
			return null;
		}
	}

	private static BigDecimal extractData(String data) {
		String number = data.replaceAll("[^\\d\\.]", "");
		//\d+\.?\d+
		if( number.matches("^\\d+\\.?\\d+$")) {
			return new BigDecimal(number);
		} else if( number.matches("^\\.\\d+$") ) {
			return new BigDecimal("0"+number);
		} else if( number.matches("^\\d+\\.$")) {
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
