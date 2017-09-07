package core;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PredicatesTest {
    @Test
    public void shouldProvideIdentityPredicate() throws Exception {
        assertThat(Predicates.identity().$(null), is(false));

        assertThat(Predicates.identity().$(Collections.emptyList()), is(true));
        assertThat(Predicates.identity().$(""), is(true));
        assertThat(Predicates.identity().$(0), is(true));
    }
}