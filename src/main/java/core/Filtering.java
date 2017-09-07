package core;

import java.util.*;

public final class Filtering {
    public static <X, F> Predicate<X> by(final Function<X, F> key, final Predicate<F> filter) {
        return new Predicate<X>() {
            private final Map<X, F> mapped = new HashMap<>();

            private F mapped(Function<X, F> key, X x) {
                if (!mapped.containsKey(x)) {
                    mapped.put(x, key.$(x));
                }

                return mapped.get(x);
            }

            @Override
            public Boolean $(X x) {
                return filter.$(mapped(key, x));
            }
        };
    }

    public static <X, F> Predicate<X> by(Function<X, F> key) {
        return by(key, Predicates.<F>identity());
    }


    private static <X> X consumeNextAcceptable(Predicate<X> predicate, Iterator<X> it) {
        while (it.hasNext()) {
            X next = it.next();

            if (predicate.$(next)) {
                return next;
            }
        }

        return null;
    }

    public static <X> Iterable<X> filter(final Predicate<X> predicate, final Iterable<X> iterable) {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable.iterator();

                    private X next;
                    private boolean dispatched = true;

                    @Override
                    public boolean hasNext() {
                        if (dispatched) {
                            next = consumeNextAcceptable(predicate, it);
                            dispatched = false;
                        }

                        return next != null;
                    }

                    @Override
                    public X next() {
                        if (dispatched) {
                            throw new IllegalStateException();
                        }

                        dispatched = true;
                        return next;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X, F> Iterable<X> filterBy(Function<X, F> key, Predicate<F> filter, Iterable<X> iterable) {
        return filter(by(key, filter), iterable);
    }


//    public interface Timestamp<X> {
//        Map.Entry<Long, TimeUnit> map(X x);
//    }
//
//    public static <X> Filter<X> byTimestamp(final Timestamp<X> key, final TimeUnit unit, final long from, final long to) {
//        return new Filter<X>() {
//            @Override
//            public Boolean $(X x) {
//                Map.Entry<Long, TimeUnit> mapped = key.map(x);
//                long time = Timing.convert(mapped.getKey(), mapped.getValue(), unit);
//
//                return from <= time && time < to;
//            }
//        };
//    }
}
