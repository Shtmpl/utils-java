package core;

import java.util.*;

public final class Partitioning {
    private static <X> boolean equiv(X x, X other) {
        return x == null ? other == null : x.equals(other);
    }


    public static <X> Iterable<List<X>> partition(final int size, final Iterable<X> iterable) {
        return new Iterable<List<X>>() {
            @Override
            public Iterator<List<X>> iterator() {
                return new Iterator<List<X>>() {
                    private final Iterator<X> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    private List<X> nextPartition() {
                        List<X> result = new LinkedList<>();
                        for (int x = 0; it.hasNext() && x < size; x++) {
                            result.add(it.next());
                        }

                        return result;
                    }

                    @Override
                    public List<X> next() {
                        return nextPartition();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    private static <X> Iterator<X> cons(final X x, final Iterator<X> it) {
        return new Iterator<X>() {
            private boolean dispatched;

            @Override
            public boolean hasNext() {
                return !dispatched || it.hasNext();
            }

            @Override
            public X next() {
                if (dispatched) {
                    return it.next();
                }

                dispatched = true;
                return x;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static <X, F> Map.Entry<Iterator<X>, List<X>> consumeNextPartition(Function<X, F> key, Iterator<X> it) {
        if (!it.hasNext()) {
            return Maps.entry(it, Collections.<X>emptyList());
        }

        List<X> result = new LinkedList<>();

        X x = it.next();
        F fx = key.$(x);
        result.add(x);

        while (it.hasNext()) {
            X next = it.next();
            F fnext = key.$(next);

            if (!equiv(fx, fnext)) {
                return Maps.entry(cons(next, it), result);
            }

            result.add(next);
        }

        return Maps.entry(it, result);
    }

    static <X> Map.Entry<Iterator<X>, List<X>> consumeNextPartition(Iterator<X> it) {
        return consumeNextPartition(Functions.<X>identity(), it);
    }

    public static <X, F> Iterable<List<X>> partitionBy(final Function<X, F> key, final Iterable<X> iterable) {
        return new Iterable<List<X>>() {
            @Override
            public Iterator<List<X>> iterator() {
                return new Iterator<List<X>>() {
                    private Map.Entry<Iterator<X>, List<X>> consumed = Maps.entry(
                            iterable.iterator(), Collections.<X>emptyList());
                    private boolean dispatched = true;

                    @Override
                    public boolean hasNext() {
                        if (dispatched) {
                            consumed = consumeNextPartition(key, consumed.getKey());
                            dispatched = false;
                        }

                        return consumed.getKey().hasNext() || !consumed.getValue().isEmpty();
                    }

                    @Override
                    public List<X> next() {
                        dispatched = true;
                        return consumed.getValue();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
