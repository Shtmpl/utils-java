package core;

import org.junit.Test;

import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IoTest {
    @Test
    public void shouldAllowToSlurp() throws Exception {
        assertThat(Io.slurp(new StringReader("")), is(""));

        assertThat(Io.slurp(new StringReader("Line 0\n")), is("Line 0"));
        assertThat(Io.slurp(new StringReader("Line 0\r")), is("Line 0"));
        assertThat(Io.slurp(new StringReader("Line 0\r\n")), is("Line 0"));

        assertThat(Io.slurp(new StringReader("Line 0\nLine 1")), is("Line 0\nLine 1"));
        assertThat(Io.slurp(new StringReader("Line 0\rLine 1")), is("Line 0\nLine 1"));
        assertThat(Io.slurp(new StringReader("Line 0\r\nLine 1")), is("Line 0\nLine 1"));
    }

    @Test
    public void shouldAllowToSlurpIgnoringBlankLines() throws Exception {
        assertThat(Io.slurp(new StringReader(""), Io.ignoringBlankLines()), is(""));

        assertThat(Io.slurp(new StringReader("\n"), Io.ignoringBlankLines()), is(""));
        assertThat(Io.slurp(new StringReader("\r"), Io.ignoringBlankLines()), is(""));
        assertThat(Io.slurp(new StringReader("\r\n"), Io.ignoringBlankLines()), is(""));

        assertThat(Io.slurp(new StringReader("Line 0\n"), Io.ignoringBlankLines()), is("Line 0"));
        assertThat(Io.slurp(new StringReader("Line 0\n\n"), Io.ignoringBlankLines()), is("Line 0"));

        assertThat(Io.slurp(new StringReader("Line 0\n\nLine 1\n\n"), Io.ignoringBlankLines()), is("Line 0\nLine 1"));
    }

    @Test
    public void shouldAllowToSlurpIgnoringLineSegmentsThatStartWithSymbol() throws Exception {
        assertThat(Io.slurp(new StringReader(""), Io.ignoringLineSegmentsThatStartWith("#")),
                is(""));

        assertThat(Io.slurp(new StringReader("Line 0\n"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line 0"));
        assertThat(Io.slurp(new StringReader("Line 0\r"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line 0"));
        assertThat(Io.slurp(new StringReader("Line 0\r\n"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line 0"));

        assertThat(Io.slurp(new StringReader("#Line 0"), Io.ignoringLineSegmentsThatStartWith("#")),
                is(""));
        assertThat(Io.slurp(new StringReader("Line# 0"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line"));
        assertThat(Io.slurp(new StringReader("Line 0#"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line 0"));

        assertThat(Io.slurp(new StringReader("#Line 0\nLine 1"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line 1"));
        assertThat(Io.slurp(new StringReader("Line 0\n#Line 1"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line 0"));

        assertThat(Io.slurp(new StringReader("Line# 0\nLine# 1"), Io.ignoringLineSegmentsThatStartWith("#")),
                is("Line\nLine"));
    }

    private static Io.LineProcessing upperCasing() {
        return new Io.LineProcessing() {
            @Override
            public String $(String string) {
                return string.toUpperCase();
            }
        };
    }

    private static Io.LineProcessing reversing() {
        return new Io.LineProcessing() {
            @Override
            public String $(String string) {
                return new StringBuilder(string).reverse().toString();
            }
        };
    }

    @Test
    public void shouldAllowToSlurpWithArbitrarySequenceOfLineProcessingSteps() throws Exception {
        assertThat(Io.slurp(new StringReader(""), upperCasing(), reversing()),
                is(""));

        assertThat(Io.slurp(new StringReader("repaid"), upperCasing(), reversing()),
                is("DIAPER"));
        assertThat(Io.slurp(new StringReader("repaid"), reversing(), upperCasing()),
                is("DIAPER"));
    }

    @Test
    public void shouldAllowToExtractFilenameExtension() throws Exception {
        assertThat(Io.filenameExtension(null), is(""));
        assertThat(Io.filenameExtension(""), is(""));
        assertThat(Io.filenameExtension("    "), is(""));

        assertThat(Io.filenameExtension("foo"), is(""));
        assertThat(Io.filenameExtension("foo."), is(""));

        assertThat(Io.filenameExtension("foo.ext"), is("ext"));

        assertThat(Io.filenameExtension("path/to/foo.ext"), is("ext"));
        assertThat(Io.filenameExtension("path/to/foo.bar.ext"), is("ext"));
    }

    @Test
    public void shouldAllowToExtractFilenameWithoutExtension() throws Exception {
        assertThat(Io.filenameWithoutExtension(null), is(""));
        assertThat(Io.filenameWithoutExtension(""), is(""));
        assertThat(Io.filenameWithoutExtension("    "), is("    "));

        assertThat(Io.filenameWithoutExtension("foo"), is("foo"));
        assertThat(Io.filenameWithoutExtension("foo."), is("foo"));

        assertThat(Io.filenameWithoutExtension("foo.ext"), is("foo"));

        assertThat(Io.filenameWithoutExtension("path/to/foo.ext"), is("foo"));
        assertThat(Io.filenameWithoutExtension("path/to/foo.bar.ext"), is("foo.bar"));
    }


    private static final class Legacy { // Legacy sorting implementation for numeric paths
        private static void extractAlpha(List<String> result, StringBuilder chunk, String string, int index) {
            if (index == string.length()) {
                result.add(chunk.toString());
                return;
            }

            char literal = string.charAt(index);
            if (Character.isDigit(literal)) {
                if (chunk.length() > 0) {
                    result.add(chunk.toString());
                }

                extractNumeric(result, new StringBuilder(), string, index);
            } else {
                chunk.append(literal);
                extractAlpha(result, chunk, string, index + 1);
            }
        }

        private static void extractNumeric(List<String> result, StringBuilder chunk, String string, int index) {
            if (index == string.length()) {
                result.add(chunk.toString());
                return;
            }

            char literal = string.charAt(index);
            if (!Character.isDigit(literal)) {
                if (chunk.length() > 0) {
                    result.add(chunk.toString());
                }

                extractAlpha(result, new StringBuilder(), string, index);
            } else {
                chunk.append(string.charAt(index));
                extractNumeric(result, chunk, string, index + 1);
            }
        }

        private static List<String> splitStringIntoAlphaAndNumericChunks(String string) {
            if (string == null || string.isEmpty()) {
                return Collections.emptyList();
            }

            List<String> result = new LinkedList<>();
            extractAlpha(result, new StringBuilder(), string, 0);

            return result;
        }

        private static int compareStringChunks(List<String> first, List<String> second) {
            List<String> longest = (first.size() > second.size()) ? first : second;

            for (int index = 0; index < longest.size(); index++) {
                if (index == first.size() && index != second.size()) {
                    return -1;
                } else if (index != first.size() && index == second.size()) {
                    return 1;
                }

                String firstItem = first.get(index);
                String secondItem = second.get(index);

                int result;
                if (firstItem.matches("\\p{Digit}+") && secondItem.matches("\\p{Digit}+")) {
                    result = new BigInteger(firstItem).compareTo(new BigInteger(secondItem));
                } else {
                    result = first.get(index).compareTo(second.get(index));
                }

                if (result != 0) {
                    return result;
                }
            }

            return 0;
        }

        private static int compareNumerically(String first, String second) {
            List<String> firstStringChunks = splitStringIntoAlphaAndNumericChunks(first);
            List<String> secondStringChunks = splitStringIntoAlphaAndNumericChunks(second);

            return compareStringChunks(firstStringChunks, secondStringChunks);
        }

        public static final Comparator<String> COMPARATOR = new Comparator<String>() {
            @Override
            public int compare(String x, String other) {
                return compareNumerically(x, other);
            }
        };
    }


    private static <X> void assertCompared(String op, Comparator<X> comparator, X x, X other) {
        String actual = String.format("%s %s %s", x, core.Ordering.op(comparator.compare(x, other)), other);
        String expected = String.format("%s %s %s", x, op, other);
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldAllowToComparePathsContainingNumericSegments() throws Exception {
        assertCompared("~", Io.PathOrdering.byPathNumerically(), Paths.get("0.foo"), Paths.get("0.foo"));
        assertCompared("~", Legacy.COMPARATOR, "0.foo", "0.foo");

        assertCompared("<", Io.PathOrdering.byPathNumerically(), Paths.get("0.foo"), Paths.get("1.foo"));
        assertCompared("<", Legacy.COMPARATOR, "0.foo", "1.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(), Paths.get("1.foo"), Paths.get("0.foo"));
        assertCompared(">", Legacy.COMPARATOR, "1.foo", "0.foo");

        assertCompared("<", Io.PathOrdering.byPathNumerically(), Paths.get("1.foo"), Paths.get("10.foo"));
        assertCompared("<", Legacy.COMPARATOR, "1.foo", "10.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(), Paths.get("10.foo"), Paths.get("1.foo"));
        assertCompared(">", Legacy.COMPARATOR, "10.foo", "1.foo");

        assertCompared("<", Io.PathOrdering.byPathNumerically(), Paths.get("2.foo"), Paths.get("10.foo"));
        assertCompared("<", Legacy.COMPARATOR, "2.foo", "10.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(), Paths.get("10.foo"), Paths.get("2.foo"));
        assertCompared(">", Legacy.COMPARATOR, "10.foo", "2.foo");

        assertCompared("<", Io.PathOrdering.byPathNumerically(), Paths.get("2.foo"), Paths.get("x.foo"));
        assertCompared("<", Legacy.COMPARATOR, "2.foo", "x.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(), Paths.get("x.foo"), Paths.get("2.foo"));
        assertCompared(">", Legacy.COMPARATOR, "x.foo", "2.foo");

        assertCompared("~", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory"),
                Paths.get("/path/to/directory"));
        assertCompared("~", Legacy.COMPARATOR,
                "/path/to/directory",
                "/path/to/directory");

        // ... shortest path first
        assertCompared("<", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory"),
                Paths.get("/path/to/directory/x.foo"));
        assertCompared("<", Legacy.COMPARATOR,
                "/path/to/directory",
                "/path/to/directory/x.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory/x.foo"),
                Paths.get("/path/to/directory"));
        assertCompared(">", Legacy.COMPARATOR,
                "/path/to/directory/x.foo",
                "/path/to/directory");

        assertCompared("<", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory/x.foo"),
                Paths.get("/path/to/directory1/x.foo"));
//        assertCompared("<", Legacy.COMPARATOR, // Fails
//                "/path/to/directory/x.foo",
//                "/path/to/directory1/x.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory1/x.foo"),
                Paths.get("/path/to/directory/x.foo"));
//        assertCompared(">", Legacy.COMPARATOR, // Fails
//                "/path/to/directory1/x.foo",
//                "/path/to/directory/x.foo");

        assertCompared("<", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory2/x.foo"),
                Paths.get("/path/to/directory10/x.foo"));
        assertCompared("<", Legacy.COMPARATOR,
                "/path/to/directory2/x.foo",
                "/path/to/directory10/x.foo");
        assertCompared(">", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory10/x.foo"),
                Paths.get("/path/to/directory2/x.foo"));
        assertCompared(">", Legacy.COMPARATOR,
                "/path/to/directory10/x.foo",
                "/path/to/directory2/x.foo");


        assertCompared("<", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/d1rect0ry/x.foo"),
                Paths.get("/path/to/directory/x.foo"));
        assertCompared("<", Legacy.COMPARATOR,
                "/path/to/d1rect0ry/x.foo",
                "/path/to/directory/x.foo");


        assertCompared("<", Io.PathOrdering.byPathNumerically(),
                Paths.get("/path/to/directory/10.foo"),
                Paths.get("/path/to/directory0/1.foo"));
//        assertCompared("<", Legacy.COMPARATOR, // Fails
//                "/path/to/directory/10.foo",
//                "/path/to/directory0/1.foo");
    }
}