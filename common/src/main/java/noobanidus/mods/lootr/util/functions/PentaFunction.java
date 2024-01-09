package noobanidus.mods.lootr.util.functions;

@FunctionalInterface
public interface PentaFunction<T, U, V, W, X, Y> {
    Y consume(T t, U u, V v, W x, X y);
}
