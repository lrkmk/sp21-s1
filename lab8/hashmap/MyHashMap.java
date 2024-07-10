package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node<K, V> {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node<K, V>>[] buckets;
    private int initialSize = 16;
    private double loadFactor = 0.75;
    private int size = 0;
    private HashSet<K> set = new HashSet<>();
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node<K, V> createNode(K key, V value) {
        return new Node<K, V>(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node<K, V>> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node<K, V>>[] createTable(int tableSize) {
        Collection<Node<K, V>>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(16);
        size = 0;
        initialSize = 16;
    }

    @Override
    public boolean containsKey(K key) {
        int pos = Math.floorMod(key.hashCode(), initialSize);
        for (Node<K, V> item: buckets[pos]) {
            if (key.equals(item.key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int pos = Math.floorMod(key.hashCode(), initialSize);
        for (Node<K, V> item: buckets[pos]) {
            if (key.equals(item.key)) {
                return item.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (++size / (double) initialSize > loadFactor) {
            resize();
        }
        int pos = Math.floorMod(key.hashCode(), initialSize);
        if (containsKey(key)) {
            for (Node<K, V> item: buckets[pos]) {
                if (key.equals(item.key)) {
                    item.value = value;
                }
            }
            size--;
        } else {
            Node<K, V> n = createNode(key, value);
            buckets[pos].add(n);
        }

        set.add(key);
    }

    private void resize() {
        Collection<Node<K, V>>[] newBuckets = createTable(initialSize * 2);
        Iterator<K> iter = iterator();
        while(iter.hasNext()) {
            K key = iter.next();
            V value = get(key);
            Node<K, V> node = createNode(key, value);
            int pos = Math.floorMod(key.hashCode(), initialSize * 2);
            newBuckets[pos].add(node);
        }
        buckets = newBuckets;
        initialSize *= 2;
    }

    @Override
    public Set<K> keySet() {
        return set;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIter();
    }

    private class HashMapIter implements Iterator<K> {
        private int bucketIndex;
        private Iterator<Node<K, V>> bucketIterator;

        HashMapIter() {
            bucketIndex = 0;
            bucketIterator = buckets[bucketIndex].iterator();
            moveToNextBucket();
        }

        private void moveToNextBucket() {
            while (!bucketIterator.hasNext() && bucketIndex < buckets.length - 1) {
                bucketIndex++;
                bucketIterator = buckets[bucketIndex].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return bucketIterator.hasNext();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                return null;
            }
            Node<K, V> nextEntry = bucketIterator.next();
            moveToNextBucket();
            return nextEntry.key;
        }
    }


}
