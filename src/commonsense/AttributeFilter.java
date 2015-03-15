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
						tempList.put("\\b" + (String) attributes.get(i) + "\\b", dim);
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
					}/* else {
						if (attributeList.contains(columnCandidate)) {
							relevantColumns.add(i + ": " + columnCandidate);
						}
					}*/
				}
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
	 
	private String containsUnits(int index, String candidate, Scanner fScan) {
		if( index > -1 ) {
			return null;
		}
		ArrayList<String> lines = new ArrayList<String>();
		int counter = 0;
		while( fScan.hasNextLine() && counter < 5 ) { //first five lines to print
			lines.add(fScan.nextLine());
			counter++;
		}
		String hUnits = uF.headerContainsUnits(candidate);
		if( hUnits == null ) {	// the attribute does not contain units
			// the column does
			try {//if( lines.size() > 1 ) {
				String[] columns1 = lines.get(0).split(",");
				String[] columns2 = lines.get(1).split(",");
				String col1Unit = uF.cellContainsUnits(columns1[index]); 
				String col2Unit = uF.cellContainsUnits(columns2[index]);
				if( col1Unit == null && col2Unit == null )  {
					return null;
				} else {
					return col1Unit != null ? col1Unit : col2Unit;
				}
			} catch( ArrayIndexOutOfBoundsException a ) {
				return "SPEC_CHAR";
			} catch( IndexOutOfBoundsException i ) {
				return "TOO_FEW_LINES";
			}
		} else {
			return hUnits;
		}
	}*/
	
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