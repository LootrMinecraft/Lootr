package noobanidus.mods.lootr.util.functions;

@FunctionalInterface
public interface TriConsumer <T, U, V> {
    void consume(T t, U u, V v);
}
