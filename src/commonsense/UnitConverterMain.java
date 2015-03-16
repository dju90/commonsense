package commonsense;

import java.math.BigDecimal;
import java.util.Scanner;

public class UnitConverterMain {

	public static void main(String[] args) {
		if( args.length == 1) {
			UnitConverter converter = new UnitConverter(args[0]);
			Scanner dm = new Scanner(System.in);
			while( true ) {
				System.out.print("Enter dimension(1D,2D,3D,weight) <comma> number <comma> units (q to exit): ");
				String inputs = dm.next();
				if( inputs.equalsIgnoreCase("q") ) {
					dm.close();
					break;
				}
				String[] input = inputs.split(",");
				try {
					String dim = input[0];
					BigDecimal num = new BigDecimal(input[1]);
					String unit = input[2];
					double result = converter.convert(dim.toUpperCase(), num, unit);
					if( result == Double.POSITIVE_INFINITY ) {
						System.out.println();
						System.out.println("Not a valid unit");
					} else if( result == Double.NEGATIVE_INFINITY ){
						System.out.println();
						System.out.println("Not a valid dimension");
					} else {
						System.out.println(num + " " + unit + " = " + result);
					}
				} catch( NumberFormatException n ) {
					System.out.println("You didn't enter a dimension or didn't enter a valid number.");
				} catch( IndexOutOfBoundsException i ) {
					System.out.println("You didn't enter units or you didn't put a space.");
				}
			}
		} else {
			System.out.println("args: unitConversion.json");
			System.exit(1);
		}

	}

}
