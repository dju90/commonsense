package commonsense;

import java.io.FileNotFoundException;

public class TableCrawlerMain {
	private static TableCrawler crawler;
	
	public static void main(String[] args) throws FileNotFoundException {
		crawler = new TableCrawler(args);
	}

}
