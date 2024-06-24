package deque;

public class ArrayDeque<T> {

    private T[] array;
    private int size;
    private int next_first = 0;
    private int next_last = 1;

    public ArrayDeque() {
        size = 0;
        array = (T[]) new Object[8];
    }

    private void resize(int capacity) {
        if(capacity > array.length) {
            next_last = array.length;
            T[] arr = (T[]) new Object[capacity];
            for (int i = 0; i < size; i++) {
                arr[i] = array[(i+next_first+1)%array.length];
            }
            array = arr;
            next_first = array.length - 1;
        } else {
            next_first = array.length / 2;
            T[] arr = (T[]) new Object[capacity];
            for (int i = 0; i < size; i++) {
                arr[i] = array[(i+next_first+1)%array.length];
            }
            array = arr;
        }

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
        if(!isEmpty()) {
            T return_val = array[(next_first + 1) % array.length];
            array[(next_first + 1) % array.length] = null;
            size--;
            next_first = (next_first + 1) % array.length;
//            if ((double) size / array.length < 0.25) {
//                resize(array.length/ 2);
//            }
            return return_val;
        }
        return null;
    }

    public T removeLast() {
        if(!isEmpty()) {
            T return_val = array[next_last - 1];
            array[next_last - 1] = null;
            size--;
            if (next_last - 1 < 0) {
                next_last = array.length - 1;
            } else {
                next_last = next_last - 1;
            }
//            if ((double) size / array.length < 0.25) {
//                resize(array.length / 2);
//            }
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

    public boolean isEmpty() {
        return size == 0;
    }

    public void printDeque() {
        for(int i = 0 ; i < size; i++){
            System.out.print(array[(next_first+i+1)%array.length]);
            System.out.print(' ');
        }
        System.out.println();
    }

//    public static void main(String[] args) {
//        ArrayDeque<Integer> deq = new ArrayDeque<Integer>();
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
//    }
}