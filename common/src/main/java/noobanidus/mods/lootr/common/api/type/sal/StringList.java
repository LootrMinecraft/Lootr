package noobanidus.mods.lootr.common.api.type.sal;

import java.util.List;
import java.util.ListIterator;

/**
 * A type-specific List of Strings. Mirrors fastutil's IntList.
 *
 * Extends List<String>, Comparable (lexicographic ordering), and StringCollection.
 * Strengthens listIterator() and subList() return types, and adds bulk
 * operations whose abstract implementations are expected to be overridden
 * with System.arraycopy-based versions in concrete classes.
 */
public interface StringList extends List<String>, Comparable<List<? extends String>>, StringCollection {

    @Override
    ListIterator<String> listIterator();

    @Override
    ListIterator<String> listIterator(int index);

    /** Returns a type-specific sublist view. Strengthens List.subList return type. */
    @Override
    StringList subList(int from, int to);

    /**
     * Resizes the list. If smaller than current size, trailing elements are discarded.
     * If larger, new slots are filled with null.
     */
    void size(int size);

    /**
     * Copies elements [{@code from}, {@code from+length}) into {@code dest} at {@code offset}.
     * Concrete classes should override this with System.arraycopy.
     */
    void getElements(int from, String[] dest, int offset, int length);

    /**
     * Removes elements in [{@code from}, {@code to}).
     * Concrete classes should override with System.arraycopy.
     */
    void removeElements(int from, int to);

    /**
     * Inserts {@code length} elements from {@code src} at {@code offset} into position {@code index}.
     * Concrete classes should override with System.arraycopy.
     */
    void addElements(int index, String[] src, int offset, int length);

    default void addElements(int index, String[] src) {
        addElements(index, src, 0, src.length);
    }
}
