package commonsense;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UnitConverter {
	private Map<String, HashMap<String, BigDecimal>> unitTable;
	
	public UnitConverter(String fileName){
		unitTable = parseJson(fileName);
	}
	
	public BigDecimal convert(String dim, BigDecimal num, String unit) {
		if( num != null ) {
			Map<String, BigDecimal> possibleUnits = unitTable.get(dim);
			if( possibleUnits != null ) {
				BigDecimal factor = possibleUnits.get(unit);
				if( factor != null ) {
					return factor.multiply(num);
				}
			}
		}
		return null;
	}
	
	public double calculate(String dim, BigDecimal num, String unit) {
		Map<String, BigDecimal> possibleUnits = unitTable.get(dim);
		if( possibleUnits != null ) {
			System.out.println(unit.replaceAll(" {2,}", " "));
			BigDecimal factor = possibleUnits.get(unit.replaceAll(" {2,}", " "));
			if( factor != null ) {
				return factor.multiply(num).doubleValue();
			} else {
				return Double.POSITIVE_INFINITY;
			}
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}
	
	private Map<String, HashMap<String, BigDecimal>> parseJson(String filePath) {
		Map<String, HashMap<String, BigDecimal>> tempUnitTable = new HashMap<String, HashMap<String, BigDecimal>>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
					filePath));
			// ultra-specific to our units-conversion.json file :/
			Set<?> dimensions = jsonObject.keySet();
			for (Object dim : dimensions) { 
				JSONArray unitMap = (JSONArray) jsonObject.get(dim); //get array of units
				if (unitMap != null) {
					HashMap<String, BigDecimal> unitList = new HashMap<String, BigDecimal>();
					for ( Object obj : unitMap) {
						Set<?> units = ((JSONObject) obj).keySet();
						for( Object unit : units) {
							unitList.put((String)unit, new BigDecimal("" + ((JSONObject) obj).get((String)unit)));
						}
					}
					tempUnitTable.put((String) dim, unitList);
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
		return tempUnitTable;
	}
}
