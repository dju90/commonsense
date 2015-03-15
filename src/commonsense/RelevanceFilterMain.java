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
		if( args.length == 4) {
			String dirName = args[0];
			AttributeFilter aF = new AttributeFilter(args[1], true);
			UnitFilter uF = new UnitFilter(args[2]);
			PrintStream writer;
			try {
				writer = new PrintStream(new File(args[3]));
				File[] fileDir = new File(dirName).listFiles();
				if( fileDir == null ) {
					System.out.println("invalid directory");
					System.exit(0);
				} else {
					pruneUnitLessColumns(fileDir, aF, uF, writer);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage: java AttributeFilterTest directoryName "
					+ "attributeJsonFile unitJsonFile outputFileName");
			System.exit(0);
		}
	}
	
	private static void pruneUnitLessColumns(File[] fileDir, AttributeFilter aF, 
										   UnitFilter uF, PrintStream writer) throws FileNotFoundException {
		for( int i=0; i<fileDir.length; i++) {
			boolean enoughLines = true;
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
					if( !uF.containsUnits(colName) ) {	// the attribute does not contain units
						// the column does
						if( lines.size() > 0 ) {
							//System.out.println("lines = " + lines.size());
							String[] columns = lines.get(0).split(",");
//							System.out.print("[");
//							for( int k = 0; k < columns.length; k++) {
//								System.out.print(columns[k].replaceAll("[^A-Za-z0-9 ]", "") + " ");
//							}
//							System.out.println("]");
							if( !(index > -1 && index < columns.length && uF.containsUnits(columns[index])) ) {
								relevants.remove(j);
							} else {
								j++;
							}
						} else {
							//enoughLines = false;
							j++;
						}
					} else {
						j++;
					}
				}
				System.out.println(enoughLines);
				if( relevants.size() > 0 && enoughLines ) {
					System.out.println("relevants size = " + relevants.size());
					writer.println(f.getName() + ": " + relevants);	
					printLines(lines, relevants, writer);
				}
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
