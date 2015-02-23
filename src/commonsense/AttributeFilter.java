package commonsense;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AttributeFilter {
	private HashSet<String> attributeList;

	public AttributeFilter(String attributeFilePath) {
		attributeList = parseJson(attributeFilePath);
	}

	private HashSet<String> parseJson(String filePath) {
		attributeList = new HashSet<String>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
					filePath));

			// ultra-specific to our attributeList file :/
			jsonObject = (JSONObject) ((JSONObject) jsonObject.get("attribute"))
					.get("size");
			Set<?> dimensions = jsonObject.keySet();
			for (Object dim : dimensions) {
				JSONArray subtypes = (JSONArray) jsonObject.get(dim);
				if (subtypes != null) {
					for (int i = 0; i < subtypes.size(); i++) {
						JSONArray innerArray = (JSONArray) subtypes.get(i);
						if (innerArray != null) {
							for (int j = 0; j < innerArray.size(); j++) {
								attributeList.add((String) innerArray.get(j));
							}
						}
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
		return attributeList;
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
	public Integer[] relevance(File f) {
		try {
			Scanner scan = new Scanner(f);
			if( scan.hasNextLine() ) {
				String[] attributes = scan.nextLine().split(",");
				ArrayList<Integer> relevantColumns = new ArrayList<Integer>();
				for (int i = 0; i < attributes.length; i++) {
					String attribute = attributes[i].replaceAll("[^A-Za-z0-9 ]", "");
					if (attributeList.contains(attribute)) {
						relevantColumns.add(i);
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