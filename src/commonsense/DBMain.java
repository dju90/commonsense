package commonsense;

public class DBMain {

	public static void main(String[] args) {
		EntityTree tree = new EntityTree(args[0]);
			DatabaseBuilder.addToDB(tree);

	}

}