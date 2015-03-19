package commonsense;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityTree {
	protected HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> tree;
	
	public EntityTree() {
		tree = new HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>>();
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
