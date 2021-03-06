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
	 * Takes in a HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> to add into a NoSQL key value store
	 * @param attMap a map of superentites and its entities and its attribute pairs to be inserted into a database
	 */
	public static void addToDB(EntityTree tree) {
		DB db = startDatabase();
		addMedians(tree.tree); 
		buildDB(tree.tree, db);
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
	private static void addMedians(HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> attMap) {
		HashMap<String, HashSet<Pair<String, BigDecimal>>> values = new HashMap<String, HashSet<Pair<String, BigDecimal>>>();
		for (Map.Entry<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> entityEntry :  attMap.entrySet() ) {
			// Examine every superentity
			// Attribute to a set of values of that attribute
			HashMap<String, HashSet<BigDecimal>> medianAttributes = new HashMap<String, HashSet<BigDecimal>>();
			for (Map.Entry<String, HashSet<Pair<String, BigDecimal>>> relationEntry: entityEntry.getValue().entrySet()) {
				// Examine every entity
				for (Pair<String, BigDecimal> relationPair : relationEntry.getValue()) {
					// Examine every relation
					// Add each of the attributes to medianAttribute
					HashSet<BigDecimal> allValues;
					if (medianAttributes.containsKey(relationPair.getKey())) {
						allValues = medianAttributes.get(relationPair.getKey());
					} else {
						allValues = new HashSet<BigDecimal>();
					}
					allValues.add(relationPair.getValue());
					medianAttributes.put(relationPair.getKey(), allValues);
					
				}
			}
			
			HashSet<Pair<String, BigDecimal>> medianRelations = new HashSet<Pair<String, BigDecimal>>();
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
				Pair<String, BigDecimal> pair = new Pair<String, BigDecimal>(medianEntry.getKey(), new BigDecimal(median.doubleValue()));
				medianRelations.add(pair);
			}
			values.put(entityEntry.getKey(), medianRelations);
		}
		attMap.put("type", values);
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
	private static void buildDB(HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> attMap, DB db) {
		// Create the collection here since this assumes these collections have not been created before
		boolean collectionExists = db.collectionExists("relations");
	    if (collectionExists == false) {
	        db.createCollection("relations", new BasicDBObject());
	    }
		DBCollection relationColl = db.getCollection("relations");
		for (Map.Entry<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> entityEntry :  attMap.entrySet() ) {
			// Examine every superentity
			for (Map.Entry<String, HashSet<Pair<String, BigDecimal>>> relationEntry: entityEntry.getValue().entrySet()) {
				// Examine every entity
				// Adding each pair of relations into relationColl
				BasicDBObject relation = new BasicDBObject("type", entityEntry.getKey()).append("entity", relationEntry.getKey());
				for (Pair<String, BigDecimal> relationPair : relationEntry.getValue()) {
					// Examine every relation
					relation.append(relationPair.getKey(), relationPair.getValue().doubleValue());
				}
				relationColl.insert(relation);
			}
		}
		// Create an index over entities
		relationColl.createIndex(new BasicDBObject("entity", 1));
	}
}
