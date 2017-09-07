package core;

import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class OrderingTest {
    private static class Point {
        public static Point point(int x, int y) {
            return new Point(x, y);
        }


        private final int x;
        private final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Point point = (Point) other;

            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;

            return result;
        }

        @Override
        public String toString() {
            return String.format("[%s %s]", x, y);
        }
    }


    private static <X> void assertCompared(String op, Comparator<X> comparator, X x, X other) {
        String actual = String.format("%s %s %s", x, Ordering.op(comparator.compare(x, other)), other);
        String expected = String.format("%s %s %s", x, op, other);
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldProvideComparatorInducingAscendingOrder() throws Exception {
        assertCompared("~", Ordering.<Integer>ascending(), null, null);
        assertCompared("<", Ordering.<Integer>ascending(), null, 0);
        assertCompared(">", Ordering.<Integer>ascending(), 0, null);

        assertCompared("~", Ordering.<Integer>ascending(), 0, 0);
        assertCompared("<", Ordering.<Integer>ascending(), 0, 1);
        assertCompared(">", Ordering.<Integer>ascending(), 1, 0);
    }

    @Test
    public void shouldProvideComparatorInducingDescendingOrder() throws Exception {
        assertCompared("~", Ordering.<Integer>descending(), null, null);
        assertCompared(">", Ordering.<Integer>descending(), null, 0);
        assertCompared("<", Ordering.<Integer>descending(), 0, null);

        assertCompared("~", Ordering.<Integer>descending(), 0, 0);
        assertCompared(">", Ordering.<Integer>descending(), 0, 1);
        assertCompared("<", Ordering.<Integer>descending(), 1, 0);
    }

    @Test
    public void shouldProvideComparatorInducingReversedOrder() throws Exception {
        assertThat(Ordering.compare(Ordering.reversed(Ordering.<Integer>ascending()), null, null),
                is(Ordering.compare(Ordering.<Integer>descending(), null, null)));
        assertThat(Ordering.compare(Ordering.reversed(Ordering.<Integer>ascending()), null, 0),
                is(Ordering.compare(Ordering.<Integer>descending(), null, 0)));
        assertThat(Ordering.compare(Ordering.reversed(Ordering.<Integer>ascending()), 0, null),
                is(Ordering.compare(Ordering.<Integer>descending(), 0, null)));

        assertThat(Ordering.compare(Ordering.reversed(Ordering.<Integer>ascending()), 0, 0),
                is(Ordering.compare(Ordering.<Integer>descending(), 0, 0)));
        assertThat(Ordering.compare(Ordering.reversed(Ordering.<Integer>ascending()), 0, 1),
                is(Ordering.compare(Ordering.<Integer>descending(), 0, 1)));
        assertThat(Ordering.compare(Ordering.reversed(Ordering.<Integer>ascending()), 1, 0),
                is(Ordering.compare(Ordering.<Integer>descending(), 1, 0)));
    }

    @Test
    public void shouldAllowToCreateComparatorInducingComplexOrder() throws Exception {
        Comparator<Point> byFirstCoordinate = new Comparator<Point>() {
            @Override
            public int compare(Point point, Point other) {
                return Integer.compare(point.x(), other.x());
            }
        };

        Comparator<Point> bySecondCoordinate = new Comparator<Point>() {
            @Override
            public int compare(Point point, Point other) {
                return Integer.compare(point.y(), other.y());
            }
        };

        Comparator<Point> comparator = Ordering.or(byFirstCoordinate, bySecondCoordinate);

        assertCompared("~", comparator, Point.point(0, 42), Point.point(0, 42));
        assertCompared("<", comparator, Point.point(0, 42), Point.point(1, 42));
        assertCompared(">", comparator, Point.point(1, 42), Point.point(0, 42));

        assertCompared("~", comparator, Point.point(42, 0), Point.point(42, 0));
        assertCompared("<", comparator, Point.point(42, 0), Point.point(42, 1));
        assertCompared(">", comparator, Point.point(42, 1), Point.point(42, 0));
    }

    @Test
    public void shouldAllowToCreateComparatorByKey() throws Exception {
        // Sorting by string length in ascending (default) order
        Comparator<String> comparator = Ordering.by(new Function<String, Integer>() {
            @Override
            public Integer $(String x) {
                return x == null ? -1 : x.length();
            }
        });

        assertCompared("~", comparator, null, null);
        assertCompared("<", comparator, null, "");
        assertCompared(">", comparator, "", null);

        assertCompared("~", comparator, "", "");
        assertCompared("<", comparator, "", "x");
        assertCompared(">", comparator, "x", "");
    }

    @Test
    public void shouldAllowToCreateComparatorByKeyInducingOrderImposedBySuppliedComparator() throws Exception {
        // Sorting by string length in descending order
        Comparator<String> comparator = Ordering.by(new Function<String, Integer>() {
            @Override
            public Integer $(String x) {
                return x == null ? -1 : x.length();
            }
        }, Ordering.<Integer>descending());

        assertCompared("~", comparator, null, null);
        assertCompared(">", comparator, null, "");
        assertCompared("<", comparator, "", null);

        assertCompared("~", comparator, "", "");
        assertCompared(">", comparator, "", "x");
        assertCompared("<", comparator, "x", "");
    }


    // .sort(Iterable<X extends Comparable<X>>)

    private static <X extends Comparable<X>> void assertSorted(List<X> input, List<X> expected) {
        for (List<X> permutation : Permutations.permutations(input)) {
            assertThat(Ordering.sort(permutation), is(expected));
        }
    }

    @Test
    public void shouldAllowToSortElements() throws Exception {
        assertSorted(
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(0, 1, 2, 2, 3, 3, 3));
    }


    // .sort(Comparator<X>, Iterable<X>)

    private static <X> void assertSorted(Comparator<X> comparator, List<X> input, List<X> expected) {
        for (List<X> permutation : Permutations.permutations(input)) {
            assertThat(Ordering.sort(comparator, permutation), is(expected));
        }
    }

    @Test
    public void shouldAllowToSortElementsInComparatorInducedOrder() throws Exception {
        // ... when it is necessary to sort objects which are not otherwise Comparable
        Comparator<Point> comparator = new Comparator<Point>() {
            @Override
            public int compare(Point point, Point other) {
                if (point == null || other == null) {
                    return point == null ? other == null ? 0 : -1 : 1;
                }

                return Integer.compare(point.x(), other.x());
            }
        };

        assertSorted(comparator,
                asList(null, Point.point(1, 0), Point.point(2, 0), Point.point(2, 0)),
                asList(null, Point.point(1, 0), Point.point(2, 0), Point.point(2, 0)));
        assertSorted(comparator,
                asList(Point.point(0, 0), Point.point(1, 0), Point.point(2, 0), Point.point(2, 0)),
                asList(Point.point(0, 0), Point.point(1, 0), Point.point(2, 0), Point.point(2, 0)));
    }


    // .sortBy(Key<X, F extends Comparable<F>>, Iterable<X>)

    private static <X, F extends Comparable<F>> void assertSortedBy(Function<X, F> key, List<X> input, List<X> expected) {
        for (List<X> permutation : Permutations.permutations(input)) {
            assertThat(Ordering.sortBy(key, permutation), is(expected));
        }
    }

    @Test
    public void shouldAllowToSortElementsByKey() throws Exception {
        assertSortedBy(
                new Function<Integer, Integer>() {
                    @Override
                    public Integer $(Integer x) {
                        return Math.abs(3 - x); // Distance to 3
                    }
                },
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(3, 3, 3, 2, 2, 1, 0));
    }


    // .sortBy(Key<X, F>, Comparator<F>, Iterable<X>)

    private static <X, F> void assertSortedBy(Function<X, F> key, Comparator<F> comparator, List<X> input, List<X> expected) {
        for (List<X> permutation : Permutations.permutations(input)) {
            assertThat(Ordering.sortBy(key, comparator, permutation), is(expected));
        }
    }

    @Test
    public void shouldAllowToSortElementsByKeyInComparatorInducedOrder() throws Exception {
        assertSortedBy(
                new Function<Integer, Integer>() {
                    @Override
                    public Integer $(Integer x) {
                        return Math.abs(3 - x);
                    }
                },
                Ordering.<Integer>descending(),
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(0, 1, 2, 2, 3, 3, 3));
    }


    @Test
    public void shouldAllowToTestIfElementsAreSorted() throws Exception {
        assertThat(Ordering.<Integer>isSorted(null), is(false));
        assertThat(Ordering.isSorted(Collections.<Integer>emptyList()), is(true));

        assertThat(Ordering.isSorted(Collections.<Integer>singletonList(null)), is(true));
        assertThat(Ordering.isSorted(Collections.singletonList(0)), is(true));

        assertThat(Ordering.isSorted(asList(null, (Integer) null)), is(true));
        assertThat(Ordering.isSorted(asList(null, 0)), is(true));

        assertThat(Ordering.isSorted(asList(0, null)), is(false));
        assertThat(Ordering.isSorted(asList(0, 0)), is(true));

        assertThat(Ordering.isSorted(asList(null, null, null, (Integer) null)), is(true));
        assertThat(Ordering.isSorted(asList(null, null, null, 0)), is(true));

        assertThat(Ordering.isSorted(asList(0, 0, 0, null)), is(false));

        assertThat(Ordering.isSorted(asList(0, 1, 2, 3)), is(true));
    }

    @Test
    public void shouldAllowToTestIfElementsAreSortedInComparatorInducedOrder() throws Exception {
        assertThat(Ordering.isSorted(Ordering.<Integer>ascending(), asList(0, 1, 2, 2, 3, 3, 3)), is(true));
        assertThat(Ordering.isSorted(Ordering.<Integer>ascending(), asList(3, 3, 3, 2, 2, 1, 0)), is(false));
    }


    private static <X> List<X> toList(Iterable<X> iterable) {
        List<X> result = new LinkedList<>();
        for (X x : iterable) {
            result.add(x);
        }

        return result;
    }

    private static <X extends Comparable<X>> void assertSortedSet(List<X> input, List<X> expected) {
        for (List<X> permutation : Permutations.permutations(input)) {
            assertThat(toList(Ordering.sortedSet(permutation)), is(expected));
        }
    }

    @Test
    public void shouldAllowToCreateSortedSet() throws Exception {
        assertSortedSet(
                asList(null, 1, 2, 2, 3, 3, 3),
                asList(null, 1, 2, 3));
        assertSortedSet(
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(0, 1, 2, 3));
    }

    private static <X> void assertSortedSet(Comparator<X> comparator, List<X> input, List<X> expected) {
        for (List<X> permutation : Permutations.permutations(input)) {
            assertThat(toList(Ordering.sortedSet(comparator, permutation)), is(expected));
        }
    }

    @Test
    public void shouldAllowToCreateSortedSetWithComparatorInducedOrder() throws Exception {
        assertSortedSet(Ordering.<Integer>ascending(),
                asList(null, 1, 2, 2, 3, 3, 3),
                asList(null, 1, 2, 3));
        assertSortedSet(Ordering.<Integer>ascending(),
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(0, 1, 2, 3));

        assertSortedSet(Ordering.<Integer>descending(),
                asList(null, 1, 2, 2, 3, 3, 3),
                asList(3, 2, 1, null));
        assertSortedSet(Ordering.<Integer>descending(),
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(3, 2, 1, 0));
    }


    @Test // FIXME:
    public void shouldDoStuff() throws Exception {
//        for (List<Point> permutation :
//                Permutations.permutations(asList(Point.point(0, 0), Point.point(0, 1), Point.point(1, 0), Point.point(1, 1))))
//            System.out.println(
//                    Sorting.sort(
//                            Sorting.or(
//                                    Sorting.by(new Sorting.Key<Point, Integer>() {
//                                        @Override
//                                        public Integer map(Point point) {
//                                            return point.x();
//                                        }
//                                    }),
//                                    Sorting.by(new Sorting.Key<Point, Integer>() {
//                                        @Override
//                                        public Integer map(Point point) {
//                                            return point.y();
//                                        }
//                                    })),
//                            permutation));
    }
}