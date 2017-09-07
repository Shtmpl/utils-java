package core;

import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class IteratorsTest {
    private static <X> void assertRemaining(Iterator<X> it, Iterator<X> expected) {

    }

    @Test
    public void shouldAllowToReturnFirstElement() throws Exception {
//        assertThat(Iterators._first(Iterators.asIterator(0, 1, 2)),
//                is(Maps.entry(0, Iterators.asIterator(1, 2))));
        Iterator<Integer> it = Iterators.asIterator(0, 1, 2);
        assertThat(Iterators.first_(it), is(0));
        assertRemaining(it, Iterators.asIterator(1, 2));
    }
}