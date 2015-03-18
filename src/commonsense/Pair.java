package commonsense;

public class Pair<K, V> {
	K key;
	V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public Pair(K key) {
		this(key, null);
	}
	
	public String toString() {
		return key + ", " + value;
	}
	
	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
	
}
