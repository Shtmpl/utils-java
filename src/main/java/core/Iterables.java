package core;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public final class Iterables {
    private static final Iterable<?> EMPTY_ITERABLE = new Iterable<Object>() {
        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyIterator();
        }
    };

    public static <X> Iterable<X> emptyIterable() {
        @SuppressWarnings("unchecked") Iterable<X> result = (Iterable<X>) EMPTY_ITERABLE;
        return result;
    }

    @SafeVarargs
    public static <X> Iterable<X> asIterable(final X... elements) {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return Iterators.asIterator(elements);
            }
        };
    }

    public static <X> List<X> toList(Iterable<X> iterable) {
        List<X> result = new LinkedList<>();
        for (X x : iterable) {
            result.add(x);
        }

        return result;
    }

    public static <X> Set<X> toSet(Iterable<X> iterable) {
        Set<X> result = new HashSet<>();
        for (X x : iterable) {
            result.add(x);
        }

        return result;
    }


    public static <X> X first(Iterable<X> iterable) {
        if (iterable == null) {
            return null;
        }

        Iterator<X> it = iterable.iterator();
        return it.hasNext() ? it.next() : null;
    }

    public static <X> Iterable<X> rest(final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable.iterator();

                    {
                        if (it.hasNext()) {
                            it.next();
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public X next() {
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> X last(Iterable<X> iterable) {
        if (iterable == null) {
            return null;
        }

        X result = null;
        for (X element : iterable) {
            result = element;
        }

        return result;
    }


    public static <X> Iterable<Map.Entry<Long, X>> enumerate(final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<Map.Entry<Long, X>>() {
            @Override
            public Iterator<Map.Entry<Long, X>> iterator() {
                return new Iterator<Map.Entry<Long, X>>() {
                    private final Iterator<X> it = iterable.iterator();
                    private long index;

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Map.Entry<Long, X> next() {
                        return new AbstractMap.SimpleImmutableEntry<>(index++, it.next());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    private static <X> Iterable<X> concat(final Iterable<X> first, final Iterable<X> other) {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> firstIterator = first.iterator();
                    private final Iterator<X> otherIterator = other.iterator();

                    @Override
                    public boolean hasNext() {
                        return firstIterator.hasNext() || otherIterator.hasNext();
                    }

                    @Override
                    public X next() {
                        if (firstIterator.hasNext()) {
                            return firstIterator.next();
                        }

                        return otherIterator.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @SafeVarargs
    public static <X> Iterable<X> concat(final Iterable<X> first, final Iterable<X>... others) {
        if (first == null) {
            return emptyIterable();
        }

        if (others == null || others.length == 0) {
            return first;
        }

        Iterable<X> result = concat(first, first(asList(others)));
        for (Iterable<X> other : rest(asList(others))) {
            result = concat(result, other);
        }

        return result;
    }

    public static <X> Iterable<X> cons(final X element, final Iterable<X> iterable) {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable.iterator();

                    private X dispatched = element;

                    @Override
                    public boolean hasNext() {
                        return dispatched != null || it.hasNext();
                    }

                    @Override
                    public X next() {
                        if (dispatched != null) {
                            X result = dispatched;
                            dispatched = null;

                            return result;
                        }

                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> Iterable<X> conj(final Iterable<X> iterable, final X element) {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable == null ?
                            Collections.<X>emptyIterator() :
                            iterable.iterator();

                    private X dispatched = element;

                    @Override
                    public boolean hasNext() {
                        return it.hasNext() || dispatched != null;
                    }

                    @Override
                    public X next() {
                        if (it.hasNext()) {
                            return it.next();
                        }

                        X result = dispatched;
                        dispatched = null;

                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    public static <X> Iterable<X> drop(final int enough, final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable.iterator();

                    {
                        for (int x = 0; it.hasNext() && x < enough; it.next(), x++) ;
                    }


                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public X next() {
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> Iterable<X> dropLast(final int enough, final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> lead = drop(enough, iterable).iterator();
                    private final Iterator<X> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return lead.hasNext() && it.hasNext();
                    }

                    @Override
                    public X next() {
                        lead.next();
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> Iterable<X> dropLast(Iterable<X> iterable) {
        return dropLast(1, iterable);
    }


    public static <X> Iterable<X> take(final int enough, final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable.iterator();
                    private int consumed;

                    @Override
                    public boolean hasNext() {
                        return consumed < enough && it.hasNext();
                    }

                    @Override
                    public X next() {
                        consumed++;
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> Iterable<X> takeLast(final int enough, final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> lead = drop(enough, iterable).iterator();
                    private final Iterator<X> it = iterable.iterator();

                    {
                        for (; lead.hasNext() && it.hasNext(); lead.next(), it.next()) ;
                    }

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public X next() {
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> Iterable<X> takeNth(final int n, final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private final Iterator<X> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public X next() {
                        X result = it.next();
                        for (int x = 0; it.hasNext() && x < n - 1; it.next(), x++) ;

                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    public static <X> Iterable<X> cycle(final Iterable<X> iterable) {
        if (iterable == null) {
            return emptyIterable();
        }

        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return new Iterator<X>() {
                    private Iterator<X> it = iterable.iterator();

                    private void rewind() {
                        it = iterable.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        if (!it.hasNext()) {
                            rewind();
                        }

                        return it.hasNext();
                    }

                    @Override
                    public X next() {
                        if (!it.hasNext()) {
                            rewind();
                        }

                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <X> Map<X, Integer> frequencies(Iterable<X> iterable) {
        Map<X, Integer> result = new LinkedHashMap<>();
        for (X x : iterable) {
            if (!result.containsKey(x)) {
                result.put(x, 0);
            }

            result.put(x, result.get(x) + 1);
        }

        return result;
    }


    public static <K, V> Iterable<K> keys(final Iterable<Map.Entry<K, V>> iterable) {
        return new Iterable<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    private final Iterator<Map.Entry<K, V>> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public K next() {
                        return it.next().getKey();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <K, V> Iterable<V> values(final Iterable<Map.Entry<K, V>> iterable) {
        return new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    private final Iterator<Map.Entry<K, V>> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public V next() {
                        return it.next().getValue();
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

