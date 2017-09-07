package core;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilteringTest {
    private static final Predicate<Integer> EVEN = new Predicate<Integer>() {
        @Override
        public Boolean $(Integer integer) {
            return integer % 2 == 0;
        }
    };

    @Test
    public void shouldAllowToFilterElements() throws Exception {
        assertThat(Iterables.toList(Filtering.filter(EVEN, asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))),
                is(asList(0, 2, 4, 6, 8)));
    }
}