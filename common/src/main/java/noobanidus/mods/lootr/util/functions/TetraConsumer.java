package noobanidus.mods.lootr.util.functions;

@FunctionalInterface
public interface TetraConsumer <U, V, W, X> {
    void consume(U u, V v, W w, X x);
}
