package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class AttributeFilterTest {

	public static void main(String[] args) {
		if( args.length == 1) {
			String dirName = args[0];
			AttributeFilter test = new AttributeFilter("attributes.json");
			PrintWriter writer;
			try {
				writer = new PrintWriter("csvFiles_w_RelevantColumns.txt");
				File[] fileDir = new File(dirName).listFiles();
				if( fileDir == null ) {
					System.out.println("invalid directory");
					System.exit(0);
				}
				for( File f : fileDir ) {
					Integer[] temp = test.relevance(f);
					if( temp != null && temp.length > 0 ) {
						writer.println(f.getName() + ", " + Arrays.toString(temp));
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

}
