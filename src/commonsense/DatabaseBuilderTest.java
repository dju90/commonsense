package commonsense;

import java.util.HashMap;
import java.util.HashSet;

public class DatabaseBuilderTest {

	/**
	 * Single test to ensure that DatabaseBuilder correctly parses the attributeMap into Mongodb
	 * @param args unused
	 */
	public static void main(String[] args) {
		HashMap<String, HashMap<String, HashSet<Pair<String, Integer>>>> attMapTest = 
				new HashMap<String, HashMap<String, HashSet<Pair<String, Integer>>>>();
		addTestData(attMapTest);
		DatabaseBuilder.addToDB(attMapTest);
	}

	/**
	 * Adds in a few items into the attributeMap to add to the database
	 * @param attMapTest
	 */
	private static void addTestData(HashMap<String, HashMap<String, HashSet<Pair<String, Integer>>>> attMapTest) {
		Pair<String, Integer> coApple = new Pair<String, Integer>("stockPrice", 99);
		Pair<String, Integer> otherCo = new Pair<String, Integer>("stockPrice", 0);
		Pair<String, Integer> fruitApple = new Pair<String, Integer>("calorie", 1);
		Pair<String, Integer> sharedData = new Pair<String, Integer>("intersect", 17);
		HashSet<Pair<String, Integer>> otherCoSet = new HashSet<Pair<String, Integer>>();
		HashSet<Pair<String, Integer>> coSet = new HashSet<Pair<String, Integer>>();
		HashSet<Pair<String, Integer>> fruitSet = new HashSet<Pair<String, Integer>>();
		HashMap<String, HashSet<Pair<String, Integer>>> coData = new HashMap<String, HashSet<Pair<String, Integer>>>();
		HashMap<String, HashSet<Pair<String, Integer>>> fruitData = new HashMap<String, HashSet<Pair<String, Integer>>>();
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
