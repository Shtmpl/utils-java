package core;

import java.util.*;

public final class Maps {
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    public static <K, V> Map<K, V> zip(Iterable<K> keys, Iterable<V> values) {
        if (keys == null || values == null) {
            return Collections.emptyMap();
        }


        Iterator<K> ks = keys.iterator();
        Iterator<V> vs = values.iterator();

        Map<K, V> result = new LinkedHashMap<>();
        while (ks.hasNext() && vs.hasNext()) {
            result.put(ks.next(), vs.next());
        }

        return result;
    }


    public static <K, V> Map<K, V> _into(Map<K, V> map, Iterable<Map.Entry<K, V>> iterable) {
        for (Map.Entry<K, V> entry : iterable) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    public static <K, V> Map<K, V> into(Map<K, V> map, Iterable<Map.Entry<K, V>> iterable) {
        return _into(new LinkedHashMap<>(map), iterable);
    }



    public static <K, V> Map<K, V> asMap(Object... elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Elements should not be null");
        } else if (elements.length % 2 == 1) {
            throw new IllegalArgumentException(String.format("Wrong number of args supplied: %d", elements.length));
        }

        Map<K, V> result = new LinkedHashMap<>();
        for (int index = 0; index < elements.length; index += 2) {
            @SuppressWarnings("unchecked") K key = (K) elements[index];
            @SuppressWarnings("unchecked") V value = (V) elements[index + 1];

            result.put(key, value);
        }

        return Collections.unmodifiableMap(result);
    }

    @SafeVarargs
    public static <K, V> boolean containsEvery(Map<K, V> map, K key, K... keys) {
        if (keys == null || keys.length == 0) {
            return map.containsKey(key);
        }

        for (K remaining : keys) {
            if (map.containsKey(remaining)) {
                continue;
            }

            return false;
        }

        return true;
    }

    /**
     * Options: <i>"default"</i>
     */
    public static <K, V> V get(Map<K, ?> map, K key, Class<V> type, Object... options) {
        if (map.containsKey(key)) {
            return Classes.cast(type, map.get(key));
        }

        Map<String, Object> wrapped = asMap(options);
        if (wrapped.containsKey("default")) {
            Object result = wrapped.get("default");

            if (result instanceof RuntimeException) {
                throw (RuntimeException) result;
            }

            return Classes.cast(type, result);
        }

        throw new IllegalArgumentException(String.format("No such key: %s", key));
    }

    public static <K, V> V get(Object[] elements, K key, Class<V> type, Object... options) {
        for (int index = 0; index < elements.length; index += 2) {
            if (key.equals(elements[index])) {
                return Classes.cast(type, elements[index + 1]);
            }
        }

        Map<String, Object> wrapped = asMap(options);
        if (wrapped.containsKey("default")) {
            Object result = wrapped.get("default");

            if (result instanceof RuntimeException) {
                throw (RuntimeException) result;
            }

            return Classes.cast(type, result);
        }

        throw new IllegalArgumentException(String.format("No such key: %s", key));
    }

    @SafeVarargs
    public static <K, V> Map<K, V> dissoc(Map<K, V> map, K key, K... keys) {
        Map<K, V> result = new HashMap<>(map);
        result.remove(key);

        if (keys == null || keys.length == 0) {
            return result;
        }

        for (K remaining : keys) {
            result.remove(remaining);
        }

        return result;
    }
}
