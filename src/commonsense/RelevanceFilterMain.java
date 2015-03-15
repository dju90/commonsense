package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RelevanceFilterMain {

	public static void main(String[] args) {
		if( args.length == 5) {
			String dirName = args[0];
			AttributeFilter aF = new AttributeFilter(args[1], args[2]);
			UnitFilter uF = new UnitFilter(args[2]);
			PrintStream writer;
			PrintStream fNamesWriter;
			try {
				writer = new PrintStream(new File(args[3]));
				fNamesWriter = new PrintStream(new File(args[4]));
				File[] fileDir = new File(dirName).listFiles();
				if( fileDir == null ) {
					System.out.println("invalid directory");
					System.exit(0);
				} else {
					//output(fileDir, aF, writer, fNamesWriter);
					pruneUnitLessColumns(fileDir, aF, uF, writer, fNamesWriter);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage: java AttributeFilterTest directoryName "
					+ "attributeJsonFile unitJsonFile outputFileName");
			System.exit(0);
		}
	}

	/*
	 * replace &[A-Z-a-z];
	 */
	private static void output(File[] fileDir, AttributeFilter aF, PrintStream writer, PrintStream fNamesWriter) 
										  		 throws FileNotFoundException {
		for( int i=0; i<fileDir.length; i++) {
			File f = fileDir[i];
			ArrayList<String> relevants = aF.relevantColumnHeadings(f);
			if( relevants != null && relevants.size() > 0 ) {
				String fName = f.getName();
				writer.println(fName + ": " + relevants);
				fNamesWriter.println(fName + ": " + relevants);
				ArrayList<String> lines = new ArrayList<String>();
				Scanner fScan = new Scanner(f);
				int counter = 0;
				while( fScan.hasNextLine() && counter < 5) {
					lines.add(fScan.nextLine());
					counter++;
				}
				// TODO: got to fill the lines with lines
				printLines(lines, relevants, writer);
			}
		}
	}
	
	/*
	 * entity column = attribute column
	 * replace &[A-Z-a-z];
	 */
	private static void pruneUnitLessColumns(File[] fileDir, AttributeFilter aF, 
										   UnitFilter uF, PrintStream writer, PrintStream fNamesWriter) throws FileNotFoundException {
		for( int i=0; i<fileDir.length; i++) {
			boolean specialChar = false;
			File f = fileDir[i];
			ArrayList<String> relevants = aF.relevantColumnHeadings(f);
			if( relevants != null && relevants.size() > 0 ) {
				// see if columns match
				Scanner fileScan = new Scanner(f);
				ArrayList<String> lines = new ArrayList<String>();
				int counter = 0;
				fileScan.nextLine();
				while( fileScan.hasNextLine() && counter < 2 ) { //first two lines to print
					lines.add(fileScan.nextLine());
					counter++;
				}
				int j = 0;
				while( j < relevants.size() ) {
					String unit = "";
					String[] col = relevants.get(j).split(":");
					int index;
					try {
						index = Integer.parseInt(col[0]);
					} catch( NumberFormatException n ) {
						index = -1;
					}
					String[] identifiers = col[1].split(";");
					String dimName = identifiers[0];
					String colName = identifiers[1];
					if( !uF.headerContainsUnits(colName) ) {	// the attribute does not contain units
						// the column does
						try {//if( lines.size() > 1 ) {
							String[] columns1 = lines.get(0).split(",");
							String[] columns2 = lines.get(1).split(",");
							
							if( index > -1 && !uF.cellContainsUnits(columns1[index]) && 
									!uF.cellContainsUnits(columns2[index]) )  {
								relevants.remove(j);
							} else {
								j++;
							}
						} catch( IndexOutOfBoundsException e) {
							specialChar = false;
							j++;
						}
					} else {
						j++;
					}
				}
				if( relevants.size() > 0 && lines.size() > 1 && !specialChar) {
					String fName = f.getName();
					writer.println(fName + ": " + relevants);
					fNamesWriter.println(fName + ": " + relevants);
					printLines(lines, relevants, writer);
				}
				fileScan.close();
			}
		}
	}
	
	private static void printLines(ArrayList<String> lines, ArrayList<String> relevants, PrintStream writer) {
		for( String line : lines ) {
			String[] cols = line.split(",");
			int entityColumn = TableCrawler.idEntityColumn(cols);
			if( entityColumn >= 0 ) {
				writer.print(cols[entityColumn].replaceAll(" {2,}", " ") + ": ");
				for(int i = 0; i < relevants.size(); i++) {
					int index;
					try {
						index = Integer.parseInt(relevants.get(i).split(":")[0]);
					} catch( NumberFormatException n ) {
						index = -1;
					}
					if( index >= 0 && index < cols.length ) {
						writer.print(cols[index]);					
					}
				}
				writer.println();
			}
		}
		writer.println();
	}
}
