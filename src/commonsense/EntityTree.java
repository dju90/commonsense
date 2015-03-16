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
		String rep = "";
		for( String superEntity: tree.keySet() ) {
			rep += superEntity + ": {\n";
			Map<String, HashSet<Pair<String, BigDecimal>>> entities = tree.get(superEntity);
			for( String entity : entities.keySet() ) {
				rep += "\t" + superEntity + "= [\n";
				Set<Pair<String,BigDecimal>> attributes = entities.get(entity);
				for( Pair<String,BigDecimal> attribute : attributes ) {
					rep += "\t\t" + attribute.toString() + "\n";
				}
				rep += "\t]\n";
			}
			rep += "}\n";
		}
		return rep;
	}
}
