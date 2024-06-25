package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {

    private T[] array;
    private int size;
    private int next_first = 0;
    private int next_last = 1;

    public ArrayDeque() {
        size = 0;
        array = (T[]) new Object[8];
    }

    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = array[(next_first + 1 + i) % array.length];
        }
        array = newArray;
        next_first = capacity - 1;
        next_last = size;
    }

    @Override
    public void addFirst(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        array[next_first] = item;
        size++;
        if (next_first - 1 < 0){
            next_first = array.length - 1;
        } else {
            next_first = next_first - 1;
        }
    }

    @Override
    public void addLast(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        array[next_last] = item;
        size++;
        next_last = (next_last + 1) % array.length;
    }

    @Override
    public T removeFirst() {
        if (!isEmpty()) {
            next_first = (next_first + 1) % array.length;  // Update next_first before accessing
            T return_val = array[next_first];
            array[next_first] = null;
            size--;
            if ((double) size / array.length < 0.25) {
                resize(array.length / 2);
            }
            return return_val;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (!isEmpty()) {
            next_last = (next_last - 1 + array.length) % array.length;  // Update next_last before accessing
            T return_val = array[next_last];
            array[next_last] = null;
            size--;
            if ((double) size / array.length < 0.25) {
                resize(array.length / 2);
            }
            return return_val;
        }
        return null;
    }


    @Override
    public T get(int index) {
        return array[(next_first+index+1)% array.length];
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public void printDeque() {
        for(int i = 0 ; i < size; i++){
            System.out.print(array[(next_first+i+1)%array.length]);
            System.out.print(' ');
        }
        System.out.println();
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        public ArrayDequeIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            T item = array[(next_first + pos + 1) % array.length];
            pos += 1;
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

    public static void main(String[] args) {
        ArrayDeque<Integer> deq = new ArrayDeque<Integer>();
        deq.addFirst(1);
        deq.addFirst(2);
        deq.addFirst(3);
        deq.addFirst(4);
        deq.addFirst(5);
        deq.addFirst(6);
        deq.addFirst(7);
        deq.addFirst(8);
        deq.addFirst(9);
        for (Integer i : deq) {
            System.out.println(i);
        }
        deq.printDeque();
    }
}
