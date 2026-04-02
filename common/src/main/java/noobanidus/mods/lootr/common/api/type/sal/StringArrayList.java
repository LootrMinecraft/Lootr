package noobanidus.mods.lootr.common.api.type.sal;

import java.util.*;

/**
 * A type-specific dynamic array of Strings. Mirrors fastutil's IntArrayList.
 *
 * Extends AbstractStringList (which provides iterator, equals, hashCode,
 * compareTo, subList, stack ops, and naive bulk defaults) and overrides
 * performance-critical methods with System.arraycopy-based implementations.
 */
public class StringArrayList extends AbstractStringList
        implements RandomAccess, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_INITIAL_CAPACITY = 10;
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final String[] EMPTY_ARRAY         = new String[0];
    private static final String[] DEFAULT_EMPTY_ARRAY = new String[0];

    protected transient String[] a;
    protected int size;

    public StringArrayList(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        a = capacity == 0 ? EMPTY_ARRAY : new String[capacity];
    }

    public StringArrayList() {
        a = DEFAULT_EMPTY_ARRAY;
    }

    public StringArrayList(Collection<? extends String> c) {
        this(c.size());
        addAll(c);
    }

    public StringArrayList(String[] src) {
        this(src, 0, src.length);
    }

    public StringArrayList(String[] src, int offset, int length) {
        this(length);
        System.arraycopy(src, offset, a, 0, length);
        size = length;
    }

    private StringArrayList(String[] a, @SuppressWarnings("unused") boolean dummy) {
        this.a = a;
        this.size = a.length;
    }

    public static StringArrayList wrap(String[] a, int length) {
        if (length > a.length)
            throw new IllegalArgumentException(
                "Length (" + length + ") > array size (" + a.length + ")");
        StringArrayList l = new StringArrayList(a, false);
        l.size = length;
        return l;
    }

    public static StringArrayList wrap(String[] a) {
        return wrap(a, a.length);
    }

    @Override
    public String get(int index) {
        checkIndex(index);
        return a[index];
    }

    @Override
    public String set(int index, String s) {
        checkIndex(index);
        String old = a[index];
        a[index] = s;
        return old;
    }

    @Override
    public void add(int index, String s) {
        checkPositionIndex(index);
        grow(size + 1);
        System.arraycopy(a, index, a, index + 1, size - index);
        a[index] = s;
        size++;
    }

    @Override
    public boolean add(String s) {
        grow(size + 1);
        a[size++] = s;
        return true;
    }

    @Override
    public String remove(int index) {
        checkIndex(index);
        String old = a[index];
        size--;
        System.arraycopy(a, index + 1, a, index, size - index);
        a[size] = null;
        return old;
    }

    @Override
    public int size() { return size; }

    @Override
    public void getElements(int from, String[] dest, int offset, int length) {
        System.arraycopy(a, from, dest, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        checkRange(from, to);
        System.arraycopy(a, to, a, from, size - to);
        Arrays.fill(a, size - (to - from), size, null);
        size -= (to - from);
    }

    @Override
    public void addElements(int index, String[] src, int offset, int length) {
        checkPositionIndex(index);
        grow(size + length);
        System.arraycopy(a, index, a, index + length, size - index);
        System.arraycopy(src, offset, a, index, length);
        size += length;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof String s)) return false;
        for (int i = 0; i < size; i++) if (s.equals(a[i])) return true;
        return false;
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof String s)) return -1;
        for (int i = 0; i < size; i++) if (s.equals(a[i])) return i;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof String s)) return -1;
        for (int i = size - 1; i >= 0; i--) if (s.equals(a[i])) return i;
        return -1;
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i < 0) return false;
        remove(i);
        return true;
    }

    @Override
    public void clear() {
        Arrays.fill(a, 0, size, null);
        size = 0;
    }

    @Override
    public void size(int size) {
        if (size > a.length) ensureCapacity(size);
        if (size > this.size) Arrays.fill(a, this.size, size, null);
        this.size = size;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(a, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] dest) {
        if (dest.length < size) return (T[]) Arrays.copyOf(a, size, dest.getClass());
        System.arraycopy(a, 0, dest, 0, size);
        if (dest.length > size) dest[size] = null;
        return dest;
    }

    @Override
    public String[] toStringArray() {
        return Arrays.copyOf(a, size);
    }

    @Override
    public String[] toStringArray(String[] dest) {
        if (dest.length < size) return Arrays.copyOf(a, size);
        System.arraycopy(a, 0, dest, 0, size);
        if (dest.length > size) dest[size] = null;
        return dest;
    }

    public void ensureCapacity(int capacity) {
        if (capacity <= a.length || a == DEFAULT_EMPTY_ARRAY) return;
        a = Arrays.copyOf(a, capacity);
    }

    private void grow(int capacity) {
        if (capacity <= a.length) return;
        if (a != DEFAULT_EMPTY_ARRAY) {
            capacity = (int) Math.max(
                Math.min((long) a.length + (a.length >> 1), MAX_ARRAY_SIZE),
                capacity);
        } else if (capacity < DEFAULT_INITIAL_CAPACITY) {
            capacity = DEFAULT_INITIAL_CAPACITY;
        }
        a = Arrays.copyOf(a, capacity);
    }

    public void trim() {
        if (size < a.length) a = Arrays.copyOf(a, size);
    }

    public void trim(int n) {
        if (n >= a.length || size == a.length) return;
        a = Arrays.copyOf(a, Math.max(n, size));
    }

    public String[] elements() { return a; }

    @Override
    public StringArrayList clone() {
        try {
            StringArrayList c = (StringArrayList) super.clone();
            c.a = a.clone();
            return c;
        } catch (CloneNotSupportedException e) { throw new AssertionError(); }
    }
}
