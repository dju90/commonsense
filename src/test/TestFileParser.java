package test;

import java.util.ArrayList;
import java.util.Scanner;

import commonsense.Pair;

public class TestFileParser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Pair<String, String>> testList = new ArrayList<Pair<String, String>>();
		Scanner fileScan  = null;
        try {
            fileScan = new Scanner(args[0]);
            String line;
            String[] lineArr;
            int count = 0;
            while (fileScan.hasNext()) {
                line = fileScan.nextLine();
                lineArr = line.split(",");
               
            }
            fileScan.close();
        } catch (Exception e) {
            System.err.println(args[0] + " failed to read");
        }
	}

}
