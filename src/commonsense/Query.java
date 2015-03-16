package commonsense;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Query {
	public static void main(String[] args) throws UnknownHostException {
		if(args.length == 1) {
		//System.out.println("starting query");
			MongoClient mongo = new MongoClient();
			DB db = mongo.getDB("commonsense");
			DBCollection dbc = db.getCollection("relations"); 
			BasicDBObject o = new BasicDBObject("entity", args[0]); 
			//System.out.println(o);
			//o.put("entity", args[0]);       
			DBCursor c = dbc.find(o);
			try {
			while(c.hasNext()) {
				System.out.println(c.next());
			}
			} finally {
				c.close();
			}
			mongo.close();
		}
	}
}