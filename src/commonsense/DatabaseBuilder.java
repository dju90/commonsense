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
	
	/**
	 * Takes in a HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> to add into a NoSQL key value store
	 * @param attMap a map of superentites and its entities and its attribute pairs to be inserted into a database
	 */
	public static void addToDB(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap) {
		DB db = startDatabase();
		//addMedians(attMap); 
		buildDB(attMap, db);
	}
	
	/**
	 * Takes the super class and finds the median attributes of its entities and adds them to the attMap as the class
	 * @param attMap the map of superentities, is modified to include another hashmap
	 */
	private static void addMedians(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap) {
		for (Map.Entry<String, HashMap<String, HashSet<Pair<String, String>>>> entityEntry :  attMap.entrySet() ) {
			// Examine every superentity
			HashMap<String, HashSet<Pair<String, String>>> medianMap = new HashMap<String, HashSet<Pair<String, String>>>();			
			// Adding each pair of relations into relationColl
			for (Map.Entry<String, HashSet<Pair<String, String>>> relationEntry: entityEntry.getValue().entrySet()) {
				// Examine every entity
				HashSet<Pair<String, String>> medianRelations = new HashSet<Pair<String, String>>();
				for (Pair<String, String> relationPair : relationEntry.getValue()) {
					// Examine every relation
				}
				medianMap.put(entityEntry.getKey(), medianRelations);
			}
			attMap.put(entityEntry.getKey(), medianMap);
		}
	}

	/**
	 * Starts a database with mongodb with DBName and returns the db connection
	 * @return the database connection after connecting to mongo, null if an error occurred.
	 */
	private static DB startDatabase() {
		MongoClient mongoClient;
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
				BasicDBObject relation = new BasicDBObject("superentity", entityEntry.getKey()).append("entity", entityEntry.getKey());
				for (Pair<String, String> relationPair : relationEntry.getValue()) {
					// Examine every relation
					relation.append(relationPair.getKey(), relationPair.getValue());
				}
				relationColl.insert(relation);
			}
		}
		// Create an index over entities
		relationColl.createIndex(new BasicDBObject("entity", "text"));
	}
}
