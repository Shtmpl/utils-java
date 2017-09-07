package core;

import java.util.Map;

public final class Functions {
    private static final Function<?, ?> IDENTITY = new Function<Object, Object>() {
        @Override
        public Object $(Object x) {
            return x;
        }
    };

    public static <X> Function<X, X> identity() {
        @SuppressWarnings("unchecked") Function<X, X> result = (Function<X, X>) IDENTITY;
        return result;
    }

    public static <R, F, X> Function<Map.Entry<X, X>, R> on(final Function<Map.Entry<F, F>, R> relation,
                                                            final Function<X, F> eq) {
        return new Function<Map.Entry<X, X>, R>() {
            @Override
            public R $(Map.Entry<X, X> pair) {
                return relation.$(Maps.entry(eq.$(pair.getKey()), eq.$(pair.getValue())));
            }
        };
    }

    //    public static <X, F> Function<X, F> comp(Function<X, F> function) {
//        return function;
//    }
//
//    public static <X, F> Function<X, F> comp(final Function<?, F> function, final Function other) {
//        Function<?, ?> result = new Function<Object, Object>() {
//            @Override
//            public Object $(Object x) {
//                return function.$(other.$(x));
//            }
//        };
//        return new Function<X, F>() {
//            @Override
//            public F $(X x) {
//                return (F) function.$(other.$(x));
//            }
//        };
//    }
}
