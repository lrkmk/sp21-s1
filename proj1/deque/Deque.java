package deque;

public interface Deque<T> {

    public void addFirst(T item);

    public void addLast(T item);

    public T removeFirst();

    public T removeLast();

    public T get(int index);

    public int size();

    public default boolean isEmpty() {
        return size() == 0;
    }

    public void printDeque();

}
