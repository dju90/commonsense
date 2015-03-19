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

	/**
	 * Will output to two files: 
	 * 		3rd argument: file w/ samples of relevant columns w/in table
	 *    4th argument: file w/ only file names and relevant column dimension/heading/unit
	 * @param args
	 */
	public static void main(String[] args) {
		if( args.length == 5) {
			String dirName = args[0];
			AttributeFilter aF = new AttributeFilter(args[1], args[2]);
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
					output(fileDir, aF, writer, fNamesWriter);
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
	 * TODO: entity column = attribute column
	 */
	private static void output(File[] fileDir, AttributeFilter aF, PrintStream writer, PrintStream fNamesWriter) 
										  		 throws FileNotFoundException {
		for( int i=0; i<fileDir.length; i++) {
			File f = fileDir[i];
			if( i % 1000 == 0 ) {
				System.out.println("At file #" + i + ": " + f.getName());
			}
			TableInfo info = aF.relevance(f);
			if( info != null && info.isValid() && info.size() > 0 ) {
				fNamesWriter.println(info);
				writer.println(info);
				// TODO: got to fill the lines with lines
				printTableSample(info, writer);
			}
		}
	}
	
	/*
	 * Prints a sample of the relevant columns of a table.
	 * Strips html special chars: &[A-Z-a-z]+;
	 */
	private static void printTableSample(TableInfo info, PrintStream writer) {
		int eI = info.getEntityIndex();
		if( eI >= 0 ) {
			for( String line : info.firstLines ) {
				String[] cols = line.split(",");
				for( int i = 0; i < cols.length; i++) {
					cols[i] = cols[i].replaceAll("&[A-Za-z]+;", "").replaceAll(" {2,}", " ");
				}
				if( eI < cols.length ) {
					writer.print(cols[eI] + ": "); //print entity name
					Integer[] rIndexes = info.getRelevantIndexes();
					for(int i = 0; i < rIndexes.length; i++) {
						int index = rIndexes[i];
						if( index >= 0 && index < cols.length ) {
							writer.print(cols[index]);					
						}
					}
					writer.println();
				}
			}
			writer.println();
		} //no entity
	}
}
