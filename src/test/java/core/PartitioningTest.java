package core;

import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PartitioningTest {
    @Test
    public void shouldAllowToPartitionElements() throws Exception {
        assertThat(Iterables.toList(Iterables.take(4, Partitioning.partition(-1, asList(0, 1, 2, 3)))),
                is(asList(
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList())));
        assertThat(Iterables.toList(Iterables.take(4, Partitioning.partition(-0, asList(0, 1, 2, 3)))),
                is(asList(
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList(),
                        Collections.<Integer>emptyList())));

        assertThat(Iterables.toList(Partitioning.partition(1, asList(0, 1, 2, 3))),
                is(asList(asList(0), asList(1), asList(2), asList(3))));
        assertThat(Iterables.toList(Partitioning.partition(2, asList(0, 1, 2, 3))),
                is(asList(asList(0, 1), asList(2, 3))));
        assertThat(Iterables.toList(Partitioning.partition(3, asList(0, 1, 2, 3))),
                is(asList(asList(0, 1, 2), asList(3))));
        assertThat(Iterables.toList(Partitioning.partition(4, asList(0, 1, 2, 3))),
                is(asList(asList(0, 1, 2, 3))));
    }


    private static <X> void assertConsumed(Iterator<X> it, Iterator<X> remaining, List<X> partition) {
        Map.Entry<Iterator<X>, List<X>> consumed = Partitioning.consumeNextPartition(it);

        assertThat(Iterators.toList(consumed.getKey()), is(Iterators.toList(remaining)));
        assertThat(consumed.getValue(), is(partition));
    }

    @Test
    public void shouldAllowToConsumeNextPartition() throws Exception {
        assertConsumed(
                Iterators.asIterator(0, 1, 2, 2, 3, 3, 3),
                Iterators.asIterator(/*0, */1, 2, 2, 3, 3, 3),
                asList(0));
        assertConsumed(
                Iterators.asIterator(/*0, */1, 2, 2, 3, 3, 3),
                Iterators.asIterator(/*0, 1, */2, 2, 3, 3, 3),
                asList(1));
        assertConsumed(
                Iterators.asIterator(/*0, 1, */2, 2, 3, 3, 3),
                Iterators.asIterator(/*0, 1, 2, 2, */3, 3, 3),
                asList(2, 2));
        assertConsumed(
                Iterators.asIterator(/*0, 1, 2, 2, */3, 3, 3),
                Iterators.<Integer>asIterator(/*0, 1, 2, 2, 3, 3, 3*/),
                asList(3, 3, 3));
        assertConsumed(
                Iterators.<Integer>asIterator(/*0, 1, 2, 2, 3, 3, 3*/),
                Iterators.<Integer>asIterator(/*0, 1, 2, 2, 3, 3, 3*/),
                Collections.<Integer>emptyList());

        assertConsumed(
                Iterators.asIterator(3, 3, 3, 2, 2, 1, 0),
                Iterators.asIterator(/*3, 3, 3, */2, 2, 1, 0),
                asList(3, 3, 3));
        assertConsumed(
                Iterators.asIterator(/*3, 3, 3, */2, 2, 1, 0),
                Iterators.asIterator(/*3, 3, 3, 2, 2, */1, 0),
                asList(2, 2));
        assertConsumed(
                Iterators.asIterator(/*3, 3, 3, 2, 2, */1, 0),
                Iterators.asIterator(/*3, 3, 3, 2, 2, 1, */0),
                asList(1));
        assertConsumed(
                Iterators.asIterator(/*3, 3, 3, 2, 2, 1, */0),
                Iterators.<Integer>asIterator(/*3, 3, 3, 2, 2, 1, 0*/),
                asList(0));
        assertConsumed(
                Iterators.<Integer>asIterator(/*3, 3, 3, 2, 2, 1, 0*/),
                Iterators.<Integer>asIterator(/*3, 3, 3, 2, 2, 1, 0*/),
                Collections.<Integer>emptyList());
    }


    @SafeVarargs
    private static <X, F> void assertPartitionedBy(Function<X, F> key, List<X> input, List<X>... expected) {
        assertThat(Iterables.toList(Partitioning.partitionBy(key, input)), is(asList(expected)));
    }

    @Test
    public void shouldAllowToPartitionElementsByKey() throws Exception {
        assertPartitionedBy(Functions.identity(),
                Collections.emptyList());

        assertPartitionedBy(Functions.<Integer>identity(),
                asList(0),
                asList(0));

        assertPartitionedBy(Functions.<Integer>identity(),
                asList(0, 1, 2, 2, 3, 3, 3),
                asList(0), asList(1), asList(2, 2), asList(3, 3, 3));
        assertPartitionedBy(Functions.<Integer>identity(),
                asList(3, 3, 3, 2, 2, 1, 0),
                asList(3, 3, 3), asList(2, 2), asList(1), asList(0));

        assertPartitionedBy(Predicates.zeroInteger(),
                asList(-2, -2, -1, 0, 1, 2, 2),
                asList(-2, -2, -1), asList(0), asList(1, 2, 2));
    }
}