package commonsense;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

public class DatabaseBuilderTest {

	/**
	 * Single test to ensure that DatabaseBuilder correctly parses the attributeMap into Mongodb
	 * @param args unused
	 */
	public static void main(String[] args) {
		HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> attMapTest = 
				new HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>>();
		addTestData(attMapTest);
	//	DatabaseBuilder.addToDB(attMapTest);
	}

	/**
	 * Adds in a few items into the attributeMap to add to the database
	 * @param attMapTest
	 */
	private static void addTestData(HashMap<String, HashMap<String, HashSet<Pair<String, BigDecimal>>>> attMapTest) {
		try {
			Pair<String, BigDecimal> coApple = new Pair<String, BigDecimal>("stockPrice", new BigDecimal("99"));
			Pair<String, BigDecimal> otherCo = new Pair<String, BigDecimal>("stockPrice", new BigDecimal("0"));
			Pair<String, BigDecimal> fruitApple = new Pair<String, BigDecimal>("calorie", new BigDecimal("1"));
			Pair<String, BigDecimal> sharedData = new Pair<String, BigDecimal>("intersect", new BigDecimal("17"));
			HashSet<Pair<String, BigDecimal>> otherCoSet = new HashSet<Pair<String, BigDecimal>>();
			HashSet<Pair<String, BigDecimal>> coSet = new HashSet<Pair<String, BigDecimal>>();
			HashSet<Pair<String, BigDecimal>> fruitSet = new HashSet<Pair<String, BigDecimal>>();
			HashMap<String, HashSet<Pair<String, BigDecimal>>> coData = new HashMap<String, HashSet<Pair<String, BigDecimal>>>();
			HashMap<String, HashSet<Pair<String, BigDecimal>>> fruitData = new HashMap<String, HashSet<Pair<String, BigDecimal>>>();
			coSet.add(coApple); 
			coSet.add(sharedData);
			fruitSet.add(fruitApple);
			fruitSet.add(sharedData);
			otherCoSet.add(otherCo);
			coData.put("AppleCo", coSet);
			coData.put("OtherCo", otherCoSet);
			fruitData.put("Apple", fruitSet);
			attMapTest.put("company", coData);
			attMapTest.put("fruit", fruitData);
		} catch (NumberFormatException e) {
			System.out.println("Error with DatabaseBuilderTest");
		}
	}
}
