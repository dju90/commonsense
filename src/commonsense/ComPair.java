package commonsense;

public class ComPair implements Comparable<ComPair> {
	String key;
	int val;

	public ComPair(String s, int n) {
		this.key = s;
		this.val = n;
	}

	public ComPair(String key) {
		this(key, 0);
	}
	
	public String toString() {
		return "{\"" + key + "\": " + val + "}";
	}
	
	public String getKey() {
		return key;
	}
	
	public int getValue() {
		return val;
	}
	
	public void setValue(int value) {
		this.val = value; 
	}
	
	public int compareTo(ComPair other) {
		return val - other.getValue();
	}
}
