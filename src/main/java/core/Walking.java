package core;

import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;

public final class Walking {
    public interface Inner<X, F> {
        F invoke(X x);
    }

    public interface Outer<F, R> {
        R invoke(Iterable<F> iterable);
    }

    public static <X, F, C> C walk(final Inner<X, F> inner, Outer<F, C> outer, final Iterable<X> iterable) {
        return outer.invoke(new Iterable<F>() {
            @Override
            public Iterator<F> iterator() {
                return new Iterator<F>() {
                    private final Iterator<X> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public F next() {
                        return inner.invoke(it.next());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        });
    }

    public static void main(String[] args) {
//        System.out.println(walk(new Inner<Integer, Integer>() {
//            @Override
//            public Integer $(Integer x) {
//                return x * 2;
//            }
//        }, new Outer<Integer, Integer>() {
//            @Override
//            public Integer $(Iterable<Integer> iterable) {
//                int result = 0;
//                for (int x : iterable) {
//                    result += x;
//                }
//
//                return result;
//            }
//        }, asList(1, 2, 3, 4, 5)));
        System.out.println(walk(new Inner<Integer, Integer>() {
            @Override
            public Integer invoke(Integer x) {
                return x;
            }
        }, new Outer<Integer, Integer>() {
            @Override
            public Integer invoke(Iterable<Integer> iterable) {
                int max = Integer.MIN_VALUE;
                for (int x : iterable) {
                    max = max < x ? x : max;
                }

                return max;
            }
        }, asList(0, 0, 0, 1, 1, 1, 2, 2, 2)));
    }
}
