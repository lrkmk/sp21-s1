package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private T[] array;
    private int size;
    private int next_first = 0;
    private int next_last = 1;
    private Comparator<T> comp;

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
