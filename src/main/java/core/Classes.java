package core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Classes {
    private static final Map<Class<?>, Class<?>> WRAPPERS;

    static {
        Map<Class<?>, Class<?>> result = new HashMap<>();
        result.put(boolean.class, Boolean.class);
        result.put(char.class, Character.class);
        result.put(byte.class, Byte.class);
        result.put(short.class, Short.class);
        result.put(int.class, Integer.class);
        result.put(long.class, Long.class);
        result.put(float.class, Float.class);
        result.put(double.class, Double.class);
        result.put(void.class, Void.class);

        WRAPPERS = Collections.unmodifiableMap(result);
    }

    public static boolean isPrimitive(Class<?> type) {
        return WRAPPERS.keySet().contains(type);
    }

    public static boolean isNotPrimitive(Class<?> type) {
        return !isPrimitive(type);
    }

    public static <T> Class<T> wrapper(Class<T> primitive) {
        if (isNotPrimitive(primitive)) {
            throw new IllegalArgumentException(String.format("Primitive type is expected. Got: %s", primitive));
        }

        @SuppressWarnings("unchecked")
        Class<T> wrapper = (Class<T>) WRAPPERS.get(primitive);

        return wrapper;
    }

    public static <T> Class<T> asWrapper(Class<T> type) {
        if (isPrimitive(type)) {
            return wrapper(type);
        }

        return type;
    }

    public static <T> T cast(Class<T> type, Object value) {
        return isPrimitive(type) ? wrapper(type).cast(value) : type.cast(value);
    }

    public static boolean isInstance(Class<?> type, Object object) {
        return type.isInstance(object);
    }

    public static boolean isNotInstance(Class<?> type, Object object) {
        return !isInstance(type, object);
    }
}
