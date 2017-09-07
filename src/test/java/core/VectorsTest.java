package core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class VectorsTest {
    private static int[] ints(int... elements) {
        return elements;
    }


    private static void assertCopied(int from, int enough, int[] array, int[] expected) {
        assertThat(Vectors.copy(array, from, enough), is(expected));
    }

    @Test
    public void shouldAllowToCopyArrayElements() throws Exception {
        assertCopied(0, 0,
                ints(),
                ints());

        assertCopied(0, 0,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints());
        assertCopied(0, 1,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0));
        assertCopied(0, 2,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1));
        assertCopied(0, 3,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2));
        assertCopied(0, 4,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3));
        assertCopied(0, 5,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4));
        assertCopied(0, 6,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5));
        assertCopied(0, 7,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6));
        assertCopied(0, 8,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7));
        assertCopied(0, 9,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8));
        assertCopied(0, 10,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertCopied(0, 10,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    }


    private static void assertSwapped(int i, int j, int[] array, int[] expected) {
        assertThat(Vectors.swap_(array, i, j), is(expected));
    }

    @Test
    public void shouldAllowToSwapArrayElements() throws Exception {
        assertSwapped(0, 0,
                ints(),
                ints());
        assertSwapped(0, 0,
                ints(0),
                ints(0));

        assertSwapped(0, 0,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertSwapped(0, 1,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(1, 0, 2, 3, 4, 5, 6, 7, 8, 9));
        assertSwapped(0, 2,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(2, 1, 0, 3, 4, 5, 6, 7, 8, 9));
    }

    private static void assertReversed(int from, int enough, int[] array, int[] expected) {
        assertThat(Vectors.reverse_(array, from, enough), is(expected));
    }

    @Test
    public void shouldAllowToReverseArrayElements() throws Exception {
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)),
                is(ints(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)));

        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 0),
                is(ints(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 1),
                is(ints(0, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 2),
                is(ints(0, 1, 9, 8, 7, 6, 5, 4, 3, 2)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 3),
                is(ints(0, 1, 2, 9, 8, 7, 6, 5, 4, 3)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 4),
                is(ints(0, 1, 2, 3, 9, 8, 7, 6, 5, 4)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 5),
                is(ints(0, 1, 2, 3, 4, 9, 8, 7, 6, 5)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 6),
                is(ints(0, 1, 2, 3, 4, 5, 9, 8, 7, 6)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 7),
                is(ints(0, 1, 2, 3, 4, 5, 6, 9, 8, 7)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 8),
                is(ints(0, 1, 2, 3, 4, 5, 6, 7, 9, 8)));
        assertThat(Vectors.reverse_(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 9),
                is(ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));


        assertReversed(0, 0,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertReversed(0, 1,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertReversed(0, 2,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(1, 0, 2, 3, 4, 5, 6, 7, 8, 9));
        assertReversed(0, 3,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(2, 1, 0, 3, 4, 5, 6, 7, 8, 9));
        assertReversed(0, 4,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(3, 2, 1, 0, 4, 5, 6, 7, 8, 9));
        assertReversed(0, 5,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(4, 3, 2, 1, 0, 5, 6, 7, 8, 9));
        assertReversed(0, 6,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(5, 4, 3, 2, 1, 0, 6, 7, 8, 9));
        assertReversed(0, 7,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(6, 5, 4, 3, 2, 1, 0, 7, 8, 9));
        assertReversed(0, 8,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(7, 6, 5, 4, 3, 2, 1, 0, 8, 9));
        assertReversed(0, 9,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(8, 7, 6, 5, 4, 3, 2, 1, 0, 9));
        assertReversed(0, 10,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        assertReversed(0, 42,
                ints(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                ints(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
    }
}