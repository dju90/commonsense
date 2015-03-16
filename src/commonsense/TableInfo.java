package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TableInfo {
	private File file;
	private int entityIndex;
	private ArrayList<Integer> relevantIndexes;
	private ArrayList<String> colDims;
	private ArrayList<String> colNames;
	private ArrayList<String> colUnits;
	protected ArrayList<String> firstLines;
	private boolean valid;
	private int size;
	
	public TableInfo(File f) {
		file = f;
		entityIndex = -1;
		valid = true;
		relevantIndexes = new ArrayList<Integer>();
		colDims         = new ArrayList<String>(); 
		colNames        = new ArrayList<String>(); 
		colUnits        = new ArrayList<String>();
		firstLines      = new ArrayList<String>();
	}
	
	public String[] fillSample(int sampleLines) {
		try {
			Scanner scan = new Scanner(file);
			if( scan.hasNextLine() ) {
				String[] colHeadings = scan.nextLine().split(",");// top line
				for( int i = 0; i < sampleLines && scan.hasNextLine(); i++ ) { //first five lines to print
					firstLines.add(scan.nextLine());
				}
				scan.close();
				return colHeadings;
			} else {
				scan.close();
				return null;
			}
		} catch( FileNotFoundException f ) {
			return null;
		}
	}
	public int size() {
		return size;
	}
	
	public void nullify() {
		relevantIndexes = null;
		colDims = null;
		colNames = null;
		colUnits = null;
		firstLines = null;
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public String getLine(int i) {
		return firstLines.get(i);
	}
	
	public void addRelevantColumn(int i, String dim, String colName, String unit) {
		relevantIndexes.add(i);
		colDims.add(dim);
		colNames.add(colName);
		colUnits.add(unit);
		size++;
	}
	
	public boolean setEntityCol() {
		try {
			entityIndex = idEntityColumn(firstLines.get(1).replaceAll(" {2,}", " ").split(","));
			return true;
		} catch( IndexOutOfBoundsException i) {
			return false;
		}
	}
	
	/*
	 * Finds the first column with useful (alphabetic) entity names
	 * 
	 * @return The column index of the first alphabetic entry
	 */
	public static int idEntityColumn(String[] line) {
		int i = 0;
		while (i < line.length) {
			// !contains only non-alphabetic chars
			if (!line[i].matches("[^a-zA-Z]+")) { 
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public int getEntityIndex() {
		return entityIndex;
	}
	
	public Integer[] getRelevantIndexes() {
		return relevantIndexes.toArray(new Integer[relevantIndexes.size()]);
	}
	
	public String[] getColDims() {
		return colDims.toArray(new String[colDims.size()]);
	}
	
	public String[] getColNames() {
		return colNames.toArray(new String[colNames.size()]);
	}
	
	public String[] getColUnits() {
		return colUnits.toArray(new String[colUnits.size()]);
	}
	
	public String toString() {
		String rep = "";
		if( valid && size > 0 ) {
			rep += file.getName() + ": {";
			rep += relevantIndexes.get(0) + ": "
					+" ["+colDims.get(0)+";"
					+colNames.get(0)+";"
					+colUnits.get(0)+"]";
			for( int i = 1; i < size; i++ ) {
				rep += ", " + relevantIndexes.get(0) + ": "
								+ "[" + colDims.get(0) + ";" 
								+ colNames.get(0) + ";"
								+ colUnits.get(0) + "]";
			}
			rep+="}";
			return rep;
		} else {
			return "";
		}
	}
}


