package core;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public final class Timing {
    private static final int NANOSECONDS_IN_A_MILLISECOND = 1000000;

    public static long convert(long time, TimeUnit from, TimeUnit to) {
        // This function effectively addresses my inability to adequately comprehend this:
        return to.convert(time, from);
    }

    /**
     * Sleeps the specified time in units.
     * Re-interrupts the current thread when the InterruptedException occurs
     */
    public static void sleep(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public static String formatElapsedTime(long nanoseconds) {
        return String.format("%d.%06d",
                nanoseconds / NANOSECONDS_IN_A_MILLISECOND,
                nanoseconds % NANOSECONDS_IN_A_MILLISECOND);
    }

    public static long time(TimeUnit unit, Runnable operation) {
        long startTime = System.nanoTime();

        operation.run();

        long executionTime = System.nanoTime() - startTime;

        return convert(executionTime, TimeUnit.NANOSECONDS, unit);
    }

    public static void time(Runnable operation) {
        System.out.printf("Elapsed time: %s ms%n", formatElapsedTime(time(TimeUnit.NANOSECONDS, operation)));
    }

    public static <X> Map.Entry<Long, X> time(TimeUnit unit, Callable<X> operation) {
        long startTime = System.nanoTime();

        X result;
        try {
            result = operation.call();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        long executionTime = System.nanoTime() - startTime;

        return new AbstractMap.SimpleImmutableEntry<>(
                convert(executionTime, TimeUnit.NANOSECONDS, unit), result);
    }

    public static <X> X time(Callable<X> operation) {
        Map.Entry<Long, X> result = time(TimeUnit.NANOSECONDS, operation);

        System.out.printf("Elapsed time: %s ms%n", formatElapsedTime(result.getKey()));
        return result.getValue();
    }

    public static <X, F> Map.Entry<Long, F> time(TimeUnit unit, Function<X, F> operation, X x) {
        long startTime = System.nanoTime();

        F result;
        try {
            result = operation.$(x);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        long executionTime = System.nanoTime() - startTime;

        return new AbstractMap.SimpleImmutableEntry<>(
                convert(executionTime, TimeUnit.NANOSECONDS, unit), result);
    }

    public static <X, F> F time(Function<X, F> operation, X x) {
        Map.Entry<Long, F> result = time(TimeUnit.NANOSECONDS, operation, x);

        System.out.printf("Elapsed time: %s ms%n", formatElapsedTime(result.getKey()));
        return result.getValue();
    }
}
