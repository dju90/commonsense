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
	 * 
	 */
	public TableInfo relevance(File f) {
		System.out.println("relevance called");
		try {
			TableInfo info = new TableInfo(f.getName());
			Scanner scan = new Scanner(f);
			if( scan.hasNextLine() ) {
				String[] colHeadings = scan.nextLine().split(",");// top line
				for( int i = 0; i < 5 && scan.hasNextLine(); i++ ) { //first five lines to print
					info.addLine(scan.nextLine());
				}
				if( info.setEntityCol() ) { // valid entity column			
					System.out.println("entity column found, colHeadings.length = " + colHeadings.length + ", info = " + info.isValid() );
					for (int i = 0; i < colHeadings.length && info.isValid(); i++) { // each col heading
						System.out.print("***");
						String columnCandidate = colHeadings[i].replaceAll("[^A-Za-z0-9 ]", "");
						System.out.print(columnCandidate);
						for( String attribute : attributeList.keySet() ) { //check for regex match on possible atts
							if( columnCandidate.matches("\\b" + attribute + "\\b") ){ // attribute token
								System.out.println("attribute match");
								String unit = containsUnits(i, columnCandidate, info);
								if( unit != null && unit != "BAD_TABLE") {
									System.out.println("added");
									info.addRelevantColumn(i, attributeList.get(attribute), columnCandidate, unit);							
								} else { // unit == "BAD_TABLE" || unit )
									info.nullify(); // bad table format
									break;
								}
							} // no match, go to the next possible attribute
						}
					}
					scan.close();
					return info;
				} else { // no entity column
					scan.close();
					return null;
				}
			} // empty table
			scan.close();
			return null;
		} catch(FileNotFoundException fe) {
			return null;
		}
	}

	/**
	 * Returns the indices of relevant columns within a csv table file
	 * 
	 * @param scan
	 *            Scanner with file pre-loaded
	 * @return An ArrayList containing the indices of the relevant columns
	 * 			null if invalid file name or empty file
	 * @throws FileNotFoundException
	 */
	public ArrayList<String> relevantColumnHeadings(File f) {
		try {
			Scanner scan = new Scanner(f);
			if( scan.hasNextLine() ) {
				// top line
				String[] attributes = scan.nextLine().split(",");
				ArrayList<String> relevantColumns = new ArrayList<String>();
				
				for (int i = 0; i < attributes.length && relevantColumns != null; i++) {
					String columnCandidate = attributes[i].replaceAll("[^A-Za-z0-9 ]", "");
					for( String attributeRegex : attributeList.keySet() ) {
						if( columnCandidate.matches(attributeRegex) ){
							relevantColumns.add(i + ": " + attributeList.get(attributeRegex) + ";" + columnCandidate);
						}
					}
				}
				scan.close();
				return relevantColumns;
			}
			scan.close();
			return null;
		} catch(FileNotFoundException fe) {
			return null;
		}
	}
	
	/*
	 * 
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
	
	/**
	 * Returns an integer array of the relevant column indices
	 * @param f
	 * @return
	 */
	public Integer[] relevantColumnIndexes(File f) {
		try {
			Scanner scan = new Scanner(f);
			if( scan.hasNextLine() ) {
				String[] attributes = scan.nextLine().split(",");
				ArrayList<Integer> relevantColumns = new ArrayList<Integer>();
				for (int i = 0; i < attributes.length; i++) {
					String columnCandidate = attributes[i].replaceAll("[^A-Za-z0-9 ]", "");
					for( String attributeRegex : attributeList.keySet() ) {
						if( columnCandidate.matches(attributeRegex) ){
							relevantColumns.add(i);
						}
					}
				}
				scan.close();
				return relevantColumns.toArray(new Integer[relevantColumns.size()]);
			}
			scan.close();
			return null;
		} catch(FileNotFoundException fe) {
			return null;
		}
		
	}
}