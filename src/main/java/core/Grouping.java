package core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Grouping {
    // FIXME: .group() ~ .groupBy(identity)? => {0 [0], 1 [1], 2 [2 2], 3 [3 3 3], ...}

    public static <X, F> Map<F, List<X>> groupBy(Function<X, F> key, Iterable<X> iterable) {
        Map<F, List<X>> result = new LinkedHashMap<>();
        for (X x : iterable) {
            F fx = key.$(x);
            if (!result.containsKey(fx)) {
                result.put(fx, new LinkedList<X>());
            }

            result.get(fx).add(x);
        }

        return result;
    }
}
