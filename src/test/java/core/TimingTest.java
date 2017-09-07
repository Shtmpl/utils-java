package core;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimingTest {
    private static final class Sleeping implements Runnable {
        private final long time;
        private final TimeUnit unit;

        public Sleeping(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
        }

        @Override
        public void run() {
            try {
                unit.sleep(time);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class Counting implements Callable<Integer> {
        private final Collection<?> collection;

        public Counting(Collection<?> collection) {
            this.collection = collection;
        }

        @Override
        public Integer call() throws Exception {
            return collection.size();
        }
    }


    @Test
    public void shouldAllowToTimeRunnableOperation() throws Exception {
        Timing.time(new Sleeping(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldAllowToTimeCallableOperation() throws Exception {
        assertThat(Timing.time(new Counting(Collections.emptyList())), is(0));
        assertThat(Timing.time(new Counting(Collections.singletonList(0))), is(1));

        assertThat(Timing.time(new Counting(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))), is(10));
    }
}