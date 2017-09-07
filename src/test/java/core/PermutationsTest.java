package core;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PermutationsTest {
    private static <X> List<X> toList(Iterable<X> iterable) {
        List<X> result = new LinkedList<>();
        for (X x : iterable) {
            result.add(x);
        }

        return result;
    }

    @SafeVarargs
    private static <X> void assertPermuted(List<X> input, List<X>... expected) {
        assertThat(toList(Permutations.permutations(input)), is(asList(expected)));
    }

    @Test
    public void shouldAllowToCreateNoPermutations() throws Exception {
        assertPermuted(Collections.emptyList());
    }

    @Test
    public void shouldAllowToCreateIntegerPermutations() throws Exception {
        assertPermuted(asList(0),
                asList(0));
        assertPermuted(asList(0, 1),
                asList(0, 1),
                asList(1, 0));
        assertPermuted(asList(0, 1, 2),
                asList(0, 1, 2), asList(0, 2, 1),
                asList(1, 0, 2), asList(1, 2, 0),
                asList(2, 0, 1), asList(2, 1, 0));
        assertPermuted(asList(0, 1, 2, 3),
                asList(0, 1, 2, 3), asList(0, 1, 3, 2), asList(0, 2, 1, 3),
                asList(0, 2, 3, 1), asList(0, 3, 1, 2), asList(0, 3, 2, 1),
                asList(1, 0, 2, 3), asList(1, 0, 3, 2), asList(1, 2, 0, 3),
                asList(1, 2, 3, 0), asList(1, 3, 0, 2), asList(1, 3, 2, 0),
                asList(2, 0, 1, 3), asList(2, 0, 3, 1), asList(2, 1, 0, 3),
                asList(2, 1, 3, 0), asList(2, 3, 0, 1), asList(2, 3, 1, 0),
                asList(3, 0, 1, 2), asList(3, 0, 2, 1), asList(3, 1, 0, 2),
                asList(3, 1, 2, 0), asList(3, 2, 0, 1), asList(3, 2, 1, 0));
    }

    @Test
    public void shouldAllowToCreateStringPermutations() throws Exception {
        assertPermuted(asList("a"),
                asList("a"));
        assertPermuted(asList("a", "b"),
                asList("a", "b"),
                asList("b", "a"));
        assertPermuted(asList("a", "b", "c"),
                asList("a", "b", "c"), asList("a", "c", "b"),
                asList("b", "a", "c"), asList("b", "c", "a"),
                asList("c", "a", "b"), asList("c", "b", "a"));
        assertPermuted(asList("a", "b", "c", "d"),
                asList("a", "b", "c", "d"), asList("a", "b", "d", "c"), asList("a", "c", "b", "d"),
                asList("a", "c", "d", "b"), asList("a", "d", "b", "c"), asList("a", "d", "c", "b"),
                asList("b", "a", "c", "d"), asList("b", "a", "d", "c"), asList("b", "c", "a", "d"),
                asList("b", "c", "d", "a"), asList("b", "d", "a", "c"), asList("b", "d", "c", "a"),
                asList("c", "a", "b", "d"), asList("c", "a", "d", "b"), asList("c", "b", "a", "d"),
                asList("c", "b", "d", "a"), asList("c", "d", "a", "b"), asList("c", "d", "b", "a"),
                asList("d", "a", "b", "c"), asList("d", "a", "c", "b"), asList("d", "b", "a", "c"),
                asList("d", "b", "c", "a"), asList("d", "c", "a", "b"), asList("d", "c", "b", "a"));
    }

    @Test
    public void shouldAllowToCreateObjectPermutations() throws Exception {
        Object _0th = new Object();
        Object _1st = new Object();
        Object _2nd = new Object();
        Object _3rd = new Object();

        assertPermuted(asList(_0th),
                asList(_0th));
        assertPermuted(asList(_0th, _1st),
                asList(_0th, _1st),
                asList(_1st, _0th));
        assertPermuted(asList(_0th, _1st, _2nd),
                asList(_0th, _1st, _2nd), asList(_0th, _2nd, _1st),
                asList(_1st, _0th, _2nd), asList(_1st, _2nd, _0th),
                asList(_2nd, _0th, _1st), asList(_2nd, _1st, _0th));
        assertPermuted(asList(_0th, _1st, _2nd, _3rd),
                asList(_0th, _1st, _2nd, _3rd), asList(_0th, _1st, _3rd, _2nd), asList(_0th, _2nd, _1st, _3rd),
                asList(_0th, _2nd, _3rd, _1st), asList(_0th, _3rd, _1st, _2nd), asList(_0th, _3rd, _2nd, _1st),
                asList(_1st, _0th, _2nd, _3rd), asList(_1st, _0th, _3rd, _2nd), asList(_1st, _2nd, _0th, _3rd),
                asList(_1st, _2nd, _3rd, _0th), asList(_1st, _3rd, _0th, _2nd), asList(_1st, _3rd, _2nd, _0th),
                asList(_2nd, _0th, _1st, _3rd), asList(_2nd, _0th, _3rd, _1st), asList(_2nd, _1st, _0th, _3rd),
                asList(_2nd, _1st, _3rd, _0th), asList(_2nd, _3rd, _0th, _1st), asList(_2nd, _3rd, _1st, _0th),
                asList(_3rd, _0th, _1st, _2nd), asList(_3rd, _0th, _2nd, _1st), asList(_3rd, _1st, _0th, _2nd),
                asList(_3rd, _1st, _2nd, _0th), asList(_3rd, _2nd, _0th, _1st), asList(_3rd, _2nd, _1st, _0th));
    }
}