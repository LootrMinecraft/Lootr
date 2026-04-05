package noobanidus.mods.lootr.common.api.type.sal;

/**
 * A type-specific LIFO stack of Strings. Mirrors fastutil's IntStack.
 * Intentionally independent of List — mixed in via AbstractStringList.
 */
public interface StringStack {
    /** Pushes an element onto the top of the stack. */
    void push(String s);

    /** Pops and returns the top element. */
    String pop();

    /** Returns the top element without removing it. Equivalent to peek(0). */
    String top();

    /**
     * Peeks at depth {@code k} from the top (0 = top).
     * Optional — default throws UnsupportedOperationException.
     */
    default String peek(int k) {
        throw new UnsupportedOperationException();
    }
}
