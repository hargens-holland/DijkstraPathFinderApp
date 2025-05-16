// == CS400 Fall 2024 File Header Information ==
// Name: Holland Hargens
// Email: hhargens@wisc.edu
// Group: <your group's id: P2.1814>
// Lecturer: Gary Dahl
// Notes to Grader:
import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.util.List;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    protected class Pair {

        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }

    }
    protected LinkedList<Pair>[] table;
    private int size;
    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity){
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        size = 0;
    }
    public HashtableMap() {
        this(64);
    }
    /**
     * Adds a new key,value pair/mapping to this collection.
     * @param key the key of the key,value pair
     * @param value the value that key maps to
     * @throws IllegalArgumentException if key already maps to a value
     * @throws NullPointerException if key is null
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        
        if ((double) (size + 1) / table.length >= 0.8) {
            resize();
        }

        int index = Math.abs(key.hashCode()) % table.length;
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                throw new IllegalArgumentException("Key already exists");
            }
        }

        table[index].add(new Pair(key, value));
        size++;
    }
    /**
     * Resizes the array to double the size when it becomes too full
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        LinkedList<Pair>[] oldTable = table;
        table = (LinkedList<Pair>[]) new LinkedList[table.length * 2];

        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }

        size = 0;

        for (LinkedList<Pair> bucket : oldTable) {
            for (Pair pair : bucket) {
                put(pair.key, pair.value);
            }
        }
    }


    /**
     * Checks whether a key maps to a value in this collection.
     * @param key the key to check
     * @return true if the key maps to a value, and false is the
     *         key doesn't map to a value
     */
    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) {
            return false;
        }

        int index = Math.abs(key.hashCode()) % table.length;
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this
     *         collection
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        int index = Math.abs(key.hashCode()) % table.length;
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }
        throw new NoSuchElementException("Key not found");
    }

    /**
     * Remove the mapping for a key from this collection.
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this
     *         collection
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        int index = Math.abs(key.hashCode()) % table.length;
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                ValueType value = pair.value;
                table[index].remove(pair);
                size--;
                return value;
            }
        }
        throw new NoSuchElementException("Key not found");
    }

    /**
     * Removes all key,value pairs from this collection.
     */
    @Override
    public void clear() {
        for (LinkedList<Pair> bucket : table) {
            bucket.clear();
        }
        size = 0;
    }

    /**
     * Retrieves the number of keys stored in this collection.
     * @return the number of keys stored in this collection
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Retrieves this collection's capacity.
     * @return the size of te underlying array for this collection
     */
    @Override
    public int getCapacity() {
        return table.length;
    }
    /**
     * Retrieves this collection's keys.
     * @return a list of keys in the underlying array for this collection
     */
    public List<KeyType> getKeys() {
        List<KeyType> keys = new LinkedList<>();
        for (LinkedList<Pair> bucket : table) {
            for (Pair pair : bucket) {
                keys.add(pair.key);
            }
        }
        return keys;
    }

}


