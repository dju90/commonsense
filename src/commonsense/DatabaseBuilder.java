package commonsense;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DatabaseBuilder {
	private static String DBName = "commonsense";
	private static MongoClient mongoClient = null;
	/**
	 * Takes in a HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> to add into a NoSQL key value store
	 * @param attMap a map of superentites and its entities and its attribute pairs to be inserted into a database
	 */
	public static void addToDB(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap) {
		DB db = startDatabase();
		addMedians(attMap); 
		buildDB(attMap, db);
		endDatabase();
	}
	
	// Closes the connection to the database
	private static void endDatabase() {
		if (mongoClient != null) {
			mongoClient.close();	
		}
	}

	/**
	 * Takes the super class and finds the median attributes of its entities and adds them to the attMap as the class
	 * @param attMap the map of superentities, is modified to include another hashmap
	 */
	private static void addMedians(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap) {
		for (Map.Entry<String, HashMap<String, HashSet<Pair<String, String>>>> entityEntry :  attMap.entrySet() ) {
			// Examine every superentity
			
			// Attribute to a set of values of that attribute
			HashMap<String, HashSet<BigDecimal>> medianAttributes = new HashMap<String, HashSet<BigDecimal>>();
			for (Map.Entry<String, HashSet<Pair<String, String>>> relationEntry: entityEntry.getValue().entrySet()) {
				// Examine every entity
				for (Pair<String, String> relationPair : relationEntry.getValue()) {
					// Examine every relation
					// Add each of the attributes to medianAttribute
					HashSet<BigDecimal> allValues;
					if (medianAttributes.containsKey(relationPair.getKey())) {
						allValues = medianAttributes.get(relationPair.getKey());
					} else {
						allValues = new HashSet<BigDecimal>();
					}
					// pattern matches not a decimal number
					String noDecimalPattern = "[^\\d\\.-]";
					String decimalPattern = ".*\\d+[\\.\\d+].*";
					String numberRange = ".*\\d-\\d.*";
					String stringValue = relationPair.getValue().replaceAll(noDecimalPattern, "");
					double val = 0.0;
					if (stringValue.matches(numberRange)) {
						String[] nums = stringValue.split("-");
						// Assume only 2
						double v1 = Double.parseDouble(nums[0]);
						double v2 = Double.parseDouble(nums[1]);
						val = (v1 + v2) / 2.0;
					} else {
						val = Double.parseDouble(relationPair.getValue().replaceAll(noDecimalPattern, ""));
					}
					String units = relationPair.getValue().replaceAll(decimalPattern, "");
					units = units.toLowerCase();
					BigDecimal value = UnitConverter.convertUnits(val, units);
					allValues.add(value);
					medianAttributes.put(relationPair.getKey(), allValues);
				}
			}
			
			HashSet<Pair<String, String>> medianRelations = new HashSet<Pair<String, String>>();
			// Determine medians and add to medianRelations
			for (Map.Entry<String, HashSet<BigDecimal>> medianEntry : medianAttributes.entrySet()) {
				BigDecimal[] medianArray = medianEntry.getValue().toArray(new BigDecimal[medianEntry.getValue().size()]);
				java.util.Arrays.sort(medianArray);
				BigDecimal median;
				if (medianArray.length % 2 == 0) {
				    median = (medianArray[medianArray.length/2].add(medianArray[medianArray.length/2 - 1])).divide(new BigDecimal(2));
				} else {
				    median = medianArray[medianArray.length/2];
				}
				Pair<String, String> pair = new Pair<String, String>(medianEntry.getKey(), Double.toString(median.doubleValue()));
				medianRelations.add(pair);
			}
			HashMap<String, HashSet<Pair<String, String>>> values;
			if (attMap.containsKey("")) {
				values = attMap.get("");		
			} else {
				values = new HashMap<String, HashSet<Pair<String, String>>>();
			}
		
			values.put(entityEntry.getKey(), medianRelations);
			attMap.put(entityEntry.getKey(), values);			
		}
	}

	/**
	 * Starts a database with mongodb with DBName and returns the db connection
	 * @return the database connection after connecting to mongo, null if an error occurred.
	 */
	private static DB startDatabase() {
		
		DB db = null;
		try {
			mongoClient = new MongoClient();
			db = mongoClient.getDB( DBName );
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return db;
	}
	
	/**
	 * Reads through attMap to add to relations with a rough schema of : superentity, entity, relation attributes
	 * @param attMap the attribute map containing superentity, entity, and relation information
	 * @param db the database connection to insert to the database
	 */
	private static void buildDB(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap, DB db) {
		// Create the collection here since this assumes these collections have not been created before
		DBCollection relationColl = db.createCollection("relations", new BasicDBObject());
		for (Map.Entry<String, HashMap<String, HashSet<Pair<String, String>>>> entityEntry :  attMap.entrySet() ) {
			// Examine every superentity
			for (Map.Entry<String, HashSet<Pair<String, String>>> relationEntry: entityEntry.getValue().entrySet()) {
				// Examine every entity
				// Adding each pair of relations into relationColl
				BasicDBObject relation = new BasicDBObject("type", entityEntry.getKey()).append("entity", relationEntry.getKey());
				for (Pair<String, String> relationPair : relationEntry.getValue()) {
					// Examine every relation
					relation.append(relationPair.getKey(), relationPair.getValue());
				}
				relationColl.insert(relation);
			}
		}
		// Create an index over entities
		relationColl.createIndex(new BasicDBObject("entity", 1));
	}
}
