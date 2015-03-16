package commonsense;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UnitFilter {
	private HashSet<String> unitList;
	
	public UnitFilter(String fileName){
		unitList = parseJson(fileName);
	}
	
	private HashSet<String> parseJson(String filePath) {
		HashSet<String> tempUnitList = new HashSet<String>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
					filePath));
			// ultra-specific to our unitList file :/
			Set<?> dimensions = jsonObject.keySet();
			for (Object dim : dimensions) {
				JSONArray units = (JSONArray) jsonObject.get(dim);
				if (units != null) {
					for (int i = 0; i < units.size(); i++) {
						tempUnitList.add((String) units.get(i));
					}
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
		return tempUnitList;
	}
	
	/**
	 * Returns if cell String contains units
	 * @param candidate
	 * @return
	 */
	public String cellUnits(String candidate) {
		if( candidate.matches("(.*)[0-9]+(.*)")) {
			if( candidate.contains("min") || candidate.toLowerCase().contains("null")) {
				return null;
			}
			String unitsOnly = candidate.replaceAll("[^A-Za-z]", "").toLowerCase();
			if ( unitList.contains(unitsOnly) ) {
				return unitsOnly;
			}
		}
		return null;
	}
	
	/**
	 * Returns if the header String contains units as a token
	 * @param candidate
	 * @return
	 */
	public String headerUnits(String candidate) {
		for( String unit : unitList ) {
			if( candidate.matches("\\b" + unit + "\\b") ) {
				return unit;
			}
		}
		return null; 
	}
}


