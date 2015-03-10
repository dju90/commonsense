package commonsense;
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
			HashMap<String, HashSet<Double>> medianAttributes = new HashMap<String, HashSet<Double>>();
			for (Map.Entry<String, HashSet<Pair<String, String>>> relationEntry: entityEntry.getValue().entrySet()) {
				// Examine every entity
				for (Pair<String, String> relationPair : relationEntry.getValue()) {
					// Examine every relation
					// Add each of the attributes to medianAttribute
					HashSet<Double> allValues;
					if (medianAttributes.containsKey(relationPair.getKey())) {
						allValues = medianAttributes.get(relationPair.getKey());
					} else {
						allValues = new HashSet<Double>();
					}
					// pattern matches not a decimal number
					String noDecimalPattern = "[^\\d+[\\.\\d+]]";
					String decimalPattern = "\\d+[\\.\\d+]";
					double val = Double.parseDouble(relationPair.getValue().replaceAll(noDecimalPattern, ""));
					// TODO convert units to standard? 
					String units = relationPair.getValue().replaceAll(decimalPattern, "");
					val = UnitConverter.convertUnits(val, units);
					allValues.add(val);
					medianAttributes.put(relationPair.getKey(), allValues);
				}
			}
			
			HashSet<Pair<String, String>> medianRelations = new HashSet<Pair<String, String>>();
			// Determine medians and add to medianRelations
			for (Map.Entry<String, HashSet<Double>> medianEntry : medianAttributes.entrySet()) {
				Double[] tempArray = medianEntry.getValue().toArray(new Double[medianEntry.getValue().size()]);
				double[] medianArray = new double[tempArray.length];
				for (int i = 0; i < tempArray.length; i++) {
					medianArray[i] = tempArray[i];
				}
				java.util.Arrays.sort(medianArray);
				double median;
				if (medianArray.length % 2 == 0) {
				    median = (medianArray[medianArray.length/2] + medianArray[medianArray.length/2 - 1])/2.0;
				} else {
				    median = medianArray[medianArray.length/2];
				}
				Pair<String, String> pair = new Pair<String, String>(medianEntry.getKey(), Double.toString(median));
				medianRelations.add(pair);
			}
			HashMap<String, HashSet<Pair<String, String>>> values = attMap.get(entityEntry.getKey());
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
