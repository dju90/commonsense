package commonsense;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Query {
	public static final double MATCH_THRESHOLD = 0.8; 
	private static List<DBObject> all;
	
	public static String query(String[] args) throws UnknownHostException {
		String result = "";
		if(args.length == 2) {
						MongoClient mongo = new MongoClient();
			DB db = mongo.getDB("commonsense");
			DBCollection dbc = db.getCollection("relations"); 
			BasicDBObject fields = new BasicDBObject("_id", false).append("entity", false).append("type", false);
			all = dbc.find().toArray();
			String e1 = closestMatch(args[0], MATCH_THRESHOLD);
			String e2 = closestMatch(args[1], MATCH_THRESHOLD);
			Pattern p1 = Pattern.compile("^" + e1 + "$", Pattern.CASE_INSENSITIVE);
			Pattern p2 = Pattern.compile("^" + e2 + "$", Pattern.CASE_INSENSITIVE);
			BasicDBObject o1 = new BasicDBObject("entity", p1);
			BasicDBObject o2 = new BasicDBObject("entity", p2);
			
			
			DBCursor c1 = dbc.find(o1, fields);
			DBCursor c2 = dbc.find(o2, fields);
			
			List<DBObject> result1 = c1.toArray();
			List<DBObject> result2 = c2.toArray();
			if (result1.size() > 0 && result2.size() > 0) {
				Map<String, Pair<Double, Double>> intersect = new HashMap<String, Pair<Double, Double>>();
				// finds the intersection
				for (DBObject dbo : result1) {
					for (DBObject dbo2 : result2) {
						for (String key : dbo.keySet()) {
							if (dbo.get(key) != null && dbo2.get(key) != null) {
								Pair<Double, Double> compare = new Pair<Double, Double>((Double)dbo.get(key), (Double)dbo2.get(key));
								intersect.put(key, compare);
							}
						}
					}
				}
				
				if (intersect.size() > 0) {
					result = determineComparison(intersect, args[0], args[1]);
				} else {
					System.out.println("Incomparable");
					result = "Incomparable";
				}
			} else {
				if (result1.size() == 0) {
					System.out.println("Dont know" + args[0]);
					result =  "Dont know " + args[0];
				} else {
					// result2.size() == 0
					System.out.println("Dont know" + args[1]);
					result = "Dont know " + args[1];
				}
			}
			c1.close();
			c2.close();
			mongo.close();
		}
		return result;
	}

	private static String determineComparison(Map<String, Pair<Double, Double>> intersect, String arg0, String arg1) {
		int arg1Over2 = 0;
		int arg1Less2 = 0;
		int equal = 0;
		for (Pair<Double, Double> comparison :  intersect.values() ) {
			int res = comparison.getKey().compareTo(comparison.getValue());
			if (res > 0) {
				// arg1 > arg2
				arg1Over2++;
			} else if (res == 0) {
				equal++;
			} else {
				// arg1 < arg2
				arg1Less2++;
			}
		}
		
		if (arg1Over2 > equal && arg1Over2 > arg1Less2) {
			return arg0;
		} else if ((equal >= arg1Over2 && equal >= arg1Less2) || arg1Over2 == arg1Less2) {
			return "equal";
		} else {
			// arg1Less2 > equal or arg1Over2
			return arg1;
		}
		
	}
	
	private static String closestMatch(String word, double threshold) {
		for( DBObject entity : all ) {
			for( String key : entity.keySet() ) {
				if( SimilarityCalculationDemo.similarityIndex(word, key) >= threshold ) {
					return key;
				}
			}
		}
		return word;
	}
}