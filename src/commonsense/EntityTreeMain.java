package commonsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class EntityTreeMain {

	public static void main(String[] args) {
		EntityTree tree = new EntityTree(args[0]);
	//	try {
			//PrintStream testOut = new PrintStream(new File(args[1]));
			//testOut.println(tree);
			DatabaseBuilder.addToDB(tree);
	//	} //catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
	//	}
	}

}
