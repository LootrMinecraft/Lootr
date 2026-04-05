package noobanidus.mods.lootr.common.api.type.sal;

import java.util.*;

/**
 * Abstract base for String lists. Mirrors fastutil's AbstractIntList.
 *
 * Extends AbstractStringCollection and implements StringList + StringStack.
 * Assumes constant-time random access — subclasses without it should override
 * listIterator(int) and the xAll() bulk methods.
 *
 * Concrete subclasses must implement:
 *   get(int), set(int, String), add(int, String), remove(int), size()
 */
public abstract class AbstractStringList extends AbstractStringCollection
        implements StringList, StringStack {

    @Override
    public ListIterator<String> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<String> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<String> listIterator(final int index) {
        checkPositionIndex(index);
        return new ListIterator<>() {
            int cursor = index;
            int lastRet = -1;

            @Override public boolean hasNext()     { return cursor < size(); }
            @Override public boolean hasPrevious() { return cursor > 0; }
            @Override public int nextIndex()       { return cursor; }
            @Override public int previousIndex()   { return cursor - 1; }

            @Override
            public String next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastRet = cursor++;
                return get(lastRet);
            }

            @Override
            public String previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                lastRet = --cursor;
                return get(lastRet);
            }

            @Override
            public void set(String s) {
                if (lastRet < 0) throw new IllegalStateException();
                AbstractStringList.this.set(lastRet, s);
            }

            @Override
            public void add(String s) {
                AbstractStringList.this.add(cursor++, s);
                lastRet = -1;
            }

            @Override
            public void remove() {
                if (lastRet < 0) throw new IllegalStateException();
                AbstractStringList.this.remove(lastRet);
                if (lastRet < cursor) cursor--;
                lastRet = -1;
            }
        };
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof String s)) return -1;
        ListIterator<String> it = listIterator();
        while (it.hasNext())
            if (s.equals(it.next())) return it.previousIndex();
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof String s)) return -1;
        ListIterator<String> it = listIterator(size());
        while (it.hasPrevious())
            if (s.equals(it.previous())) return it.nextIndex();
        return -1;
    }

    @Override
    public boolean add(String s) {
        add(size(), s);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i < 0) return false;
        remove(i);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        checkPositionIndex(index);
        boolean modified = false;
        for (String s : c) {
            add(index++, s);
            modified = true;
        }
        return modified;
    }

    @Override
    public StringList subList(int from, int to) {
        checkRange(from, to);
        return (this instanceof RandomAccess)
            ? new RandomAccessSubList(this, from, to)
            : new SubList(this, from, to);
    }

    @Override
    public void size(int size) {
        int current = size();
        if (size < current)
            removeElements(size, current);
        else
            while (size() < size) add(null);
    }

    @Override
    public void getElements(int from, String[] dest, int offset, int length) {
        ListIterator<String> it = listIterator(from);
        for (int i = 0; i < length; i++) dest[offset + i] = it.next();
    }

    @Override
    public void removeElements(int from, int to) {
        checkRange(from, to);
        ListIterator<String> it = listIterator(from);
        int n = to - from;
        while (n-- > 0) { it.next(); it.remove(); }
    }

    @Override
    public void addElements(int index, String[] src, int offset, int length) {
        checkPositionIndex(index);
        for (int i = 0; i < length; i++) add(index + i, src[offset + i]);
    }

    @Override public void push(String s) { add(s); }
    @Override public String pop()        { return remove(size() - 1); }
    @Override public String top()        { return get(size() - 1); }
    @Override public String peek(int k)  { return get(size() - 1 - k); }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof List)) return false;
        ListIterator<String> a = listIterator();
        ListIterator<?> b = ((List<?>) o).listIterator();
        while (a.hasNext() && b.hasNext())
            if (!Objects.equals(a.next(), b.next())) return false;
        return !a.hasNext() && !b.hasNext();
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (String s : this) h = 31 * h + (s == null ? 0 : s.hashCode());
        return h;
    }

    @Override
    public int compareTo(List<? extends String> other) {
        int r;
        ListIterator<String> a = listIterator();
        ListIterator<? extends String> b = other.listIterator();
        while (a.hasNext() && b.hasNext()) {
            String as = a.next(), bs = b.next();
            if ((r = as == null ? (bs == null ? 0 : -1)
                               : (bs == null ? 1 : as.compareTo(bs))) != 0)
                return r;
        }
        return Integer.compare(size(), other.size());
    }

    @Override
    public String[] toStringArray() {
        String[] a = new String[size()];
        getElements(0, a, 0, size());
        return a;
    }

    @Override
    public void replaceAll(java.util.function.UnaryOperator<String> op) {
        ListIterator<String> it = listIterator();
        while (it.hasNext()) it.set(op.apply(it.next()));
    }

    protected void checkIndex(int i) {
        if (i < 0 || i >= size())
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size());
    }

    protected void checkPositionIndex(int i) {
        if (i < 0 || i > size())
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size());
    }

    protected void checkRange(int from, int to) {
        if (from < 0) throw new IndexOutOfBoundsException("from: " + from);
        if (to > size()) throw new IndexOutOfBoundsException("to: " + to);
        if (from > to) throw new IllegalArgumentException("from > to: " + from + " > " + to);
    }

    private static class SubList extends AbstractStringList {
        final AbstractStringList parent;
        final int offset;
        int size;

        SubList(AbstractStringList parent, int from, int to) {
            this.parent = parent;
            this.offset = from;
            this.size   = to - from;
        }

        @Override public String get(int i)            { checkIndex(i); return parent.get(offset + i); }
        @Override public String set(int i, String s)  { checkIndex(i); return parent.set(offset + i, s); }
        @Override public String remove(int i)         { checkIndex(i); String r = parent.remove(offset + i); size--; return r; }
        @Override public void add(int i, String s)    { checkPositionIndex(i); parent.add(offset + i, s); size++; }
        @Override public int size()                   { return size; }
    }

    private static class RandomAccessSubList extends SubList implements RandomAccess {
        RandomAccessSubList(AbstractStringList parent, int from, int to) {
            super(parent, from, to);
        }
    }
}
