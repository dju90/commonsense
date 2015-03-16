package commonsense;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AttributeFilter {
	private HashMap<String, String> attributeList;
	private UnitFilter uF;

	public AttributeFilter(String attributeFilePath, String unitFilePath) {
		attributeList = parseJson(attributeFilePath);
		uF = new UnitFilter(unitFilePath);
	}

	private HashMap<String, String> parseJson(String filePath) {
		HashMap<String, String> tempList = new HashMap<String, String>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
					filePath));
			// ultra-specific to our attributeList file :/
			jsonObject = (JSONObject) ((JSONObject) jsonObject.get("attribute"))
					.get("size");
			Set<?> dimensions = jsonObject.keySet();
			for (Object dimension : dimensions) {
				String dim = "" + dimension;				
				JSONArray attributes = (JSONArray) jsonObject.get(dim);
				if (attributes != null) {
					for (int i = 0; i < attributes.size(); i++) {
						tempList.put((String) attributes.get(i), dim);
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
		return tempList;
	}
	
	/**
	 * Returns a TableInfo object with all the relevant column information.
	 * @param f
	 * @return
	 */
	public TableInfo relevance(File f) {
		TableInfo info = new TableInfo(f);
		String[] colHeadings = info.ready(5);
		if( colHeadings != null ) {
			int entityI = info.getEntityIndex();
			for (int i = 0; i < colHeadings.length && info.isValid(); i++) { // each col heading
				if( i != entityI ) { //entity col != attribute col
					String columnCandidate = colHeadings[i].replaceAll("[^A-Za-z0-9 ]", "");
					for( String attribute : attributeList.keySet() ) { //check for regex match on possible atts
						if( columnCandidate.matches("\\b" + attribute + "\\b") ){ // attribute token
							String unit = containsUnits(i, columnCandidate, info);
							if( unit != null && unit != "BAD_TABLE") {
								info.addRelevantColumn(i, attributeList.get(attribute), columnCandidate, unit);							
							} else { // unit == "BAD_TABLE" || unit )
								info.nullify(); // bad table format
								break;
							}
						} // no match, go to the next possible attribute
					}
				}
			}
			return info;
		} else { // empty table
			return null;
		}
	}
	
	/*
	 * Returns if a column contains units either in the header or cells
	 */
	private String containsUnits(int index, String candidate, TableInfo info) {
		String hUnits = uF.headerUnits(candidate);
		if( hUnits == null ) {	// the attribute does not contain units
			try {
				String[] columns1 = info.getLine(0).replaceAll(" {2,}", " ").split(",");
				String[] columns2 = info.getLine(1).replaceAll(" {2,}", " ").split(",");
				String col1Unit = uF.cellUnits(columns1[index]); 
				String col2Unit = uF.cellUnits(columns2[index]);
				if( col1Unit == null && col2Unit == null )  {
					return null;
				} else {
					String retUnit = col2Unit == null ? col1Unit : col2Unit;
					return retUnit;
				}
			} catch( IndexOutOfBoundsException i ) {
				return "BAD_TABLE";
			}
		} else {
			return hUnits;
		}
	}
}