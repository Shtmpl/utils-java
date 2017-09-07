package core;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringsTest {
    @Test
    public void shouldAllowToTestIfStringIsEmpty() throws Exception {
        assertThat(Strings.isBlank(null), is(true));
        assertThat(Strings.isBlank(""), is(true));
        assertThat(Strings.isBlank("    "), is(true));
        assertThat(Strings.isBlank("\n"), is(true));
        assertThat(Strings.isBlank("string"), is(false));
    }

    @Test
    public void shouldAllowToTestIfStringIsNotEmpty() throws Exception {
        assertThat(Strings.isNotBlank(null), is(false));
        assertThat(Strings.isNotBlank(""), is(false));
        assertThat(Strings.isNotBlank("    "), is(false));
        assertThat(Strings.isNotBlank("\n"), is(false));
        assertThat(Strings.isNotBlank("string"), is(true));
    }

    @Test
    public void shouldAllowToTestIfStringIsNumeric() throws Exception {
        assertThat(Strings.isNumeric(null), is(false));
        assertThat(Strings.isNumeric(""), is(false));

        assertThat(Strings.isNumeric("foo"), is(false));

        assertThat(Strings.isNumeric("0"), is(true));
        assertThat(Strings.isNumeric("10"), is(true));
        assertThat(Strings.isNumeric("42"), is(true));

        assertThat(Strings.isNumeric("42foo"), is(false));
        assertThat(Strings.isNumeric("foo42"), is(false));
    }


    @Test
    public void shouldAllowToRepresentStringAsSequence() throws Exception {
        assertThat(Iterables.toList(Strings.sequence(null)), is(Collections.<String>emptyList()));
        assertThat(Iterables.toList(Strings.sequence("")), is(Collections.<String>emptyList()));

        assertThat(Iterables.toList(Strings.sequence("foo")), is(asList("f", "o", "o")));
    }


    @Test
    public void shouldAllowToJoinObjectArrayElementsIntoString() throws Exception {
        assertThat(Strings.join((String[]) null), is(""));
        assertThat(Strings.join(new String[0]), is(""));
        assertThat(Strings.join(new String[]{"0"}), is("0"));
        assertThat(Strings.join(new String[]{"0", "1", "2"}), is("012"));
    }

    @Test
    public void shouldAllowToJointObjectArrayElementsIntoSomethingSeparatedString() throws Exception {
        assertThat(Strings.join(null, (String[]) null), is(""));
        assertThat(Strings.join("", (String[]) null), is(""));
        assertThat(Strings.join(null, new String[0]), is(""));
        assertThat(Strings.join("", new String[0]), is(""));

        assertThat(Strings.join("", new String[]{"0"}), is("0"));
        assertThat(Strings.join(",", new String[]{"0"}), is("0"));
        assertThat(Strings.join(", ", new String[]{"0"}), is("0"));

        assertThat(Strings.join("", new String[]{"0", "1", "2"}), is("012"));
        assertThat(Strings.join(",", new String[]{"0", "1", "2"}), is("0,1,2"));
        assertThat(Strings.join(", ", new String[]{"0", "1", "2"}), is("0, 1, 2"));
    }

    @Test
    public void shouldAllowToJoinCollectionElementsIntoString() throws Exception {
        assertThat(Strings.join((List) null), is(""));
        assertThat(Strings.join(Collections.emptyList()), is(""));
        assertThat(Strings.join(asList(0)), is("0"));
        assertThat(Strings.join(asList(0, 1, 2)), is("012"));
    }

    @Test
    public void shouldAllowToJoinCollectionElementsIntoSomethingSeparatedString() throws Exception {
        assertThat(Strings.join(null, (List) null), is(""));
        assertThat(Strings.join("", (List) null), is(""));
        assertThat(Strings.join(null, Collections.emptyList()), is(""));
        assertThat(Strings.join("", Collections.emptyList()), is(""));

        assertThat(Strings.join("", asList(0)), is("0"));
        assertThat(Strings.join(",", asList(0)), is("0"));
        assertThat(Strings.join(", ", asList(0)), is("0"));

        assertThat(Strings.join("", asList(0, 1, 2)), is("012"));
        assertThat(Strings.join(",", asList(0, 1, 2)), is("0,1,2"));
        assertThat(Strings.join(", ", asList(0, 1, 2)), is("0, 1, 2"));
    }


    @Test
    public void shouldAllowToSplitStringIntoAlphaAndNumericParts() throws Exception {
        assertThat(Strings.splitIntoNumericAndNonNumericParts(""), is(Collections.<String>emptyList()));
        assertThat(Strings.splitIntoNumericAndNonNumericParts("/"), is(asList("/")));
        assertThat(Strings.splitIntoNumericAndNonNumericParts("0"), is(asList("0")));
        assertThat(Strings.splitIntoNumericAndNonNumericParts("foo"), is(asList("foo")));

        assertThat(Strings.splitIntoNumericAndNonNumericParts("0.foo"), is(asList("0", ".foo")));
        assertThat(Strings.splitIntoNumericAndNonNumericParts("10.foo"), is(asList("10", ".foo")));
        assertThat(Strings.splitIntoNumericAndNonNumericParts("100.foo"), is(asList("100", ".foo")));

        assertThat(Strings.splitIntoNumericAndNonNumericParts("100.foo0"), is(asList("100", ".foo", "0")));
        assertThat(Strings.splitIntoNumericAndNonNumericParts("100.foo10"), is(asList("100", ".foo", "10")));

        assertThat(Strings.splitIntoNumericAndNonNumericParts("/path/to/directory0/0.foo"),
                is(asList("/path/to/directory", "0", "/", "0", ".foo")));
    }


    @Test
    public void shouldAllowToTestForSubstringInclusions() throws Exception {
        assertThat(Strings.includes(null, null), is(false));
        assertThat(Strings.includes("foo", null), is(false));
        assertThat(Strings.includes(null, "foo"), is(false));

        assertThat(Strings.includes("", ""), is(true));
        assertThat(Strings.includes("foo", ""), is(true));

        assertThat(Strings.includes("", "foo"), is(false));
        assertThat(Strings.includes("Hello, World!", "foo"), is(false));

        assertThat(Strings.includes("foo", "foo"), is(true));
        assertThat(Strings.includes("foo, foo", "foo"), is(true));
    }

    @Test
    public void shouldAllowToCountSubstringInclusions() throws Exception {
        assertThat(Strings.countInclusions(null, null), is(0));
        assertThat(Strings.countInclusions("foo", null), is(0));
        assertThat(Strings.countInclusions(null, "foo"), is(0));

        assertThat(Strings.countInclusions("", ""), is(1));
        assertThat(Strings.countInclusions("foo", ""), is(4)); // _f_o_o_

        assertThat(Strings.countInclusions("", "foo"), is(0));
        assertThat(Strings.countInclusions("Hello, World!", "foo"), is(0));

        assertThat(Strings.countInclusions("foo", "foo"), is(1));
        assertThat(Strings.countInclusions("foo, foo", "foo"), is(2));
        assertThat(Strings.countInclusions("foo, foo", "foo, foo"), is(1));
        assertThat(Strings.countInclusions("foo, foo, foo", "foo"), is(3));
        assertThat(Strings.countInclusions("foo, foo, foo", "foo, foo"), is(1));
        assertThat(Strings.countInclusions("foo, foo, foo", "foo, foo, foo"), is(1));
    }


    @Test
    public void shouldAllowToMapGivenStringFormatOverOneCollection() throws Exception {
        assertThat(Strings.mapFormat("%d!", null),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%d!", Collections.emptyList()),
                is(Collections.<String>emptyList()));

        assertThat(Strings.mapFormat("%d!", Collections.singletonList(0)),
                is(Collections.singletonList("0!")));
        assertThat(Strings.mapFormat("%d!", asList(0, 1)),
                is(asList("0!", "1!")));
        assertThat(Strings.mapFormat("%d!", asList(0, 1, 2)),
                is(asList("0!", "1!", "2!")));
    }

    @Test
    public void shouldAllowToMapGivenStringFormatOverMultipleCollections() throws Exception {
        assertThat(Strings.mapFormat("%s = %d!", null, (Iterable<?>) null),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%s = %d!", null, Collections.emptyList()),
                is(Collections.<String>emptyList()));

        assertThat(Strings.mapFormat("%s = %d!", Collections.emptyList(), (Iterable<?>) null),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%s = %d!", Collections.emptyList(), Collections.emptyList()),
                is(Collections.<String>emptyList()));

        assertThat(Strings.mapFormat("%s = %d!", Collections.emptyList(), null, null),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%s = %d!", Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
                is(Collections.<String>emptyList()));

        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y", "z"), (Iterable<?>) null),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y", "z"), Collections.emptyList()),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%s = %d!", null, asList(0, 1, 2)),
                is(Collections.<String>emptyList()));
        assertThat(Strings.mapFormat("%s = %d!", Collections.emptyList(), asList(0, 1, 2)),
                is(Collections.<String>emptyList()));

        assertThat(Strings.mapFormat("%s = %d!", asList("x"), asList(0)),
                is(asList("x = 0!")));
        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y"), asList(0, 1)),
                is(asList("x = 0!", "y = 1!")));
        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y", "z"), asList(0, 1, 2)),
                is(asList("x = 0!", "y = 1!", "z = 2!")));

        assertThat(Strings.mapFormat("%s = %d!", asList("x"), asList(0, 1, 2)),
                is(asList("x = 0!")));
        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y"), asList(0, 1, 2)),
                is(asList("x = 0!", "y = 1!")));

        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y", "z"), asList(0)),
                is(asList("x = 0!")));
        assertThat(Strings.mapFormat("%s = %d!", asList("x", "y", "z"), asList(0, 1)),
                is(asList("x = 0!", "y = 1!")));
    }
}