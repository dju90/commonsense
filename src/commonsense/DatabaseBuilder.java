package commonsense;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DatabaseBuilder {
	private static String dbName = "mydb"; // TODO get better name for database name
	
	/**
	 * Takes in a HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> to add into a NoSQL key value store
	 * @param args
	 */
	public static void addToDB(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap) {
		DB db = startDatabase();
		buildDB(attMap, db);
	}
	
	/**
	 * Starts a database with mongo with dbName and returns the db connection
	 * @return
	 */
	private static DB startDatabase() {
		MongoClient mongoClient;
		DB db = null;
		try {
			mongoClient = new MongoClient();
			db = mongoClient.getDB( dbName );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}
	
	/**
	 * Reads through attMap to add to entities with a rough schema of : superentity, entity
	 * and to relations with a rough schema of : superentity, entity, relation attributes
	 * @param attMap
	 * @param db
	 */
	private static void buildDB(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMap, DB db) {
		// Create the collection here since this assumes these collections have not been created before
		DBCollection relationColl = db.createCollection("relations", new BasicDBObject());
		DBCollection entityColl = db.createCollection("entities", new BasicDBObject());
		for (String superEntity: attMap.keySet() ) {
			HashMap<String, HashSet<Pair<String, String>>> entityRelation = attMap.get(superEntity);
			for (String entity: entityRelation.keySet()) {
				HashSet<Pair<String, String>> relationSet = entityRelation.get(entity);
				// Adding each super entity and entity into entityColl
				BasicDBObject entityClass = new BasicDBObject("superentity", superEntity).append("entity", entity);
				entityColl.insert(entityClass);
				// Adding each pair of relations into relationColl
				BasicDBObject relation = new BasicDBObject("superentity", superEntity).append("entity", entity);
				for (Pair<String, String> relationPair : relationSet) {
					relation.append(relationPair.getKey(), relationPair.getValue());
				}
				relationColl.insert(relation);
			}
		}
		// Create an index over entities
		relationColl.createIndex(new BasicDBObject("entity", "text"));
		entityColl.createIndex(new BasicDBObject("entity", "text"));
	}
	
	/**
	 * Foreach superentity take the medians of the attributes for each entity add to the relations table
	 */
	public static void determineMedians() {
		// TODO 
	}
}
