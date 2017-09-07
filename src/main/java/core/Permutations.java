package core;

import java.util.*;

import static java.util.Arrays.asList;

public final class Permutations {
    private static int lastIndexOfElementLessThanNextElement(int[] elements) {
        int result = -1;
        for (int i = 0; i < elements.length - 1; i++) {
            if (elements[i] < elements[i + 1]) {
                result = i;
            }
        }

        return result;
    }

    private static int lastIndexOfElementGreaterThanElementAtIndex(int index, int[] elements) {
        int result = -1;
        for (int x = index + 1; x < elements.length; x++) {
            if (elements[index] < elements[x]) {
                result = x;
            }
        }

        return result;
    }

    private static int[] nextPermutationFor(int[] permutation) {
//        int lastIndexOfElementLessThanNextElement = lastIndexOfElementLessThanNextElement(permutation);
//        int lastIndexOfElementLessThanNextElement = Vectors.findLast(permutation, new Vectors.Predicate<Integer>() {
//            @Override
//            public boolean satisfies(int index, Integer element, Integer[] array) {
//                return index < array.length - 1 && element < array[index + 1];
//            }
//        }).getKey();
        final int lastIndexOfElementLessThanNextElement = Vectors.findLast(permutation, new Vectors.IntegerPredicate() {
            @Override
            public boolean satisfies(int index, int element, int[] array) {
                return index < array.length - 1 && element < array[index + 1];
            }
        }).getKey();
        if (lastIndexOfElementLessThanNextElement == -1) {
            return new int[0];
        }

//        int lastIndexOfElementGreaterThatElementAtIndex = lastIndexOfElementGreaterThanElementAtIndex(
//                lastIndexOfElementLessThanNextElement,
//                permutation);
//        int lastIndexOfElementGreaterThatElementAtIndex = Vectors.findLast(Vectors.box(permutation), new Vectors.Predicate<Integer>() {
//            @Override
//            public boolean satisfies(int index, Integer element, Integer[] array) {
//                return array[lastIndexOfElementLessThanNextElement] < element;
//            }
//        }).getKey();
        int lastIndexOfElementGreaterThatElementAtIndex = Vectors.findLast(permutation, new Vectors.IntegerPredicate() {
            @Override
            public boolean satisfies(int index, int element, int[] array) {
                return array[lastIndexOfElementLessThanNextElement] < element;
            }
        }).getKey();
        if (lastIndexOfElementGreaterThatElementAtIndex == -1) {
            return new int[0];
        }

        return Vectors.reverse_(
                Vectors.swap_(
                        permutation,
                        lastIndexOfElementLessThanNextElement,
                        lastIndexOfElementGreaterThatElementAtIndex),
                lastIndexOfElementLessThanNextElement + 1);
    }

    private static int[] range(int enough) {
        int[] result = new int[enough];
        for (int i = 0; i < enough; i++) {
            result[i] = i;
        }

        return result;
    }

    private static <X> List<X> mapIndexes(int[] indexes, List<X> input) {
        List<X> result = new LinkedList<>();
        for (int index : indexes) {
            result.add(input.get(index));
        }

        return result;
    }

    public static <X> Iterable<List<X>> permutations(final List<X> input) {
        return new Iterable<List<X>>() {
            @Override
            public Iterator<List<X>> iterator() {
                return new Iterator<List<X>>() {
                    private int[] nextPermutationIndexes = range(input.size());

                    @Override
                    public boolean hasNext() {
                        return nextPermutationIndexes.length != 0;
                    }

                    @Override
                    public List<X> next() {
                        List<X> result = mapIndexes(nextPermutationIndexes, input);
                        nextPermutationIndexes = nextPermutationFor(nextPermutationIndexes);

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

    public static void main(String[] args) {
        for (int x = 0; x < 10; x++) {
            long startTime = System.nanoTime();
            for (Map.Entry<Long, List<Integer>> permutation :
                    Iterables.enumerate(permutations(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)))) {
                if (permutation.getKey() == 1000000L) {
//                    System.out.println(permutation.getValue()); // [2, 7, 8, 3, 9, 1, 5, 6, 0, 4]
                    break;
                }
            }

            long executionTime = System.nanoTime() - startTime;
            System.out.printf("Elapsed time: %d.%06d ms%n", executionTime / 1000000, executionTime % 1000000);
        }
    }

//                  manual        w/ boxing        w/o boxing
//    Elapsed time: 131.422817 ms 182.682110 ms 171.939444 ms
//    Elapsed time: 111.459146 ms 144.354882 ms 109.488130 ms
//    Elapsed time: 116.195380 ms 128.043724 ms  96.888032 ms
//    Elapsed time: 100.620169 ms 189.853059 ms 124.327101 ms
//    Elapsed time: 164.847241 ms 123.363109 ms 110.669334 ms
//    Elapsed time:  85.113459 ms 116.586481 ms  92.989326 ms
//    Elapsed time:  88.321592 ms 164.714044 ms  84.602336 ms
//    Elapsed time:  83.095019 ms 164.532546 ms 118.984018 ms
//    Elapsed time: 114.613708 ms 121.203570 ms 137.811567 ms
//    Elapsed time:  99.087677 ms 117.950647 ms  83.326576 ms
}
