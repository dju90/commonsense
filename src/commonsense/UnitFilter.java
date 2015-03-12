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
		unitList = new HashSet<String>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(
					filePath));

			// ultra-specific to our unitList file :/
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
								unitList.add((String) innerArray.get(j));
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
		return unitList;
	}
}


