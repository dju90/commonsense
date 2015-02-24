package commonsense;

import java.util.HashMap;
import java.util.HashSet;

public class DatabaseBuilderTest {

	public static void main(String[] args) {
		HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMapTest = 
				new HashMap<String, HashMap<String, HashSet<Pair<String, String>>>>();
		addTestData(attMapTest);
		DatabaseBuilder.addToDB(attMapTest);
	}

	private static void addTestData(
			HashMap<String, HashMap<String, HashSet<Pair<String, String>>>> attMapTest) {
		Pair<String, String> coApple = new Pair<String, String>("stockPrice", "high");
		Pair<String, String> otherCo = new Pair<String, String>("something", "unused");
		Pair<String, String> fruitApple = new Pair<String, String>("calorie", "some");
		Pair<String, String> sharedData = new Pair<String, String>("intersect", "shared");
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
