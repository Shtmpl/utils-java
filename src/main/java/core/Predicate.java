package core;

public interface Predicate<X> extends Function<X, Boolean> {
    @Override
    Boolean $(X x);
}
