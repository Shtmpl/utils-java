package core;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public final class Io {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static <T> List<T> cons(T element, List<T> elements) {
        List<T> result = new LinkedList<>();
        result.add(element);
        result.addAll(elements);

        return result;
    }

    /**
     * Coerce to java.nio.file.Path
     */
    public static <X> Path path(X x) {
        if (x instanceof Path) {
            return (Path) x;
        } else if (x instanceof String) {
            return Paths.get((String) x);
        } else if (x instanceof URI) {
            return Paths.get((URI) x);
        }

        throw new IllegalArgumentException(String.format("%s cannot be coerced to path", x));
    }


    public interface LineProcessing extends Function<String, String> {
//        String $(String string);
    }

    public static LineProcessing ignoringBlankLines() {
        return new LineProcessing() {
            @Override
            public String $(String string) {
                return string.trim().isEmpty() ? "" : string;
            }
        };
    }

    public static LineProcessing ignoringLineSegmentsThatStartWith(final String symbol) {
        return new LineProcessing() {
            @Override
            public String $(String string) {
                int symbolInclusionIndex = string.indexOf(symbol);
                if (symbolInclusionIndex == -1) {
                    return string;
                }

                return string.substring(0, symbolInclusionIndex);
            }
        };
    }

    private static BufferedReader ensureBuffered(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        }

        return new BufferedReader(reader);
    }

    public static String slurp(Reader reader) {
        try (BufferedReader bufferedReader = ensureBuffered(reader)) {
            StringBuilder result = new StringBuilder();

            String line;
            String lineSeparator = "";
            while ((line = bufferedReader.readLine()) != null) {
                result.append(lineSeparator).append(line);
                lineSeparator = "\n";
            }

            return result.toString();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String slurp(Path path) {
        try {
            return slurp(Files.newBufferedReader(path, DEFAULT_CHARSET));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String slurp(Reader reader, Iterable<LineProcessing> processing) {
        try (BufferedReader bufferedReader = ensureBuffered(reader)) {
            StringBuilder result = new StringBuilder();

            String line;
            String lineSeparator = "";
            while ((line = bufferedReader.readLine()) != null) {
                for (LineProcessing next : processing) {
                    line = next.$(line);
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                result.append(lineSeparator).append(line);
                lineSeparator = "\n";
            }

            return result.toString();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String slurp(Path path, Iterable<LineProcessing> processing) {
        try {
            return slurp(Files.newBufferedReader(path, DEFAULT_CHARSET), processing);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String slurp(Reader reader, LineProcessing processing, LineProcessing... rest) {
        return slurp(reader, cons(processing, asList(rest)));
    }

    public static String slurp(Path path, LineProcessing processing, LineProcessing... rest) {
        try {
            return slurp(Files.newBufferedReader(path, DEFAULT_CHARSET), processing, rest);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    public static <W extends Writer> W spit(W writer, String contents, Object... options) {
        try {
            writer.write(contents);
            if (Maps.get(options, "flush?", Boolean.class, "default", true)) {
                writer.flush();
            }

            return writer;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void spit(Path path, Iterable<String> contents, Object... options) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, DEFAULT_CHARSET,
                StandardOpenOption.CREATE,
                Maps.get(options, "append?", Boolean.class, "default", false) ?
                        StandardOpenOption.APPEND :
                        StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (String segment : contents) {
                spit(writer, segment).newLine();
            }

            writer.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void spit(Path path, String contents, Object... options) {
        Map<String, Object> wrapped = Maps.asMap(options);

        try (BufferedWriter writer = Files.newBufferedWriter(path, DEFAULT_CHARSET,
                StandardOpenOption.CREATE,
                Maps.get(wrapped, "append?", Boolean.class, "default", false) ?
                        StandardOpenOption.APPEND :
                        StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            spit(writer, contents);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    public static String filenameExtension(String filename) {
        if (Strings.isBlank(filename)) {
            return "";
        }

        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }

    public static String filenameWithoutExtension(String filename) {
        if (filename == null) {
            return "";
        }

        if (!filename.contains(".")) {
            return filename;
        }

        Path path = Paths.get(filename);

        return path.getFileName().toString().contains(".") ?
                path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf("."))
                : path.getFileName().toString();
    }

    public static void deleteFile(Path path) {
        try {
            if (Files.isRegularFile(path)) {
                Files.delete(path);
                return;
            }

            throw new RuntimeException(String.format("Path: %s does not denote a regular file", path));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void deleteDirectoryRecursively(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) throws IOException {
                    Files.delete(filePath);

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directoryPath, IOException exception) throws IOException {
                    Files.delete(directoryPath);

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void deleteDirectory(Path path) {
        if (Files.isDirectory(path)) {
            deleteDirectoryRecursively(path);
            return;
        }

        throw new RuntimeException(String.format("Path: %s does not denote a directory", path));
    }


    public static final class PathFiltering {
        private static final Function<Path, String> TYPE = new Function<Path, String>() {
            @Override
            public String $(Path x) {
                if (Files.isRegularFile(x, LinkOption.NOFOLLOW_LINKS)) {
                    return "file";
                } else if (Files.isDirectory(x, LinkOption.NOFOLLOW_LINKS)) {
                    return "directory";
                }

                return "unknown";
            }
        };

        private static final Predicate<Path> FILE_FILTER = Filtering.by(
                TYPE,
                new Predicate<String>() {
                    @Override
                    public Boolean $(String x) {
                        return "f".equals(x) || "file".equals(x);
                    }
                });

        private static final Predicate<Path> DIRECTORY_FILTER = Filtering.by(
                TYPE,
                new Predicate<String>() {
                    @Override
                    public Boolean $(String x) {
                        return "d".equals(x) || "directory".equals(x);
                    }
                });

        public static Predicate<Path> files() {
            return FILE_FILTER;
        }

        public static Predicate<Path> directories() {
            return DIRECTORY_FILTER;
        }

        public static Predicate<Path> byType(String type) {
            if (type == null) {
                throw new IllegalArgumentException("Type specifier should not be null");
            }

            switch (type.toLowerCase()) {
                case "f":
                case "file":
                    return FILE_FILTER;
                case "d":
                case "directory":
                    return DIRECTORY_FILTER;
                default:
                    throw new IllegalArgumentException(String.format("Invalid type specifier: %s", type));
            }
        }


        public static Predicate<Path> byName(String glob) {
            final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(String.format("glob:%s", glob));
            return new Predicate<Path>() {
                @Override
                public Boolean $(Path x) {
                    return matcher.matches(x.getFileName());
                }
            };
        }


        public static Predicate<Path> byPath(String glob) {
            final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(String.format("glob:%s", glob));
            return new Predicate<Path>() {
                @Override
                public Boolean $(Path x) {
                    return matcher.matches(x);
                }
            };
        }


        public static Predicate<Path> byRegex(String regex) {
            final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(String.format("regex:%s", regex));
            return new Predicate<Path>() {
                @Override
                public Boolean $(Path x) {
                    return matcher.matches(x);
                }
            };
        }


        public static Predicate<Path> byLastModifiedTimestamp(final TimeUnit unit, final long from, final long to) {
            return new Predicate<Path>() {
                @Override
                public Boolean $(Path x) {
                    long time = Timing.convert(x.toFile().lastModified(), TimeUnit.MILLISECONDS, unit);

                    return from <= time && time < to;
                }
            };
        }
    }


    public static final class PathOrdering {
        private static final Function<Path, String> LEXICOGRAPHICAL_PATH = new Function<Path, String>() {
            @Override
            public String $(Path path) {
                return path.toString();
            }
        };

        public static Comparator<Path> byPathLexicographically() {
            return Ordering.by(LEXICOGRAPHICAL_PATH);
        }


        private static Iterable<String> pathSegments(final Path path) {
            return new Iterable<String>() {
                @Override
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        private final Iterator<Path> it = path.iterator();

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public String next() {
                            return it.next().toString();
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        }

        private static List<String> digitSymbols() {
            List<String> result = new LinkedList<>();
            for (int digit = 0; digit < 10; digit++) {
                result.add(String.valueOf(digit));
            }

            return result;
        }

        private static int compareNumerically(String x, String other) {
            Iterator<String> parts = Strings.splitIntoNumericAndNonNumericParts(x).iterator();
            Iterator<String> otherParts = Strings.splitIntoNumericAndNonNumericParts(other).iterator();

            while (parts.hasNext() && otherParts.hasNext()) {
                String part = parts.next();
                String otherPart = otherParts.next();

                int result = Strings.isNumeric(part) && Strings.isNumeric(otherPart) ?
                        new BigInteger(part).compareTo(new BigInteger(otherPart)) :
                        part.compareTo(otherPart);

                if (result == 0) {
                    continue;
                }

                return result;
            }

            return parts.hasNext() ? 1 : otherParts.hasNext() ? -1 : 0;
        }

        private static int compareLexicographically(String x, String other) {
            return x.compareTo(other);
        }

        private static final Comparator<Path> NUMERICAL_PATH_COMPARATOR = new Comparator<Path>() {
            @Override
            public int compare(Path path, Path other) {
                Iterator<String> it = pathSegments(path).iterator();
                Iterator<String> that = pathSegments(other).iterator();

                while (it.hasNext() && that.hasNext()) {
                    String segment = it.next();
                    String thatSegment = that.next();

                    int result = Strings.includesAny(segment, digitSymbols())
                            || Strings.includesAny(thatSegment, digitSymbols()) ?
                            compareNumerically(segment, thatSegment) :
                            compareLexicographically(segment, thatSegment);

                    if (result == 0) {
                        continue;
                    }

                    return result;
                }

                return it.hasNext() ? 1 : that.hasNext() ? -1 : 0;
            }
        };

        public static Comparator<Path> byPathNumerically() {
            return NUMERICAL_PATH_COMPARATOR;
        }


        private static final Function<Path, Long> SIZE = new Function<Path, Long>() {
            @Override
            public Long $(Path path) {
                return path.toFile().length();
            }
        };

        public static Comparator<Path> bySize() {
            return Ordering.by(SIZE);
        }


        private static final Function<Path, Long> LAST_MODIFIED_TIMESTAMP = new Function<Path, Long>() {
            @Override
            public Long $(Path path) {
                return path.toFile().lastModified();
            }
        };

        public static Comparator<Path> byLastModifiedTimestamp() {
            return Ordering.by(LAST_MODIFIED_TIMESTAMP);
        }
    }


    private static List<Path> findPaths(final Path path, int depth, final Predicate<Path> filter, Comparator<Path> comparator) {
        try {
            final List<Path> result = new LinkedList<>();
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), depth, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes attributes) throws IOException {
                    if (directoryPath.equals(path)) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (filter.$(directoryPath)) {
                        result.add(directoryPath.toAbsolutePath().normalize());
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) throws IOException {
                    if (filePath.equals(path)) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (filter.$(filePath)) {
                        result.add(filePath.toAbsolutePath().normalize());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            Collections.sort(result, comparator);

            return result;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static List<Path> findPaths(Path path, Predicate<Path> filter, Comparator<Path> comparator) {
        return findPaths(path, 1, filter, comparator);
    }

    public static List<Path> findPaths(Path path, Predicate<Path> filter) {
        return findPaths(path, 1, filter, Ordering.<Path>ascending());
    }

    public static List<Path> findPaths(Path path, Comparator<Path> comparator) {
        return findPaths(path, 1, Predicates.<Path>identity(), comparator);
    }

    public static List<Path> findPaths(Path path) {
        return findPaths(path, 1, Predicates.<Path>identity(), Ordering.<Path>ascending());
    }

    public static List<Path> findPathsRecursively(Path path, Predicate<Path> filter, Comparator<Path> comparator) {
        return findPaths(path, Integer.MAX_VALUE, filter, comparator);
    }

    public static List<Path> findPathsRecursively(Path path, Predicate<Path> filter) {
        return findPaths(path, Integer.MAX_VALUE, filter, Ordering.<Path>ascending());
    }

    public static List<Path> findPathsRecursively(Path path, Comparator<Path> comparator) {
        return findPaths(path, Integer.MAX_VALUE, Predicates.<Path>identity(), comparator);
    }

    public static List<Path> findPathsRecursively(Path path) {
        return findPaths(path, Integer.MAX_VALUE, Predicates.<Path>identity(), Ordering.<Path>ascending());
    }


    private static final class Formatting {
        public static String formatTree(final Path path) {
            try {
                final StringBuilder result = new StringBuilder();
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    private int level;

                    @Override
                    public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes attributes) throws IOException {
                        result.append(Strings.join(Collections.nCopies(level * 4 - 1, " ")))
                                .append(directoryPath.getFileName())
                                .append("\n");

                        level++;

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) throws IOException {
                        result.append(Strings.join(Collections.nCopies(level * 4, " ")))
                                .append(filePath.getFileName())
                                .append("\n");

                        return FileVisitResult.CONTINUE;
                    }
                });

                return result.toString();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Formatting.formatTree(Paths.get("src")));
    }
}
