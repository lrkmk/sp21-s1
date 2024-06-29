package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode rootNode;
    int size = 0;

    private class BSTNode {
        K _key;
        V _val;
        BSTNode _left;
        BSTNode _right;


        public BSTNode(K key, V val) {
            _left = null;
            _right = null;
            _key = key;
            _val = val;
        }
    }

    @Override
    public void clear() {
        rootNode = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containHelper(key, rootNode) != null;
    }

    private BSTNode containHelper(K key, BSTNode root) {
        if (root == null) {
            return null;
        }
        if (root._key.compareTo(key) == 0) {
            return root;
        } else if (root._key.compareTo(key) > 0) {
            return containHelper(key, root._left);
        } else if (root._key.compareTo(key) < 0) {
            return containHelper(key, root._right);
        }
        return null;
    }
    @Override
    public V get(K key) {
        return getHelper(key, rootNode);
    }

    private V getHelper(K key, BSTNode root) {
        if (root == null) {
            return null;
        }
        if (root._key.compareTo(key) == 0) {
            return root._val;
        } else if (root._key.compareTo(key) > 0) {
            return getHelper(key, root._left);
        } else if (root._key.compareTo(key) < 0) {
            return getHelper(key, root._right);
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public void put(K key, V value) {
        if (rootNode == null) {
            rootNode = new BSTNode(key, value);
        } else {
            putHelper(key, value, rootNode);
        }
        size++;
    }

    private BSTNode putHelper(K key, V value, BSTNode root) {
        if (root == null) {
            return new BSTNode(key, value);
        }
        if (root._key.compareTo(key) > 0) {
            root._left = putHelper(key, value, root._left);
        } else if (root._key.compareTo(key) < 0) {
            root._right = putHelper(key, value, root._right);
        }
        return root;
    }


    public void printInOrder() {
        printHelper(rootNode);
    }

    private void printHelper(BSTNode root) {
        if(root == null) {
            return;
        }
        printHelper(root._left);
        System.out.print(root._val);
        printHelper(root._right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            b.put("hi" + i, 1 + i);
        }
        System.out.println(b.size());
        b.printInOrder();
    }

}
