package core;

import org.junit.Test;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class IterablesTest {

    @Test
    public void shouldAllowToRetrieveFirstElement() throws Exception {
        assertThat(Iterables.first(null), is(nullValue()));
        assertThat(Iterables.first(Collections.emptyList()), is(nullValue()));

        assertThat(Iterables.first(asList(0)), is(0));
        assertThat(Iterables.first(asList(0, 1)), is(0));
        assertThat(Iterables.first(asList(0, 1, 2)), is(0));
    }

    @Test
    public void shouldAllowToRetrieveRestElements() throws Exception {
        assertThat(Iterables.toList(Iterables.rest(null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.rest(Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.rest(asList(0))), is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Iterables.rest(asList(0, 1))), is(asList(1)));
        assertThat(Iterables.toList(Iterables.rest(asList(0, 1, 2))), is(asList(1, 2)));
    }

    @Test
    public void shouldAllowToRetrieveLastElement() throws Exception {
        assertThat(Iterables.last(null), is(nullValue()));
        assertThat(Iterables.last(Collections.<String>emptyList()), is(nullValue()));

        assertThat(Iterables.last(asList(0)), is(0));
        assertThat(Iterables.last(asList(0, 1)), is(1));
        assertThat(Iterables.last(asList(0, 1, 2)), is(2));
        assertThat(Iterables.last(asList(0, 1, 2, 3)), is(3));
    }

    private static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    @Test
    public void shouldAllowToEnumerateElements() throws Exception {
        assertThat(Iterables.toList(Iterables.enumerate(null)),
                is(Collections.<Map.Entry<Long, Object>>emptyList()));
        assertThat(Iterables.toList(Iterables.enumerate(Collections.emptyList())),
                is(Collections.<Map.Entry<Long, Object>>emptyList()));

        assertThat(Iterables.toList(Iterables.enumerate(asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))),
                is(asList(
                        entry(0L, "0"), entry(1L, "1"), entry(2L, "2"), entry(3L, "3"), entry(4L, "4"),
                        entry(5L, "5"), entry(6L, "6"), entry(7L, "7"), entry(8L, "8"), entry(9L, "9"))));
    }

    @Test
    public void shouldAllowToConcatenateSequences() throws Exception {
        assertThat(Iterables.toList(Iterables.concat(null, null, null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.concat(Collections.emptyList(), Collections.emptyList())),
                is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.concat(asList(0, 1, 2), asList(3, 4, 5))), is(asList(0, 1, 2, 3, 4, 5)));
    }

    @Test
    public void shouldAllowToConstructSequence() throws Exception {
        assertThat(Iterables.toList(Iterables.cons(0, asList(1, 2, 3))), is(asList(0, 1, 2, 3)));
    }

    @Test
    public void shouldAllowToConjoinElement() throws Exception {
        assertThat(Iterables.toList(Iterables.conj(null, 0)), is(asList(0)));
        assertThat(Iterables.toList(Iterables.conj(Collections.<Integer>emptyList(), 0)), is(asList(0)));
        assertThat(Iterables.toList(Iterables.conj(asList(0), 1)), is(asList(0, 1)));
        assertThat(Iterables.toList(Iterables.conj(asList(0, 1), 2)), is(asList(0, 1, 2)));
        assertThat(Iterables.toList(Iterables.conj(asList(0, 1, 2), 3)), is(asList(0, 1, 2, 3)));
    }

    @Test
    public void shouldAllowToDropElements() throws Exception {
        assertThat(Iterables.toList(Iterables.drop(42, null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.drop(42, Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.drop(-1, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
        assertThat(Iterables.toList(Iterables.drop(-0, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));

        assertThat(Iterables.toList(Iterables.drop(1, asList(0, 1, 2, 3))), is(asList(1, 2, 3)));
        assertThat(Iterables.toList(Iterables.drop(2, asList(0, 1, 2, 3))), is(asList(2, 3)));
        assertThat(Iterables.toList(Iterables.drop(3, asList(0, 1, 2, 3))), is(asList(3)));
        assertThat(Iterables.toList(Iterables.drop(4, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Iterables.drop(5, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));
    }

    @Test
    public void shouldAllowToDropLastElements() throws Exception {
        assertThat(Iterables.toList(Iterables.dropLast(42, null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.dropLast(42, Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.dropLast(-1, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
        assertThat(Iterables.toList(Iterables.dropLast(-0, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));

        assertThat(Iterables.toList(Iterables.dropLast(1, asList(0, 1, 2, 3))), is(asList(0, 1, 2)));
        assertThat(Iterables.toList(Iterables.dropLast(2, asList(0, 1, 2, 3))), is(asList(0, 1)));
        assertThat(Iterables.toList(Iterables.dropLast(3, asList(0, 1, 2, 3))), is(asList(0)));
        assertThat(Iterables.toList(Iterables.dropLast(4, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Iterables.dropLast(5, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));
    }

    @Test
    public void shouldAllowToDropLastElement() throws Exception {
        assertThat(Iterables.toList(Iterables.dropLast(asList(0, 1, 2, 3))), is(Iterables.toList(Iterables.dropLast(1, asList(0, 1, 2, 3)))));
    }

    @Test
    public void shouldAllowToTakeElements() throws Exception {
        assertThat(Iterables.toList(Iterables.take(42, null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.take(42, Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.take(-1, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Iterables.take(-0, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));

        assertThat(Iterables.toList(Iterables.take(1, asList(0, 1, 2, 3))), is(asList(0)));
        assertThat(Iterables.toList(Iterables.take(2, asList(0, 1, 2, 3))), is(asList(0, 1)));
        assertThat(Iterables.toList(Iterables.take(3, asList(0, 1, 2, 3))), is(asList(0, 1, 2)));
        assertThat(Iterables.toList(Iterables.take(4, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
        assertThat(Iterables.toList(Iterables.take(5, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
    }

    @Test
    public void shouldAllowToTakeLastElements() throws Exception {
        assertThat(Iterables.toList(Iterables.takeLast(42, null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.takeLast(42, Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.takeLast(-1, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Iterables.takeLast(-0, asList(0, 1, 2, 3))), is(Collections.<Integer>emptyList()));

        assertThat(Iterables.toList(Iterables.takeLast(1, asList(0, 1, 2, 3))), is(asList(3)));
        assertThat(Iterables.toList(Iterables.takeLast(2, asList(0, 1, 2, 3))), is(asList(2, 3)));
        assertThat(Iterables.toList(Iterables.takeLast(3, asList(0, 1, 2, 3))), is(asList(1, 2, 3)));
        assertThat(Iterables.toList(Iterables.takeLast(4, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
        assertThat(Iterables.toList(Iterables.takeLast(5, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
    }

    @Test
    public void shouldAllowToTakeNthElements() throws Exception {
        assertThat(Iterables.toList(Iterables.takeNth(42, null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.takeNth(42, Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.takeNth(-1, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
        assertThat(Iterables.toList(Iterables.takeNth(-0, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));

        assertThat(Iterables.toList(Iterables.takeNth(1, asList(0, 1, 2, 3))), is(asList(0, 1, 2, 3)));
        assertThat(Iterables.toList(Iterables.takeNth(2, asList(0, 1, 2, 3))), is(asList(0, 2)));
        assertThat(Iterables.toList(Iterables.takeNth(3, asList(0, 1, 2, 3))), is(asList(0, 3)));
        assertThat(Iterables.toList(Iterables.takeNth(4, asList(0, 1, 2, 3))), is(asList(0)));
        assertThat(Iterables.toList(Iterables.takeNth(5, asList(0, 1, 2, 3))), is(asList(0)));
    }

    @Test
    public void shouldAllowToCycleElements() throws Exception {
        assertThat(Iterables.toList(Iterables.cycle(null)), is(Collections.emptyList()));
        assertThat(Iterables.toList(Iterables.cycle(Collections.emptyList())), is(Collections.emptyList()));

        assertThat(Iterables.toList(Iterables.take(7, Iterables.cycle(asList(0)))), is(asList(0, 0, 0, 0, 0, 0, 0)));
        assertThat(Iterables.toList(Iterables.take(7, Iterables.cycle(asList(0, 1)))), is(asList(0, 1, 0, 1, 0, 1, 0)));
        assertThat(Iterables.toList(Iterables.take(7, Iterables.cycle(asList(0, 1, 2)))), is(asList(0, 1, 2, 0, 1, 2, 0)));
        assertThat(Iterables.toList(Iterables.take(7, Iterables.cycle(asList(0, 1, 2, 3)))), is(asList(0, 1, 2, 3, 0, 1, 2)));
    }
}