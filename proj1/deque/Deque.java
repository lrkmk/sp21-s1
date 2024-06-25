package deque;


public interface Deque<T> {

    void addFirst(T item);

    void addLast(T item);

    T removeFirst();

    T removeLast();

    T get(int index);

    int size();

    public default boolean isEmpty() {
        return size() == 0;
    }

    void printDeque();

}
