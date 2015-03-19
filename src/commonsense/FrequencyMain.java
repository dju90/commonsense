package commonsense;

public class FrequencyMain {

	public static void main(String[] args) {
		int[] trials = {1,2,3,5,9,10,50,99,100,150,500,999,1000,5000,9999,10000,99999,100000};
		for( int i = 0; i < trials.length; i++) {
			int freq = (int) Math.round(Math.pow(3.7, Math.log10(trials[i])));
			int adjFreq = freq - (int) Math.round(Math.log10(freq)+1);
			adjFreq = adjFreq == 0 ? 1 : adjFreq;
			System.out.println("For a table of size " + trials[i] + ", " + trials[i]/adjFreq + " queries are made using eqn A.");
		}
	}

}
