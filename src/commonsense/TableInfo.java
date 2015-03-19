package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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

	/**
	 * Constructs an empty TableInfo object with the given file's handler
	 * 
	 * @param f
	 */
	public TableInfo(File f) {
		this(f, null);
	}

	/**
	 * Constructs a new TableInfo object based in the info string info =
	 * fileName.csv:entityCol|index;dimension;columnName;unit,index;dimension;columnName;unit,...
	 * 
	 * @param f
	 * @param info
	 */
	public TableInfo(File f, String info) {
		if( f != null ) {
			file = f;
			valid = true;
			relevantIndexes = new ArrayList<Integer>();
			colDims = new ArrayList<String>();
			colNames = new ArrayList<String>();
			colUnits = new ArrayList<String>();
			firstLines = new ArrayList<String>();
			if (info == null) {
				entityIndex = -1;
				size = 0;
			} else {
				try {
					String[] prelim = info.split("\\|");
					String[] meta = prelim[0].split(":");
					entityIndex = Integer.parseInt(meta[1]);
					String[] cols = prelim[1].split(",");
					for (int i = 0; i < cols.length; i++) {
						String[] data = cols[i].split(";");
						relevantIndexes.add(Integer.parseInt(data[0]));
						colDims.add(data[1]);
						colNames.add(data[2]);
						colUnits.add(data[3]);
						size++;
					}
				} catch (Exception e) {
					valid = false;
				}
			}
		} else {
			valid = false;
		}
	}
	
//	public TableInfo(String dir, String info) {
//		this(new File(dir + "/" + info.split(":")[0]), info);
//	}

	/**
	 * Populates with n lines from the file and determines entity column
	 * If the entity column cannot be found, returns null
	 * 
	 * @param n			up to n sample lines will populate the current object 
	 * @return
	 */
	public String[] ready(int n) {
		try {
			Scanner scan = new Scanner(file);
			if (scan.hasNextLine()) {
				String[] colHeadings = scan.nextLine().split(",");// top line
				for (int i = 0; i < n && scan.hasNextLine(); i++) {
					firstLines.add(scan.nextLine());
				}
				scan.close();
				if( setEntityCol() ) {
					return colHeadings;
				} else {
					return null;
				}
			} else {
				scan.close();
				return null;
			}
		} catch (FileNotFoundException f) {
			return null;
		}
	}
	
	public File getFile() { 
		return file;
	}
	
	/*
	 * Determines the entity column from the sample lines. Requires fillSample()
	 * to be called first...i know this is bad :(
	 * 
	 * @return true if successful; false if sampleLines was not populated first
	 */
	private boolean setEntityCol() {
		try {
			entityIndex = idEntityColumn(firstLines.get(1).replaceAll(" {2,}", " ")
					.split(","));
			return true;
		} catch (IndexOutOfBoundsException i) {
			return false;
		}
	}

	/**
	 * Returns number of relevant columns are in the table.
	 */
	public int size() {
		return size;
	}

	/**
	 * Renders the TableInfo object invalid.
	 */
	public void nullify() {
		relevantIndexes = null;
		colDims = null;
		colNames = null;
		colUnits = null;
		firstLines = null;
		valid = false;
	}

	/**
	 * Returns whether the TableInfo object correlates to a well-formed file with
	 * an entity column
	 * 
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Returns the sample line at index i
	 * 
	 * @param i
	 *          index of the desired line
	 * @return
	 */
	public String getLine(int i) {
		return firstLines.get(i);
	}

	/**
	 * Adds a relevant column entry to the TableInfo object
	 * 
	 * @param i
	 *          the column's index
	 * @param dim
	 *          the dimension of the column's unit
	 * @param colName
	 *          the name of the column
	 * @param unit
	 *          the units of the column
	 */
	public void addRelevantColumn(int i, String dim, String colName, String unit) {
		relevantIndexes.add(i);
		colDims.add(dim);
		colNames.add(colName);
		colUnits.add(unit);
		size++;
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
		if (valid && size > 0) {
			rep += file.getName() + ":" + entityIndex + "|" + relevantIndexes.get(0)
					+ ";" + colDims.get(0) + ";" + colNames.get(0) + ";"
					+ colUnits.get(0) + "";
			for (int i = 1; i < size; i++) {
				rep += "," + relevantIndexes.get(0) + ";" + colDims.get(0) + ";"
						+ colNames.get(0) + ";" + colUnits.get(0);
			}
			return rep;
		} else {
			return "";
		}
	}
}
