package commonsense;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Wrapper class for superentity { entity { attribute pairs hashmap in TableCrawler.java
 * @author DJu90
 *
 */
public class EntityTree {
	public HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> tree;
	
	/**
	 * Constructs a new, empty EntityTree object
	 */
	public EntityTree() {
		this(null);
	}
	
	/**
	 * Constructs a new EntityTree object from the given JSON file
	 * NOTE: ultra-specific! careful with formatting
	 * @param filePath
	 */
	public EntityTree(String filePath) {
		parseJson(filePath);
	}
	
	private void parseJson(String filePath) {
		tree = new HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>>();
		if( filePath != null ) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
						filePath));
				if( jsonObject != null ) {
					Set<?> superEntities = jsonObject.keySet();
					for (Object superEntity : superEntities) { 
						System.out.println(superEntity);
						JSONObject innerJsonObject = (JSONObject) jsonObject.get(superEntity); //get array of units
						HashMap<String, HashSet<Pair<String, BigDecimal>>> entityData = 
								new HashMap<String, HashSet<Pair<String, BigDecimal>>>();
						Set<?> entities = innerJsonObject.keySet();
						for( Object entity : entities ) {
							System.out.println("\t" + entity);
							JSONArray attributes = (JSONArray) (innerJsonObject.get(entity));
							HashSet<Pair<String, BigDecimal>> attributeList = new HashSet<Pair<String, BigDecimal>>();
							for( int i = 0; i < attributes.size(); i++ ) {
								JSONObject attribute = (JSONObject) attributes.get(i);
								String attName = "";
								for( Object att : attribute.keySet() ) {
									attName = att.toString();
								}
								System.out.println("\t\t" + attName + ": " + attribute.get(attName).toString());
								Pair<String, BigDecimal> data = 
										new Pair<String, BigDecimal>(attName, 
												new BigDecimal(attribute.get(attName).toString()));
								attributeList.add(data);
							}
							entityData.put(entity.toString(), attributeList);
						}
						tree.put(superEntity.toString(), entityData);
					}
				}
			} catch (FileNotFoundException fnfe) {
				System.out.println("File not found.");
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("Input/output error.");
				ioe.printStackTrace();
			} catch (ParseException pe) {
				System.out.println("Parse error.");
				pe.printStackTrace();
			}
		}
	}
	
	public String toString() {
		String rep = "{\n";
		Set<String> superKeys = tree.keySet();
		int superSize = superKeys.size();
		int outerCt = 1;
		for( String superEntity: superKeys ) {
			rep += "\""+superEntity + "\": {\n";
			Map<String, HashSet<Pair<String, BigDecimal>>> entities = tree.get(superEntity);
			
			Set<String> entityKeys = entities.keySet();
			int entitySize = entityKeys.size();
			int innerCt = 1;
			for( String entity : entityKeys ) {
				rep += "\t\"" + entity + "\": [\n";
				Set<Pair<String,BigDecimal>> attributes = entities.get(entity);
				
				int attSize = attributes.size();
				int metaCt = 1;
				for( Pair<String,BigDecimal> attribute : attributes ) {
					if( metaCt < attSize ) {
						rep += "\t\t" + attribute.toString() + ",\n";						
					} else {
						rep += "\t\t" + attribute.toString() + "\n";
					}
					metaCt++;
				}
				
				if( innerCt < entitySize ) {
					rep += "\t],\n";
				} else {
					rep += "\t]\n";
				}
				innerCt++;
			}
			
			if( outerCt < superSize ) {
				rep += "},\n";
			} else {
				rep += "}\n";
			}
			outerCt++;
		}
		rep += "}";
		return rep;
	}
}
