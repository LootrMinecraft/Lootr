package noobanidus.mods.lootr.common.api.type.sal;

import java.util.Collection;

/**
 * A type-specific Collection of Strings. Mirrors fastutil's IntCollection.
 * Strengthens the iterator() return type and adds type-safe bulk operations.
 */
public interface StringCollection extends Collection<String> {

    @Override
    java.util.Iterator<String> iterator();

    /** Returns a new String[] containing all elements. */
    String[] toStringArray();

    /** Copies elements into {@code a} (or a new array if {@code a} is too small). */
    String[] toStringArray(String[] a);
}
