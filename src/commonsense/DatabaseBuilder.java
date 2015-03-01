package commonsense;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;

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
		buildDB(attMap, db);
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
		for (String superEntity: attMap.keySet() ) {
			// Examine every superentity
			HashMap<String, HashSet<Pair<String, String>>> entityData = attMap.get(superEntity);
			for (String entity: entityData.keySet()) {
				// Examine every entity
				HashSet<Pair<String, String>> relationSet = entityData.get(entity);
				// Adding each pair of relations into relationColl
				BasicDBObject relation = new BasicDBObject("superentity", superEntity).append("entity", entity);
				for (Pair<String, String> relationPair : relationSet) {
					// Examine every relation
					relation.append(relationPair.getKey(), relationPair.getValue());
				}
				relationColl.insert(relation);
			}
		}
		// Create an index over entities
		// TODO also create an index over superentity?
		relationColl.createIndex(new BasicDBObject("entity", "text"));
	}
}
