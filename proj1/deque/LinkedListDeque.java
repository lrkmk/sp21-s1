package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>{

    private Node sentinel;
    private int size;

    class Node {
        public T _item;
        public Node _prev;
        public Node _next;

        public Node(T item){
            this._item = item;
        }
    }

    public LinkedListDeque() {
        sentinel = new Node(null);
        sentinel._prev = sentinel;
        sentinel._next = sentinel;
        size = 0;
    }

    public LinkedListDeque(T item) {
        sentinel = new Node(null);
        Node new_node = new Node(item);
        sentinel._next = new_node;
        sentinel._prev = new_node;
        new_node._next = sentinel;
        new_node._prev = sentinel;
        size = 1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void addFirst(T item) {
        Node new_node = new Node(item);
        new_node._next = sentinel._next;
        sentinel._next._prev = new_node;
        sentinel._next = new_node;
        sentinel._next._prev = sentinel;
        size++;
    }

    @Override
    public T removeFirst() {
        T return_val = sentinel._next._item;
        if (!isEmpty()) {
            sentinel._next._next._prev = sentinel;
            sentinel._next = sentinel._next._next;
            size--;
        }
        return return_val;
    }

    @Override
    public void addLast(T item) {
        Node new_node = new Node(item);
        sentinel._prev._next = new_node;
        new_node._prev = sentinel._prev;
        sentinel._prev = new_node;
        new_node._next = sentinel;
        size++;
    }

    @Override
    public T removeLast() {
        T return_val = sentinel._prev._item;
        if (!isEmpty()) {
            sentinel._prev._prev._next = sentinel;
            sentinel._prev = sentinel._prev._prev;
            size--;
        }
        return return_val;
    }

    @Override
    public void printDeque() {
        Node curr = sentinel._next;
        while(curr != sentinel) {
            System.out.print(curr._item);
            System.out.print(' ');
            curr = curr._next;
        }
        System.out.println();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        Node curr = sentinel;
        for(int i = 0; i < index + 1; i++) {
            curr = curr._next;
        }
        return curr._item;
    }

    public T getRecursive(int index) {
        return getRecursiveNode(index)._item;
    }

    private Node getRecursiveNode(int index) {
        if (index == 0) {
            return sentinel._next;
        } else {
            return getRecursiveNode(index - 1)._next;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node pos;

        public LinkedListDequeIterator() {
            pos = sentinel;
        }

        @Override
        public boolean hasNext() {
            return pos._next != sentinel;
        }

        @Override
        public T next() {
            T item = pos._item;
            pos = pos._next;
            return item;
        }
    }

    @Override
    public boolean equals(Object o) {
        for (T item: this) {
            if (!item.equals(o)) {
                return false;
            }
        }
        return true;
    }
}
