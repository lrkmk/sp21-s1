package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {

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

    Iterator<T> iterator();

    boolean equals(Object o);
}
