package core;

import java.util.*;

import static java.util.Arrays.asList;

public final class Ordering {
    private static <X> List<X> cons(X x, List<X> list) {
        List<X> result = new LinkedList<>();
        result.add(x);
        result.addAll(list);

        return result;
    }


    public static <X> int compare(Comparator<X> comparator, X x, X other) {
        return comparator.compare(x, other);
    }

    public static <X extends Comparable<X>> int compare(X x, X other) {
        return compare(Ordering.<X>ascending(), x, other);
    }

    public static String op(int value) {
        switch (Integer.signum(value)) {
            case -1:
                return "<";
            case 0:
                return "~";
            case +1:
                return ">";
            default:
                return "?";
        }
    }


    public static <X extends Comparable<X>> Comparator<X> ascending() {
        return new Comparator<X>() {
            @Override
            public int compare(X x, X other) {
                if (x == null || other == null) {
                    return x == null ? other == null ? 0 : -1 : 1;
                }

                return x.compareTo(other);
            }
        };
    }

    public static <X extends Comparable<X>> Comparator<X> descending() {
        return new Comparator<X>() {
            @Override
            public int compare(X x, X other) {
                if (x == null || other == null) {
                    return x == null ? other == null ? 0 : 1 : -1;
                }

                return other.compareTo(x);
            }
        };
    }

    public static <X> Comparator<X> reversed(final Comparator<X> comparator) {
        return new Comparator<X>() {
            @Override
            public int compare(X x, X other) {
                return comparator.compare(other, x);
            }
        };
    }

    public static <X> Comparator<X> or(final Collection<Comparator<X>> comparators) {
        return new Comparator<X>() {
            @Override
            public int compare(X x, X other) {
                for (Comparator<X> comparator : comparators) {
                    int result = comparator.compare(x, other);
                    if (result == 0) {
                        continue;
                    }

                    return result;
                }

                return 0;
            }
        };
    }

    @SafeVarargs
    public static <X> Comparator<X> or(Comparator<X> comparator, Comparator<X>... rest) {
        return or(cons(comparator, asList(rest)));
    }


    public static <X, F> Comparator<X> by(final Function<X, F> key, final Comparator<F> comparator) {
        return new Comparator<X>() {
            private final Map<X, F> mapped = new HashMap<>();

            private F mapped(Function<X, F> key, X x) {
                if (!mapped.containsKey(x)) {
                    mapped.put(x, key.$(x));
                }

                return mapped.get(x);
            }

            @Override
            public int compare(X x, X other) {
                return comparator.compare(mapped(key, x), mapped(key, other));
            }
        };
    }

    public static <X, F extends Comparable<F>> Comparator<X> by(final Function<X, F> key) {
        return by(key, Ordering.<F>ascending());
    }


    public static <X> List<X> sort(Comparator<X> comparator, Iterable<X> iterable) {
        List<X> result = new LinkedList<>();
        for (X x : iterable) {
            result.add(x);
        }

        Collections.sort(result, comparator);

        return result;
    }

    public static <X extends Comparable<X>> List<X> sort(Iterable<X> iterable) {
        return sort(Ordering.<X>ascending(), iterable);
    }


    public static <X, F> List<X> sortBy(Function<X, F> key, Comparator<F> comparator, Iterable<X> iterable) {
        return sort(by(key, comparator), iterable);
    }

    public static <X, F extends Comparable<F>> List<X> sortBy(Function<X, F> key, Iterable<X> iterable) {
        return sortBy(key, Ordering.<F>ascending(), iterable);
    }


    private static <X> boolean isLessThanOrEqualTo(Comparator<X> comparator, X x, X other) {
        if (x == null || other == null) {
            return x == null;
        }

        return comparator.compare(x, other) < 1;
    }

    public static <X> boolean isSorted(Comparator<X> comparator, Iterable<X> iterable) {
        if (iterable == null) {
            return false;
        }

        Iterator<X> it = iterable.iterator();

        X previous = it.hasNext() ? it.next() : null;
        if (previous == null) {
            return true; // Iterable<X> is empty
        }

        while (it.hasNext()) {
            X x = it.next();
            if (isLessThanOrEqualTo(comparator, previous, x)) {
                continue;
            }

            return false;
        }

        return true;

    }

    public static <X extends Comparable<X>> boolean isSorted(Iterable<X> iterable) {
        return isSorted(Ordering.<X>ascending(), iterable);
    }

    public static <X, F> boolean isSortedBy(Function<X, F> key, Comparator<F> comparator, Iterable<X> iterable) {
        return isSorted(by(key, comparator), iterable);
    }

    public static <X, F extends Comparable<F>> boolean isSortedBy(Function<X, F> key, Iterable<X> iterable) {
        return isSortedBy(key, Ordering.<F>ascending(), iterable);
    }


    public static <X> SortedSet<X> sortedSet(Comparator<X> comparator, Iterable<X> iterable) {
        SortedSet<X> result = new TreeSet<>(comparator);
        for (X x : iterable) {
            result.add(x);
        }

        return result;
    }

    public static <X extends Comparable<X>> SortedSet<X> sortedSet(Iterable<X> iterable) {
        return sortedSet(Ordering.<X>ascending(), iterable);
    }

    public static <X, F> SortedSet<X> sortedSetBy(Function<X, F> key, Comparator<F> comparator, Iterable<X> iterable) {
        return sortedSet(by(key, comparator), iterable);
    }

    public static <X, F extends Comparable<F>> SortedSet<X> sortedSetBy(Function<X, F> key, Iterable<X> iterable) {
        return sortedSetBy(key, Ordering.<F>ascending(), iterable);
    }
}
