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