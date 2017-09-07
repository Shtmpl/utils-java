package core;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RangesTest {
    @Test
    public void shouldAllowToRangeIntegers() throws Exception {
        assertThat(Iterables.toList(Iterables.take(10, Ranges.rangeOf(int.class))),
                is(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));

        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 0)),
                is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 10)),
                is(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, -10)),
                is(asList(0, -1, -2, -3, -4, -5, -6, -7, -8, -9)));

        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 0, 0)),
                is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 10, 10)),
                is(Collections.<Integer>emptyList()));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 0, 10)),
                is(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 10, 0)),
                is(asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));


        assertThat(Iterables.toList(Iterables.take(10, Ranges.rangeOf(int.class, 0, 10, 0))),
                is(asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)));
        assertThat(Iterables.toList(Iterables.take(10, Ranges.rangeOf(int.class, 10, 0, 0))),
                is(asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 10)));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 0, 10, 1)),
                is(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 0, 10, 2)),
                is(asList(0, 2, 4, 6, 8)));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 10, 0, 1)),
                is(asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        assertThat(Iterables.toList(Ranges.rangeOf(int.class, 10, 0, 2)),
                is(asList(10, 8, 6, 4, 2)));
    }

    @Test
    public void shouldAllowToRangeLongs() throws Exception {
        assertThat(Iterables.toList(Iterables.take(10, Ranges.rangeOf(long.class))),
                is(asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));

        assertThat(Iterables.toList(Ranges.rangeOf(long.class, 10L)),
                is(asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));

        assertThat(Iterables.toList(Ranges.rangeOf(long.class, 0L, 10L)),
                is(asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));

        assertThat(Iterables.toList(Ranges.rangeOf(long.class, 0L, 10L, 1L)),
                is(asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
    }
}