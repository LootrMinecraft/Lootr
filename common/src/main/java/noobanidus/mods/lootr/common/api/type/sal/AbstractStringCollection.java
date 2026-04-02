package noobanidus.mods.lootr.common.api.type.sal;

import java.util.AbstractCollection;

/**
 * Abstract base for String collections. Mirrors fastutil's AbstractIntCollection.
 * Extends AbstractCollection<String> to inherit iterator-based defaults for
 * contains, remove, addAll, removeAll, retainAll, clear, and toString.
 *
 * Subclasses must implement iterator() and size().
 */
public abstract class AbstractStringCollection extends AbstractCollection<String>
        implements StringCollection {

    @Override
    public String[] toStringArray() {
        return toArray(new String[size()]);
    }

    @Override
    public String[] toStringArray(String[] a) {
        if (a.length < size()) a = new String[size()];
        int i = 0;
        for (String s : this) a[i++] = s;
        if (a.length > size()) a[size()] = null;
        return a;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String s : this) {
            if (!first) sb.append(", ");
            sb.append(s);
            first = false;
        }
        return sb.append("]").toString();
    }
}
