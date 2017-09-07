package core;

import java.util.AbstractMap;
import java.util.Map;

public final class Vectors {
    public static Integer[] box(int[] array) {
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }

        return result;
    }

    public static int[] unbox(Integer[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }

        return result;
    }


    public static int[] copy(int[] array, int from, int enough) {
        int[] result = new int[enough];
        System.arraycopy(array, from, result, 0, enough);

        return result;
    }

    public static int[] copy(int[] array, int from) {
        return copy(array, from, array.length);
    }

    public static int[] copy(int[] array) {
        return copy(array, 0, array.length);
    }


    public static int[] swap_(int[] array, int i, int j) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        if (i < 0 || array.length < i
                || j < 0 || array.length < j) {
            return array;
        }

        int x = array[i];
        array[i] = array[j];
        array[j] = x;

        return array;
    }

    public static int[] swap(int[] array, int i, int j) {
        return swap_(copy(array), i, j);
    }


    public static int[] reverse_(int[] array, int from, int enough) {
        int length = array.length < enough ? array.length : enough;
        for (int i = from; i < (from + length) / 2; i++) {
            swap_(array, i, from + length - 1 - i);
        }

        return array;
    }

    public static int[] reverse_(int[] array, int from) {
        return reverse_(array, from, array.length);
    }

    public static int[] reverse_(int[] array) {
        return reverse_(array, 0, array.length);
    }

    public static int[] reverse(int[] array, int from, int enough) {
        return reverse_(copy(array), from, enough);
    }

    public static int[] reverse(int[] array, int from) {
        return reverse_(copy(array), from);
    }

    public static int[] reverse(int[] array) {
        return reverse_(copy(array));
    }


    public interface Predicate<X> {
        boolean satisfies(int index, X element, X[] array);
    }

    private static <X> boolean isSatisfied(Predicate<X> predicate, int index, X element, X[] array) {
        try {
            return predicate.satisfies(index, element, array);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    public static <X> Map.Entry<Integer, X> findFirst(X[] array, Predicate<X> predicate) {
        for (int i = 0; i < array.length; i++) {
            if (isSatisfied(predicate, i, array[i], array)) {
                return new AbstractMap.SimpleImmutableEntry<>(i, array[i]);
            }
        }

        return new AbstractMap.SimpleImmutableEntry<>(-1, null);
    }

    public static <X> Map.Entry<Integer, X> findLast(X[] array, Predicate<X> predicate) {
        for (int i = array.length - 1; -1 < i; i--) {
            if (isSatisfied(predicate, i, array[i], array)) {
                return new AbstractMap.SimpleImmutableEntry<>(i, array[i]);
            }
        }

        return new AbstractMap.SimpleImmutableEntry<>(-1, null);
    }


    public interface IntegerPredicate {
        boolean satisfies(int index, int element, int[] array);
    }

    private static boolean isSatisfied(IntegerPredicate predicate, int index, int element, int[] array) {
        try {
            return predicate.satisfies(index, element, array);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    public static Map.Entry<Integer, Integer> findFirst(int[] array, IntegerPredicate predicate) {
        for (int i = 0; i < array.length; i++) {
            if (isSatisfied(predicate, i, array[i], array)) {
                return new AbstractMap.SimpleImmutableEntry<>(i, array[i]);
            }
        }

        return new AbstractMap.SimpleImmutableEntry<>(-1, null);
    }

    public static Map.Entry<Integer, Integer> findLast(int[] array, IntegerPredicate predicate) {
        for (int i = array.length - 1; -1 < i; i--) {
            if (isSatisfied(predicate, i, array[i], array)) {
                return new AbstractMap.SimpleImmutableEntry<>(i, array[i]);
            }
        }

        return new AbstractMap.SimpleImmutableEntry<>(-1, null);
    }
}
