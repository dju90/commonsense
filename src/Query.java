import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import commonsense.Pair;

public class Query {
	public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
		if(args.length == 2) {
			MongoClient mongo = new MongoClient();
			DB db = mongo.getDB("commonsense");
			DBCollection dbc = db.getCollection("relations"); 
			
			BasicDBObject fields = new BasicDBObject("_id", false).append("entity",false).append("type", false);
			
			Pattern p1 = Pattern.compile("^" + args[0].trim() + "$", Pattern.CASE_INSENSITIVE);
			Pattern p2 = Pattern.compile("^" + args[1].trim() + "$", Pattern.CASE_INSENSITIVE);
			System.out.println("For Arguments " + args[0] + " and " + args[1] + ":<br><br>");
			//System.out.println(p1);
			//System.out.println(p2);
			BasicDBObject o1 = new BasicDBObject("entity", p1);
			BasicDBObject o2 = new BasicDBObject("entity", p2);
			
			DBCursor c1 = dbc.find(o1, fields);
			DBCursor c2 = dbc.find(o2, fields);
			
			List<DBObject> result1 = c1.toArray();
			//System.out.println(result1);
			List<DBObject> result2 = c2.toArray();
			//System.out.println(result2);
			
			//Create json map 
			Scanner jsonScan = new Scanner(new File("attributes.json"));
			Map<String, Set<String>> dimensions = new HashMap<String, Set<String>>();
			String dim; 
			while(jsonScan.hasNext()) {
				dim = jsonScan.next(); 
				String attribute;
				if(dim.contains("D")) {
					dimensions.put(dim, new HashSet<String>());
					while(jsonScan.hasNext() && (!(attribute = jsonScan.next()).contains("]"))) {
						dimensions.get(dim).add(attribute);
					}
				}
			}
			jsonScan.close();
			
			if (result1.size() > 0 && result2.size() > 0) {
				Map<String, Pair<Double, Double>> intersect = new HashMap<String, Pair<Double, Double>>();
				// finds the intersection
				for (DBObject dbo : result1) {
					for (DBObject dbo2 : result2) {
						//Only one string returned
						for (String key : dbo.keySet()) {
							if (dbo.get(key) != null && dbo2.get(key) != null) {
								Pair<Double, Double> compare = new Pair<Double, Double>((Double)dbo.get(key), (Double)dbo2.get(key));
								intersect.put(key, compare);
							} else {
								//if no exact match for intersecting attributes then find next closest comparable
								for(DBObject dbo3 : result2) {
									for(String key2 : dbo3.keySet()) {
										for(String d : dimensions.keySet()) {
											if (dimensions.get(d).contains(key2) && 
													dimensions.get(d).contains(key)) {
												if (dbo.get(key) != null && dbo2.get(key2) != null) {
													Pair<Double, Double> compare = new Pair<Double, Double>((Double)dbo.get(key), (Double)dbo2.get(key2));
													intersect.put(key + " or " + key2, compare);
												}
											}
										}
									}
								}
							}
						}
					}
				}
//				System.out.println(intersect.size());
//				for(String key : intersect.keySet()) {
//					System.out.println(intersect.get(key));
//				}
				if (intersect.size() > 0) {
					determineComparison(intersect, args[0], args[1]);
				} else {
					System.out.println("These objects are incomparable<br>");
				}
			} else {
				if (result1.size() == 0) {
					System.out.println("Sorry, I dont know what " + args[0] + " is.<br>");
				} else {
					// result2.size() == 0
					System.out.println("Sorry, I dont know what " + args[1] + " is.");
				}
			}
			c1.close();
			c2.close();
			mongo.close();
		} else {
			System.out.println("Sorry, I can't make a size comparison without 2 arugments.");
		}
	}

	private static void determineComparison(Map<String, Pair<Double, Double>> intersect, String arg0, String arg1) {
		int arg1Over2 = 0;
		int arg1Less2 = 0;
		int equal = 0;
		Pair<Double, Double> comparison;
		for (String e :  intersect.keySet()) {
			comparison = intersect.get(e);
			int res = comparison.getKey().compareTo(comparison.getValue());
			String resStart = e + " of " + arg0 + " (" + comparison.getKey() + ")";
			String resEnd = e + " of " + arg1 + " (" + comparison.getValue() + ")<br>";
			if (res > 0) {
				System.out.println(resStart + " > " + resEnd);
			} else if (res == 0) {
				System.out.println(resStart + " == " + resEnd);
			} else {
				// arg1Less2 > equal or arg1Over2
				System.out.println(resStart + " < " + resEnd);
			}		
			
			
			//int res = comparison.getKey().compareTo(comparison.getValue());
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
		System.out.println("<br>");
		System.out.print("In terms of overall size comparisons, ");
		if (arg1Over2 > equal && arg1Over2 > arg1Less2) {
			System.out.println(arg0 + " is greater than " + arg1 + "<br>");
		} else if ((equal >= arg1Over2 && equal >= arg1Less2) || arg1Over2 == arg1Less2) {
			System.out.println("these two arguments are equal" + "<br>");
		} else {
			// arg1Less2 > equal or arg1Over2
			System.out.println(arg1 +  "is greater than " + arg0 + "<br>");
		}
		
	} 
}
