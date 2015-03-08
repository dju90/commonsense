package commonsense;

import java.util.HashMap;
import java.util.HashSet;

public class DatabaseBuilderTest {

	/**
	 * Single test to ensure that DatabaseBuilder correctly parses the attributeMap into Mongodb
	 * @param args unused
	 */
	public static void main(String[] args) {
		HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMapTest = 
				new HashMap<String, HashMap<String, HashSet<Pair<String, String>>>>();
		addTestData(attMapTest);
		DatabaseBuilder.addToDB(attMapTest);
	}

	/**
	 * Adds in a few items into the attributeMap to add to the database
	 * @param attMapTest
	 */
	private static void addTestData(HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMapTest) {
		Pair<String, String> coApple = new Pair<String, String>("stockPrice", "99");
		Pair<String, String> otherCo = new Pair<String, String>("stockPrice", "0");
		Pair<String, String> fruitApple = new Pair<String, String>("calorie", "1");
		Pair<String, String> sharedData = new Pair<String, String>("intersect", "17 m");
		HashSet<Pair<String, String>> otherCoSet = new HashSet<Pair<String, String>>();
		HashSet<Pair<String, String>> coSet = new HashSet<Pair<String, String>>();
		HashSet<Pair<String, String>> fruitSet = new HashSet<Pair<String, String>>();
		HashMap<String, HashSet<Pair<String, String>>> coData = new HashMap<String, HashSet<Pair<String, String>>>();
		HashMap<String, HashSet<Pair<String, String>>> fruitData = new HashMap<String, HashSet<Pair<String, String>>>();
		coSet.add(coApple); 
		coSet.add(sharedData);
		fruitSet.add(fruitApple);
		fruitSet.add(sharedData);
		otherCoSet.add(otherCo);
		coData.put("Apple", coSet);
		coData.put("OtherCo", otherCoSet);
		fruitData.put("Apple", fruitSet);
		attMapTest.put("company", coData);
		attMapTest.put("fruit", fruitData);
	}
}
