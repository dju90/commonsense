import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.cybozu.labs.langdetect.*;

//CSE 454 
//Common Sense Data Mining Project
//Part 1: Filtering - Language Filtering. 
//Filtering all english files into a directory
public class LangDetectFilter {
    public static void main(String[] args) throws LangDetectException, IOException {
		if(args.length == 3) {
		    File main = new File(args[0]); 
		    if(main.isDirectory()) {
		       DetectorFactory.loadProfile(new File(args[1]));
		       for(String filename : main.list()) {
		    	  if(filename.contains(".csv")) {
		    		  File f = new File(filename);
		    		  Detector detector = DetectorFactory.create();
		    		  detector.append(filename);
		    		  String lang = detector.getProbabilities().toString();
		    		  if(lang.contains("en") || (lang.contains("it") || lang.contains("fr"))) {
		    			  System.out.println(true);
		    			  System.out.println(filename);
		    			  File dst = new File(args[2]);
		    			  try {
		    				    FileUtils.copyFileToDirectory(new File(args[0] + filename), dst);
		    				} catch (IOException e) {
		    				    e.printStackTrace();
		    				}
		    		  } else {
		    			  System.out.println(false);
		    		  }
		    		  System.out.println(detector.getProbabilities().toString());
		    		  
		    	  }
		       }
		    }
		}
    }
}