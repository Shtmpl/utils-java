package core;

import java.util.Iterator;

public final class Ranges {
    private static Iterable<Integer> rangeOfIntegers(final int from, final int to, final int step) {
        // {:pre [step > 0]}
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private int current = from;

                    @Override
                    public boolean hasNext() {
                        return to < from ? to < current : current < to;
                    }

                    @Override
                    public Integer next() {
                        int result = current;
                        current += to < from ? -step : +step;

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

    private static Iterable<Long> rangeOfLongs(final long from, final long to, final long step) {
        return new Iterable<Long>() {
            @Override
            public Iterator<Long> iterator() {
                return new Iterator<Long>() {
                    private long current = from;

                    @Override
                    public boolean hasNext() {
                        return current < to;
                    }

                    @Override
                    public Long next() {
                        long result = current;
                        current += step;

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

    private static <T> T unsupportedOperationForType(Class<?> type) {
        throw new UnsupportedOperationException(String.format("range() is not supported for %s", type));
    }

    public static <T extends Number> Iterable<T> rangeOf(Class<T> type) {
        Class<T> wrapper = Classes.asWrapper(type);
        if (Integer.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfIntegers(0, Integer.MAX_VALUE, 1);

            return result;
        } else if (Long.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfLongs(0, Long.MAX_VALUE, 1);

            return result;
        }

        return unsupportedOperationForType(type);
    }

    public static <T extends Number> Iterable<T> rangeOf(Class<T> type, T to) {
        Class<T> wrapper = Classes.asWrapper(type);
        if (Integer.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfIntegers(0, (Integer) to, 1);

            return result;
        } else if (Long.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfLongs(0, (Long) to, 1);

            return result;
        }

        return unsupportedOperationForType(type);
    }

    public static <T extends Number> Iterable<T> rangeOf(Class<T> type, T from, T to) {
        Class<T> wrapper = Classes.asWrapper(type);
        if (Integer.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfIntegers((Integer) from, (Integer) to, 1);

            return result;
        } else if (Long.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfLongs((Long) from, (Long) to, 1);

            return result;
        }

        return unsupportedOperationForType(type);
    }

    public static <T extends Number> Iterable<T> rangeOf(Class<T> type, T from, T to, T step) {
        Class<T> wrapper = Classes.asWrapper(type);
        if (Integer.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfIntegers((Integer) from, (Integer) to, (Integer) step);

            return result;
        } else if (Long.class.equals(wrapper)) {
            @SuppressWarnings("unchecked")
            Iterable<T> result = (Iterable<T>) rangeOfLongs((Long) from, (Long) to, (Long) step);

            return result;
        }

        return unsupportedOperationForType(type);
    }
}
