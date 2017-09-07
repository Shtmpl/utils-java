package core;

import java.util.*;

public final class Iterators {
    @SafeVarargs
    public static <X> Iterator<X> asIterator(final X... elements) {
        if (elements == null) {
            return Collections.emptyIterator();
        }

        return new Iterator<X>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public X next() {
                return elements[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <X> List<X> toList(Iterator<X> iterator) {
        List<X> result = new LinkedList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    public static <X> X first_(Iterator<X> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    static <X> Map.Entry<X, Iterator<X>> _first(Iterator<X> iterator) {
        return Maps.entry(first_(iterator), iterator);
    }

    public static <X> Iterator<X> rest_(Iterator<X> iterator) {
        if (iterator.hasNext()) {
            iterator.next();
        }

        return iterator;
    }

    public static <X> X last_(Iterator<X> iterator) {
        X result = null;
        while (iterator.hasNext()) {
            result = iterator.next();
        }

        return result;
    }
}
