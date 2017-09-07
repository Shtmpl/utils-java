package core;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.util.Arrays.asList;

public final class Watching {
    private static <T> List<T> cons(T element, List<T> list) {
        List<T> result = new LinkedList<>();
        result.add(element);
        result.addAll(list);

        return result;
    }


    private static List<WatchEvent.Kind<Path>> allWatchEvents() {
        List<WatchEvent.Kind<Path>> result = new LinkedList<>();
        Collections.addAll(result,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        return result;
    }

    private static Map.Entry<WatchService, Map<WatchKey, Path>> associateWatcherFor(List<WatchEvent.Kind<Path>> events,
                                                                                    Collection<Path> paths) {
        List<WatchEvent.Kind<Path>> desiredEvents = events.isEmpty() ? allWatchEvents() : events;
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();

            Map<WatchKey, Path> keys = new HashMap<>();
            for (Path path : paths) {
                keys.put(path.register(watcher, desiredEvents.toArray(new WatchEvent.Kind[desiredEvents.size()])), path);
            }

            return new AbstractMap.SimpleImmutableEntry<>(watcher, keys);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void ensureClosed(WatchService watchService) {
        try {
            watchService.close();
        } catch (IOException ignore) {
            /*NOP*/
        }
    }

    private static String formatEventType(WatchEvent<?> event) {
        WatchEvent.Kind<?> kind = event.kind();

        if (StandardWatchEventKinds.OVERFLOW.equals(kind)) {
            return "invalidate";
        } else if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
            return "create";
        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
            return "delete";
        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
            return "modify";
        }

        return "invalidate";
    }

    private static boolean isOverflowEvent(WatchEvent<?> event) {
        return StandardWatchEventKinds.OVERFLOW.equals(event.kind());
    }

    private static Map.Entry<Path, Path> formatOrigin(Path directoryPath, WatchEvent<?> event) {
        if (isOverflowEvent(event)) {
            return new AbstractMap.SimpleImmutableEntry<>(directoryPath, Paths.get(""));
        }

        @SuppressWarnings("unchecked") WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
        return new AbstractMap.SimpleImmutableEntry<>(directoryPath, pathEvent.context());
    }

    private static class WatchEventIterator implements Iterator<Map.Entry<String, Map.Entry<Path, Path>>> {
        private final WatchService watcher;
        private final Map<WatchKey, Path> keys;

        private final List<Map.Entry<String, Map.Entry<Path, Path>>> pending = new LinkedList<>();

        WatchEventIterator(List<WatchEvent.Kind<Path>> events, Collection<Path> paths) {
            Map.Entry<WatchService, Map<WatchKey, Path>> specification = associateWatcherFor(events, paths);

            this.watcher = specification.getKey();
            this.keys = specification.getValue();
        }

        @Override
        public boolean hasNext() {
            return !Thread.currentThread().isInterrupted();
        }

        private List<Map.Entry<String, Map.Entry<Path, Path>>> awaitEvents() {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return Collections.emptyList();
            }

            List<Map.Entry<String, Map.Entry<Path, Path>>> result = new LinkedList<>();
            for (WatchEvent<?> event : key.pollEvents()) {
                result.add(new AbstractMap.SimpleImmutableEntry<>(
                        formatEventType(event),
                        formatOrigin(keys.get(key), event)));
            }

            if (!key.reset()) {
                keys.remove(key);

                if (keys.isEmpty()) {
                    return Collections.emptyList();
                }
            }

            return result;
        }

        @Override
        public Map.Entry<String, Map.Entry<Path, Path>> next() {
            if (pending.isEmpty()) {
                pending.addAll(awaitEvents());

                if (pending.isEmpty()) {
                    ensureClosed(watcher);

                    return null; // Appropriate Sentinel?
                }
            }

            return pending.remove(0);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    private static WatchEvent.Kind<Path> mapToWatchEventKind(String eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type should not be null");
        }

        switch (eventType.toLowerCase()) {
            case "create":
                return StandardWatchEventKinds.ENTRY_CREATE;
            case "delete":
                return StandardWatchEventKinds.ENTRY_DELETE;
            case "modify":
                return StandardWatchEventKinds.ENTRY_MODIFY;
            default:
                throw new IllegalArgumentException(String.format("Invalid event type specified: %s", eventType));
        }
    }

    private static List<WatchEvent.Kind<Path>> mapToWatchEventKinds(List<String> eventTypes) {
        List<WatchEvent.Kind<Path>> result = new LinkedList<>();
        for (String eventType : eventTypes) {
            result.add(mapToWatchEventKind(eventType));
        }

        return result;
    }

    public static Iterable<Map.Entry<String, Map.Entry<Path, Path>>> watchDirectories(final List<String> eventTypes,
                                                                     final Collection<Path> paths) {
        return new Iterable<Map.Entry<String, Map.Entry<Path, Path>>>() {
            @Override
            public Iterator<Map.Entry<String, Map.Entry<Path, Path>>> iterator() {
                return new WatchEventIterator(mapToWatchEventKinds(eventTypes), paths);
            }
        };
    }

    public static Iterable<Map.Entry<String, Map.Entry<Path, Path>>> watchDirectories(List<String> eventTypes,
                                                                     Path path, Path... rest) {
        return watchDirectories(eventTypes, cons(path, asList(rest)));
    }

    public static Iterable<Map.Entry<String, Map.Entry<Path, Path>>> watchDirectories(Collection<Path> paths) {
        return watchDirectories(Collections.<String>emptyList(), paths);
    }

    public static Iterable<Map.Entry<String, Map.Entry<Path, Path>>> watchDirectories(Path path, Path... rest) {
        return watchDirectories(Collections.<String>emptyList(), cons(path, asList(rest)));
    }


    public interface Callback extends Function<Map.Entry<String, Map.Entry<Path, Path>>, Void> {
    }

    public static void watchDirectories(Callback callback, List<String> eventTypes, Collection<Path> paths) {
        Map.Entry<WatchService, Map<WatchKey, Path>> specification = associateWatcherFor(
                mapToWatchEventKinds(eventTypes), paths);

        WatchService watcher = specification.getKey();
        Map<WatchKey, Path> keys = specification.getValue();

        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                callback.$(
                        new AbstractMap.SimpleImmutableEntry<>(
                                formatEventType(event),
                                formatOrigin(keys.get(key), event)));
            }

            if (!key.reset()) {
                keys.remove(key);

                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void watchDirectories(Callback callback, List<String> eventTypes, Path path, Path... rest) {
        watchDirectories(callback, eventTypes, cons(path, asList(rest)));
    }

    public static void watchDirectories(Callback callback, Collection<Path> paths) {
        watchDirectories(callback, Collections.<String>emptyList(), paths);
    }

    public static void watchDirectories(Callback callback, Path path, Path... rest) {
        watchDirectories(callback, Collections.<String>emptyList(), cons(path, asList(rest)));
    }
}
