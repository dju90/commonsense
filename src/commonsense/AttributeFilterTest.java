package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class AttributeFilterTest {

	public static void main(String[] args) {
		String dirName = args[0];
		AttributeFilter test = new AttributeFilter("attributes.json");
		PrintWriter writer;
		try {
			writer = new PrintWriter("csvFiles_w_relevantColumns.txt", "UTF-8");
			File[] fileDir = new File(dirName).listFiles();
			for( File f : fileDir ) {
				writer.println(test.relevance(f));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
