package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> {

    private T[] array;
    private int size;
    private int next_first = 0;
    private int next_last = 1;
    private Comparator<T> comp;

    public MaxArrayDeque() {
        size = 0;
        array = (T[]) new Object[8];
    }

    public MaxArrayDeque(Comparator<T> c) {
        size = 0;
        array = (T[]) new Object[8];
        comp = c;
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

    public void addLast(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        array[next_last] = item;
        size++;
        next_last = (next_last + 1) % array.length;
    }

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


    public T get(int index) {
        return array[(next_first+index+1)% array.length];
    }

    public int size() {
        return size;
    }


    public void printDeque() {
        for(int i = 0 ; i < size; i++){
            System.out.print(array[(next_first+i+1)%array.length]);
            System.out.print(' ');
        }
        System.out.println();
    }

    public T max() {
        if(isEmpty()) {
            return null;
        }
        T max_val = array[(next_first+1)%array.length];
        for (int i = 0; i < size; i++) {
            T next = array[(next_first+i+1)%array.length];
            if (comp.compare(max_val,next) < 0) {
                max_val = next;
            }
        }
        return max_val;
    }

    public T max(Comparator<T> c) {
        if(isEmpty()) {
            return null;
        }
        T max_val = array[(next_first+1)%array.length];
        for (int i = 0; i < size; i++) {
            T next = array[(next_first+i+1)%array.length];
            if (c.compare(max_val,next) < 0) {
                max_val = next;
            }
        }
        return max_val;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

//    public static void main(String[] args) {
//       MaxArrayDeque<Integer> deq = new MaxArrayDeque<Integer>();
//        deq.addFirst(1);
//        deq.addFirst(2);
//        deq.addFirst(3);
//        deq.addFirst(4);
//        deq.addFirst(5);
//        deq.addFirst(6);
//        deq.addFirst(7);
//        deq.addFirst(8);
//        deq.addFirst(9);
//        deq.printDeque();
//        Comparator<Integer> c = (Integer a, Integer b) -> a - b;
//        System.out.println(deq.max(c));
//    }
}