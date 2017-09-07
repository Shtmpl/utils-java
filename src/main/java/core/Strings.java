package core;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public final class Strings {
    private static <X> List<X> cons(X x, List<X> list) {
        List<X> result = new LinkedList<>();
        result.add(x);
        result.addAll(list);

        return result;
    }


    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }


    public static boolean isNumeric(String string) {
        if (Strings.isBlank(string)) {
            return false;
        }

        return string.matches("\\p{Digit}+");
    }


    public static Iterable<String> sequence(final String string) {
        if (isBlank(string)) {
            return Iterables.emptyIterable();
        }

        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private final char[] symbols = string.toCharArray();
                    private int index;

                    @Override
                    public boolean hasNext() {
                        return index < symbols.length;
                    }

                    @Override
                    public String next() {
                        return String.valueOf(symbols[index++]);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    public static <X> String join(String separator, Iterable<X> iterable) {
        if (iterable == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String delimiter = "";
        for (X element : iterable) {
            result.append(delimiter).append(element);
            delimiter = separator == null ? "" : separator;
        }

        return result.toString();
    }

    public static <X> String join(Iterable<X> iterable) {
        return join("", iterable);
    }

    public static <X> String join(String separator, X[] array) {
        if (array == null || array.length == 0) {
            return "";
        }

        return join(separator, asList(array));
    }

    public static <X> String join(X[] array) {
        return join("", array);
    }


    public static List<String> split(String string, String regex) {
        return asList(string.split(regex));
    }

    public static List<String> split(String string, String regex, int limit) {
        return asList(string.split(regex, limit));
    }

    public static List<String> splitIntoNumericAndNonNumericParts(String string) {
        if (string.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new LinkedList<>();

        CharacterIterator it = new StringCharacterIterator(string);
        char symbol = it.first();
        while (symbol != CharacterIterator.DONE) {
            StringBuilder numericPart = new StringBuilder();
            while (symbol != CharacterIterator.DONE && Character.isDigit(symbol)) {
                numericPart.append(symbol);
                symbol = it.next();
            }

            if (0 < numericPart.length()) {
                result.add(numericPart.toString());
            }


            StringBuilder nonNumericPart = new StringBuilder();
            while (symbol != CharacterIterator.DONE && !Character.isDigit(symbol)) {
                nonNumericPart.append(symbol);
                symbol = it.next();
            }

            if (0 < nonNumericPart.length()) {
                result.add(nonNumericPart.toString());
            }
        }

        return result;
    }


    public static boolean includes(String string, String substring) {
        return string != null && substring != null && string.contains(substring);
    }

    public static boolean includesAny(String string, Iterable<String> substrings) {
        for (String substring : substrings) {
            if (string.contains(substring)) {
                return true;
            }
        }

        return false;
    }

    public static boolean includesAny(String string, String substring, String... rest) {
        return includesAny(string, cons(substring, asList(rest)));
    }

    public static int countInclusions(String string, String substring) {
        if (string == null || substring == null) {
            return 0;
        }

        if (substring.isEmpty()) {
            return string.length() + 1;
        }

        int result = 0;
        int inclusionIndex = string.indexOf(substring);
        while (inclusionIndex != -1) {
            result++;
            inclusionIndex = string.indexOf(substring, inclusionIndex + substring.length());
        }

        return result;
    }


    // Formatting

    public static String leftJustify(String string, int width, String fill) {
        if (width < string.length()) {
            return string;
        }

        return string + join(Collections.nCopies(width - string.length(), fill));
    }

    public static String leftJustify(String string, int width) {
        return leftJustify(string, width, " ");
    }

    public static String rightJustify(String string, int width, String fill) {
        if (width < string.length()) {
            return string;
        }

        return join(Collections.nCopies(width - string.length(), fill)) + string;
    }

    public static String rightJustify(String string, int width) {
        return rightJustify(string, width, " ");
    }


    public static <X> Iterable<String> mapString(final Iterable<X> iterable) {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private final Iterator<X> it = iterable.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public String next() {
                        return String.valueOf(it.next());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    public static List<String> mapFormat(String format, Iterable<?> iterable) {
        if (iterable == null) {
            return Collections.emptyList();
        }

        List<String> result = new LinkedList<>();
        for (Object element : iterable) {
            result.add(String.format(format, element));
        }

        return Collections.unmodifiableList(result);
    }

    private static final class InterleavingIterable implements Iterable<List<?>> {
        private static boolean everyHasNext(List<Iterator<?>> iterators) {
            for (Iterator<?> iterator : iterators) {
                if (iterator.hasNext()) {
                    continue;
                }

                return false;
            }

            return true;
        }

        private static List<?> everyNext(List<Iterator<?>> iterators) {
            List<Object> result = new LinkedList<>();
            for (Iterator<?> iterator : iterators) {
                result.add(iterator.next());
            }

            return result;
        }


        private final List<Iterable<?>> iterables;

        public InterleavingIterable(List<Iterable<?>> iterables) {
            this.iterables = iterables;
        }

        @Override
        public Iterator<List<?>> iterator() {
            return new Iterator<List<?>>() {
                private final List<Iterator<?>> iterators = new LinkedList<>();

                {
                    for (Iterable<?> iterable : iterables) {
                        iterators.add(iterable.iterator());
                    }
                }

                @Override
                public boolean hasNext() {
                    return everyHasNext(iterators);
                }

                @Override
                public List<?> next() {
                    return everyNext(iterators);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private static <X> Iterable<X> emptyIterable() {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {
                return Collections.emptyIterator();
            }
        };
    }

    public static List<String> mapFormat(String format, Iterable<?> first, Iterable<?>... rest) {
        if (first == null || rest == null) {
            return Collections.emptyList();
        }

        List<Iterable<?>> nonNullIterables = new LinkedList<>();
        nonNullIterables.add(first);
        for (Iterable<?> other : rest) {
            nonNullIterables.add(other == null ? emptyIterable() : other);
        }

        List<String> result = new LinkedList<>();
        for (List<?> interleaved : new InterleavingIterable(nonNullIterables)) {
            result.add(String.format(format, interleaved.toArray()));
        }

        return Collections.unmodifiableList(result);
    }
}
