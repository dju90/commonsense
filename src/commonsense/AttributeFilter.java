package commonsense;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AttributeFilter {
	private HashMap<String, String> attributeList;

	public AttributeFilter(String attributeFilePath) {
		attributeList = parseJson(attributeFilePath);

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
				String[] attributes = scan.nextLine().split(",");
				ArrayList<String> relevantColumns = new ArrayList<String>();
				for (int i = 0; i < attributes.length; i++) {
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
	
	public Integer[] relevantColumnIndexes(File f) {
		try {
			Scanner scan = new Scanner(f);
			if( scan.hasNextLine() ) {
				String[] attributes = scan.nextLine().split(",");
				ArrayList<Integer> relevantColumns = new ArrayList<Integer>();
				for (int i = 0; i < attributes.length; i++) {
					String columnCandidate = attributes[i].replaceAll("[^A-Za-z0-9 ]", "");
					for( String attributeRegex : attributeList.keySet() ) {
						if( columnCandidate.matches("\\b" + attributeRegex + "\\b") ){
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