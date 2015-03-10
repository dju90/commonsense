package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class AttributeFilterTest {

	public static void main(String[] args) {
		if( args.length == 2) {
			String dirName = args[0];
			AttributeFilter test = new AttributeFilter("attributes.json");
			PrintStream writer;
			try {
				writer = new PrintStream(new File(args[1]));
				File[] fileDir = new File(dirName).listFiles();
				if( fileDir == null ) {
					System.out.println("invalid directory");
					System.exit(0);
				}
				//for( File f : fileDir ) {
				for( int i=0; i<fileDir.length; i++) {
					File f = fileDir[i];
					String[] temp = test.relevantColumnHeadings(f);
					if( temp != null && temp.length > 0 ) {
						//System.out.println(f.getName() + ": " + Arrays.toString(temp));
						writer.println(f.getName() + ": " + Arrays.toString(temp));
						Scanner fileScan = new Scanner(f);
						firstTwoLines(fileScan, temp, writer);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Usage: java AttributeFilterTest directoryName");
			System.exit(0);
		}
	}
	
	private static void firstTwoLines(Scanner fScan, String[] relevants, PrintStream writer) {
		int count = 0;
		fScan.nextLine();
		if( fScan.hasNextLine() ) {
			
		}
		while( fScan.hasNextLine() && count < 2 ) {
			String line = fScan.nextLine();
			String[] cols = line.split(",");
			int entityColumn = TableCrawler.idEntityColumn(cols);
			if( entityColumn >= 0 ) {
				writer.print(cols[entityColumn].replaceAll(" {2,}", " ") + ": ");
				for(int i = 0; i < relevants.length; i++) {
					int index;
					try {
						index = Integer.parseInt(relevants[i].split(":")[0]);
					} catch( NumberFormatException n ) {
						index = -1;
					}
					if( index >= 0 ) {
						writer.print(cols[index]);					
					}
				}
				count++;
				writer.println();
			}
		}
		System.out.println();
	}

}
