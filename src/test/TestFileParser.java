package test;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import commonsense.Pair;
import commonsense.Query;

public class TestFileParser {
	// Pair of comparisons to correct answer.
	private static Map<Pair<String, String>, String> testMap;
	public static void main(String[] args) {
		testMap = new HashMap<Pair<String, String>, String>();
		Scanner fileScan  = null;
        try {
            fileScan = new Scanner(new File(args[0]));
            String line;
            String[] lineArr;
            int count = 0;
            while (fileScan.hasNext()) {
                line = fileScan.nextLine();
                lineArr = line.split(",");
                if (count > 0) {
                	buildTestMap(lineArr);
                }
                count++;
            }
            fileScan.close();
            testSystem();
        } catch (Exception e) {
            System.err.println(args[0] + " failed to read");
        }
	}
	
	private static void buildTestMap(String[] lineArr) {
		String comparison;
        for (int i = 1; i < lineArr.length; i++) {
        	comparison = lineArr[i];
        	if (comparison.contains("<")) {
        		String[] objects = comparison.split("<");
        		Pair<String, String> testVal =  new Pair<String,String>(objects[0], objects[1]);
        		testMap.put(testVal, objects[1]);
        	} else if (comparison.contains(">")) {
        		String[] objects = comparison.split(">");
        		Pair<String, String> testVal =  new Pair<String,String>(objects[0], objects[1]);
        		testMap.put(testVal, objects[0]);
        	} else {
        		// comparison.matches("==");
        		String[] objects = comparison.split("==");
        		Pair<String, String> testVal =  new Pair<String,String>(objects[0], objects[1]);
        		testMap.put(testVal, "equal");
        	}
        }
	}
	
	private static void testSystem() {
		Map<Pair<String, String>, String> actualMap = new HashMap<Pair<String, String>, String>();
		for (Pair<String,String> comparisons: testMap.keySet()) {
			String arg1 = comparisons.getKey();
			String arg2 = comparisons.getValue();
			String result = "IDK";
			// Use arg 1 and arg 2 to get result
			String[] args = {arg1, arg2};
			try {
				result = Query.query(args);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			actualMap.put(comparisons, result);
		}
		
		double actualValue = checkResults(actualMap);
		//double naiveValue = naiveResults();
		System.out.println("actual: " + actualValue);
		//System.out.println("naive: " + naiveValue);
	}

	private static double naiveResults() {
		int counter = 0;
		int total = 0;
		for (Map.Entry<Pair<String, String>, String> comparisonEntry :  testMap.entrySet() ) {
			String actual = comparisonEntry.getKey().getKey();
			String expected = comparisonEntry.getValue();
			if (actual.equals(expected)) {
				counter++;
			}
			total++;
		}
		return counter/(double)total;
	}

	private static double checkResults(Map<Pair<String, String>, String> actualMap) {
		int counter = 0;
		int total = 0;
		for (Map.Entry<Pair<String, String>, String> comparisonEntry :  testMap.entrySet() ) {
			String actual = actualMap.get(comparisonEntry.getKey());
			String expected = comparisonEntry.getValue();
			if (actual.equals(expected)) {
				counter++;
			}
			total++;
		}
		System.out.println(counter);
		return counter/(double) total;
	}
}
