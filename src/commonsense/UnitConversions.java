package commonsense;

import java.math.BigDecimal;

public class UnitConversions {

	// Length Conversions to Meters
	public static BigDecimal InchesToMeters = new BigDecimal(0.0254);
	public static BigDecimal CentiToMeters = new BigDecimal(0.01);
	public static BigDecimal MilliToMeters = new BigDecimal(0.001);
	public static BigDecimal KiloToMeters = new BigDecimal(1000);
	public static BigDecimal FeetToMeters = new BigDecimal(0.3048);
	public static BigDecimal YardsToMeters = new BigDecimal(0.9144);
	public static BigDecimal MilesToMeters = new BigDecimal(1609.34);
	
	// Area Conversions to Square Meters
	public static BigDecimal SqKilloToSqMeters = new BigDecimal(100000);
	public static BigDecimal HectareToSqMeters = new BigDecimal(10000);
	public static BigDecimal SqMileToSqMeters = new BigDecimal(2590000);
	public static BigDecimal AcreToSqMeters = new BigDecimal(4046.86);
	public static BigDecimal SqYardToSqMeters = new BigDecimal(0.836127);
	public static BigDecimal SqFeetToSqMeters = new BigDecimal(0.092903);
	public static BigDecimal SqInchesToSqMeters = new BigDecimal(0.00064516);
	
	// Volume Conversions to Cubic Meters
	public static BigDecimal GaloToCuMeters = new BigDecimal(0.00378541);
	public static BigDecimal QuartToCuMeters = new BigDecimal(0.000946353);
	public static BigDecimal LiterToCuMeters = new BigDecimal(0.001);
	public static BigDecimal MilliLitersToCuMeters = new BigDecimal(0.000001);
	public static BigDecimal CuFeetToCuMeters = new BigDecimal(0.0283168);
	public static BigDecimal CuInchesToCuMeters = new BigDecimal(0.092903);
	
	// Mass Conversions to Kilograms
	public static BigDecimal GramsToKilos = new BigDecimal(0.001);
	public static BigDecimal MilligramsToKilos = new BigDecimal(0.000001);
	public static BigDecimal PoundsToKilos = new BigDecimal(0.453592);
	public static BigDecimal OuncesToKilos = new BigDecimal(0.0283495);
	public static BigDecimal TonsToKilos = new BigDecimal(1000);
	
	public static BigDecimal convertUnits(double val, String units) {
		// TODO Auto-generated method stub
		// HashSet<String> allUnits = parseJSON("units.txt");
		// if length
		// if weight
		// if area
		// if volume
		BigDecimal value = new BigDecimal(val);
		return value;
	}
	
	// Checks units and converts to meters
	private static BigDecimal convertLength(BigDecimal val, String units) {
		if (units.matches("in") || units.matches("inch")) {
			return val.multiply(InchesToMeters);
		} else if (units.matches("cm") || units.matches("centimeter")) {
			return val.multiply(CentiToMeters);
		} else if (units.matches("mm") || units.matches("millimeter")) {
			return val.multiply(MilliToMeters);
		} else if (units.matches("km") || units.matches("kilometer")) {
			return val.multiply(KiloToMeters);
		} else if (units.matches("foot") || units.matches("feet") || units.matches("ft")) {
			return val.multiply(FeetToMeters);
		} else if (units.matches("yard") || units.matches("yd")) {
			return val.multiply(YardsToMeters);
		} else if (units.matches("miles") || units.matches("mi")) {
			return val.multiply(MilesToMeters);
		} else {
			// assuming it is meters
			return val;
		}
	}

	// Checks units and converts to square meters
	private static BigDecimal convertArea(BigDecimal val, String units) {
		if (units.matches("sq") || units.matches("square")) {
			if (units.matches("km") || units.matches("kilo")) {
				return val.multiply(SqKilloToSqMeters);
			} else if (units.matches("mile") || units.matches("mi")) {
				return val.multiply(SqMileToSqMeters);
			} else if (units.matches("yard") || units.matches("yd")) {
				return val.multiply(SqYardToSqMeters);
			} else if (units.matches("foot") || units.matches("feet") || units.matches("ft")) {
				return val.multiply(SqFeetToSqMeters);
			} else if (units.matches("in") || units.matches("inch")) {
				return val.multiply(SqInchesToSqMeters);
			} else {
				// Assuming it is sq meters
				return val;
			}
		} else if (units.matches("acre") || units.matches("ha")) {
			if (units.matches("ha") || units.matches("hect")) {
				return val.multiply(HectareToSqMeters);
			} else {
				return val.multiply(AcreToSqMeters);
			}
		} else {
			// Assuming its sq meters
			return val;
		}
	}
	
	// Checks units and converts to cubic meters
	private static BigDecimal convertVolume(BigDecimal val, String units) {
		if (units.matches("cu") || units.matches("cubic")) {
			if (units.matches("foot") || units.matches("feet") || units.matches("ft")) {
				return val.multiply(CuFeetToCuMeters);
			} else if (units.matches("in") || units.matches("inch")) {
				return val.multiply(CuInchesToCuMeters);
			} else {
				// Assuming it is cu meters
				return val;
			}
		} else if (units.matches("ml") || units.matches("milli")) {
			return val.multiply(MilliLitersToCuMeters);
		} else if (units.matches("liter")) {
			return val.multiply(LiterToCuMeters);
		} else if (units.matches("gal")) {
			return val.multiply(GaloToCuMeters);
		} else if (units.matches("quart") || units.matches("qt")) {
			return val.multiply(QuartToCuMeters);
		} else {
			// Assuming its cu meters
			return val;
		}
	}
	
	// Checks units and converts to kilograms
	private static BigDecimal convertMass(BigDecimal val, String units) {
		if (units.matches("gram")) {
			return val.multiply(GramsToKilos);
		} else if (units.matches("mg") || units.matches("milli")) {
			return val.multiply(MilligramsToKilos);
		} else if (units.matches("lb") || units.matches("pound")) {
			return val.multiply(PoundsToKilos);
		} else if (units.matches("ounces") || units.matches("oz")) {
			return val.multiply(OuncesToKilos);
		} else if (units.matches("ton")) {
			return val.multiply(QuartToCuMeters);
		} else {
			// Assuming its kilograms
			return val;
		}
	}			
}
