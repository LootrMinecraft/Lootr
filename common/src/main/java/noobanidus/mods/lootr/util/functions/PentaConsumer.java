package noobanidus.mods.lootr.util.functions;

@FunctionalInterface
public interface PentaConsumer <T, U, V, W, X> {
    void consume(T t, U u, V v, W w, X x);
}
