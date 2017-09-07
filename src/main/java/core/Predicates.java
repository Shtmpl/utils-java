package core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public final class Predicates {
    private static <T> List<T> cons(T element, List<T> elements) {
        List<T> result = new LinkedList<>();
        result.add(element);
        result.addAll(elements);

        return result;
    }


    private static final Predicate<?> IDENTITY = new Predicate<Object>() {
        @Override
        public Boolean $(Object x) {
            return x != null;
        }
    };

    public static <X> Predicate<X> identity() {
        @SuppressWarnings("unchecked") Predicate<X> result = (Predicate<X>) IDENTITY;
        return result;
    }


    public static <X> Predicate<X> and(final Collection<Predicate<X>> predicates) {
        return new Predicate<X>() {
            @Override
            public Boolean $(X x) {
                for (Predicate<X> predicate : predicates) {
                    if (predicate.$(x)) {
                        continue;
                    }

                    return false;
                }

                return true;
            }
        };
    }

    @SafeVarargs
    public static <X> Predicate<X> and(Predicate<X> predicate, Predicate<X>... rest) {
        return and(cons(predicate, asList(rest)));
    }

    public static <X> Predicate<X> or(final Collection<Predicate<X>> predicates) {
        return new Predicate<X>() {
            @Override
            public Boolean $(X x) {
                for (Predicate<X> predicate : predicates) {
                    if (predicate.$(x)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    @SafeVarargs
    public static <X> Predicate<X> or(Predicate<X> predicate, Predicate<X>... rest) {
        return or(cons(predicate, asList(rest)));
    }

    public static <X> Predicate<X> not(final Predicate<X> predicate) {
        return new Predicate<X>() {
            @Override
            public Boolean $(X x) {
                return !predicate.$(x);
            }
        };
    }


    public static Predicate<Integer> zeroInteger() {
        return new Predicate<Integer>() {
            @Override
            public Boolean $(Integer x) {
                return x == 0;
            }
        };
    }

    public static <X extends Number> Predicate<X> zero() {
        return new Predicate<X>() {
            @Override
            public Boolean $(X x) {
                return x.longValue() == 0;
            }
        };
    }
}
