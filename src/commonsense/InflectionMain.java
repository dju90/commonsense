package commonsense;

public class InflectionMain {
	public static void main(String[] args) {
		System.out.println("calves => " + Inflection.singularize("calves") + " " + Inflection.isUncountable("calves"));
		System.out.println("steer calves => " + Inflection.singularize("steer calves") + " " + Inflection.isUncountable("steer calves"));
		System.out.println("shelves wood => " + Inflection.singularize("wood shelves") + " " + Inflection.isUncountable("wood shelves"));
		System.out.println("blues => " + Inflection.singularize("blues") + " " + Inflection.isUncountable("blues"));
		System.out.println("public inquiries => " + Inflection.singularize("public inquiries") + " " + Inflection.isUncountable("public inquiries"));
		
		System.out.println("&nbsp; => " + Inflection.isUncountable("&nbsp"));
		System.out.println("4x4 => " + Inflection.isUncountable("4x4"));
		System.out.println("stan => " + Inflection.isUncountable("stan"));
		System.out.println("sandy => " + Inflection.isUncountable("sandy"));
		System.out.println("elena => " + Inflection.isUncountable("elena"));
		System.out.println("forward => " + Inflection.isUncountable("forward"));


	}
}
