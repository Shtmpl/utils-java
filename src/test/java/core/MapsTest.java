package core;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MapsTest {
    @Test
    public void shouldThrowWhenNullIsProvidedAsVararg() throws Exception {
        try {
            Maps.asMap((Object[]) null);
            fail();
        } catch (IllegalArgumentException expected) {
            System.out.printf("Thrown: %s%n", expected);
        }
    }

    @Test
    public void shouldThrowWhenInvalidNumberOfArgsIsProvided() throws Exception {
        try {
            Maps.asMap((Object) null);
            fail();
        } catch (IllegalArgumentException expected) {
            System.out.printf("Thrown: %s%n", expected);
        }

        try {
            Maps.asMap(null, null, null);
            fail();
        } catch (IllegalArgumentException expected) {
            System.out.printf("Thrown: %s%n", expected);
        }
    }

    @Test
    public void shouldAllowToInterpretElementsAsMap() throws Exception {
        assertThat(Maps.asMap(), is(Collections.emptyMap()));

        assertThat(Maps.asMap(null, null),
                is((Map) new HashMap<Object, Object>() {{
                    put(null, null);
                }}));

        assertThat(Maps.asMap("A", 0),
                is((Map) new HashMap<String, Integer>() {{
                    put("A", 0);
                }}));

        assertThat(Maps.asMap("A", 0, "B", 1),
                is((Map) new HashMap<String, Integer>() {{
                    put("A", 0);
                    put("B", 1);
                }}));


        assertThat(Maps.<String, Integer>asMap("A", 0).get("A"), is(0));
    }

    @Test
    public void shouldThrowWhenKeyDoesNotExist() throws Exception {
        try {
            Maps.get(Maps.asMap(), "foo", Long.class);
            fail();
        } catch (IllegalArgumentException exception) {
            System.out.printf("Thrown: %s%n", exception);
        }
    }

    @Test
    public void shouldAllowToRetrieveValueCastingItToTheTypeDesired() throws Exception {
        assertThat(Maps.get(Maps.<String, Object>asMap("foo", 0), "foo", int.class), is(0));
        assertThat(Maps.get(Maps.<String, Object>asMap("foo", 0), "foo", Integer.TYPE), is(0));
        assertThat(Maps.get(Maps.<String, Object>asMap("foo", 0), "foo", Integer.class), is(0));
    }

    @Test
    public void shouldThrowWhenRetrievingValueThatCannotBeCastedToTheTypeDesired() throws Exception {
        try {
            Maps.get(Maps.<String, Object>asMap("foo", 0), "foo", Long.class);
            fail();
        } catch (ClassCastException expected) {
            System.out.printf("Thrown: %s%n", expected);
        }
    }

    @Test
    public void shouldAllowToRetrieveDefaultValueForKeyThatDoesNotExist() throws Exception {
        assertThat(Maps.get(Maps.asMap(), "foo", Long.class, "default", 0L), is(0L));
    }
    
    @Test
    public void shouldAllowToDissociateKeys() throws Exception {
        assertThat(Maps.dissoc(Maps.asMap(), "foo"),
                is(Maps.asMap()));

        assertThat(Maps.dissoc(Maps.asMap("foo", 0), "foo"),
                is(Maps.asMap()));
        assertThat(Maps.dissoc(Maps.asMap("foo", 0, "bar", 1), "foo"),
                is(Maps.asMap("bar", 1)));
    }

    @Test
    public void shouldAllowToTestIfMapContainsEveryKeySpecified() throws Exception {
        assertThat(Maps.containsEvery(Maps.asMap(), "foo"), is(false));

        assertThat(Maps.containsEvery(Maps.asMap("foo", 0), "foo"), is(true));
        assertThat(Maps.containsEvery(Maps.asMap("foo", 0), "foo", "bar"), is(false));
        assertThat(Maps.containsEvery(Maps.asMap("foo", 0, "bar", 1), "foo", "bar"), is(true));
    }
}