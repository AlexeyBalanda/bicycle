package org.nolab.util.taskexe.test;

import org.nolab.util.context.Context;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Basis for {@link Context} and its subinterfaces test implementations.
 */
public class TIBasis implements Serializable {

    private static final long serialVersionUID = -8215171663503185353L;

    static class EntryImpl implements Context.Entry, Serializable {

        private static final long serialVersionUID = 4616829007478256840L;

        final String key;
        Object value;

        EntryImpl(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    /**
     * Iterator with entries copied in their own array.
     * Adding or removing entries in context will not
     * affect this iterator.
     */
    static class SeparatedEntryIterator {

        private final Context.Entry[] entries;
        int index = 0;

        SeparatedEntryIterator(Context.Entry[] entries, int size) {
            this.entries = new Context.Entry[size];
            System.arraycopy(entries, 0, this.entries, 0, size);
        }

        public boolean hasNext() {
            return index < entries.length;
        }

        protected Context.Entry nextEntry() {
            if (index >= entries.length) {
                throw new NoSuchElementException();
            }
            return entries[index++];
        }
    }

    /**
     * {@link Context.Keys}
     */
    static class SeparatedKeys extends SeparatedEntryIterator
            implements Iterator<String>, Context.Keys {

        SeparatedKeys(Context.Entry[] entries, int length) {
            super(entries, length);
        }

        @Override
        public String next() {
            return nextEntry().getKey();
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }
    }

    /**
     * {@link Context.Values}
     */
    static class SeparatedValues extends SeparatedEntryIterator
            implements Iterator<Object>, Context.Values {

        SeparatedValues(Context.Entry[] entries, int length) {
            super(entries, length);
        }

        @Override
        public Object next() {
            return nextEntry().getValue();
        }

        @Override
        public Iterator<Object> iterator() {
            return this;
        }
    }

    /**
     * {@link Context.Entries}
     */
    static class SeparatedEntries extends SeparatedEntryIterator
            implements Iterator<Context.Entry>, Context.Entries {

        SeparatedEntries(Context.Entry[] entries, int length) {
            super(entries, length);
        }

        @Override
        public Context.Entry next() {
            return nextEntry();
        }

        @Override
        public Iterator<Context.Entry> iterator() {
            return this;
        }
    }

    private static final int SIZE_MUL = 2;

    protected EntryImpl[] entries;
    protected int size = 0;

    /**
     *
     * @param capacity initial capacity
     */
    protected TIBasis(int capacity) {
        entries = new EntryImpl[capacity];
    }

    /**
     *
     * @param entries initial entries
     * @param size initial size
     */
    protected TIBasis(EntryImpl[] entries, int size) {
        this.entries = entries;
        this.size = size;
    }

    /**
     * All mappings from specified source.
     *
     * @param source initial mappings source
     */
    protected TIBasis(Map<String, Object> source) {
        size = source.size();
        entries = new EntryImpl[size];
        int i = 0;
        for (Map.Entry<String, Object> sourceEntry : source.entrySet()) {
            EntryImpl entry = new EntryImpl(sourceEntry.getKey(), sourceEntry.getValue());
            entries[i++] = entry;
        }
    }

    protected EntryImpl findEntry(String key) {
        for (int i = 0; i < size; i++) {
            EntryImpl entry = entries[i];
            if (Objects.equals(entry.key, key)) {
                return entry;
            }
        }
        return null;
    }

    protected void addEntry(EntryImpl entry) {
        EntryImpl[] entries;
        if (size < this.entries.length) {
            entries = this.entries;
        } else {
            entries = new EntryImpl[size * SIZE_MUL];
            System.arraycopy(this.entries, 0, entries, 0, size);
            this.entries = entries;
        }
        entries[size++] = entry;
    }

    protected void removeEntry(EntryImpl entry) {
        int i;
        int j;
        for (i = 0; i < size; i++) {
            if (entries[i] == entry) {
                break;
            }
        }
        if (i < size) {
            int decSize = size - 1;
            for (j = i; j < decSize; j++) {
                entries[j] = entries[j + 1];
            }
            entries[decSize] = null;
            size = decSize;
        }
    }

    /**
     * Create copy using constructor {@link #TIBasis(EntryImpl[] entries, int size)}
     *
     * @param <C> type of constructable subclass
     * @param ctor constructor
     * @return copy of subclass instance
     */
    protected <C extends Context> C createCopy(BiFunction<EntryImpl[], Integer, C> ctor) {
        EntryImpl[] entries = new EntryImpl[size];
        System.arraycopy(this.entries, 0, entries, 0, size);
        int size = this.size;
        return ctor.apply(entries, size);
    }

    /**
     * Create copy using constructor {@link #TIBasis(EntryImpl[] entries, int size)}
     * and select entries with specified criteria.
     *
     * @param <C> type of constructable subclass
     * @param ctor constructor
     * @param criteria criteria to select entries
     * @return copy of subclass instance
     */
    protected <C extends Context> C createCopy(BiFunction<EntryImpl[], Integer, C> ctor,
                                               Predicate<EntryImpl> criteria) {
        EntryImpl[] copyEntries = new EntryImpl[size];
        int copySize = 0;
        for (int i = 0; i < size; i++) {
            if (criteria.test(entries[i])) {
                copyEntries[copySize++] = new EntryImpl(entries[i].key, entries[i].value);
            }
        }
        return ctor.apply(copyEntries, copySize);
    }

    /**
     * Retain only those entries, that match specified criteria.
     *
     * @param criteria criteria to select entries
     */
    protected void filter(Predicate<EntryImpl> criteria) {
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (criteria.test(entries[i])) {
                entries[newSize++] = entries[i];
            }
        }
        for (int i = newSize; i < size; i++) {
            entries[i] = null;
        }
        size = newSize;
    }

    /*Implementation proxies*/

    protected Object doPut(String key, Object value) {
        EntryImpl entry = findEntry(key);
        if (entry == null) {
            EntryImpl newEntry = new EntryImpl(key, value);
            addEntry(newEntry);
            return null;
        } else {
            Object oldValue = entry.value;
            entry.value = value;
            return oldValue;
        }
    }

    protected Object doPutIfAbsent(String key, Object value) {
        EntryImpl entry = findEntry(key);
        if (entry == null) {
            EntryImpl newEntry = new EntryImpl(key, value);
            addEntry(newEntry);
            return null;
        } else {
            Object oldValue = entry.value;
            if (oldValue == null) {
                entry.value = value;
            }
            return oldValue;
        }
    }

    protected Object doPutIfKeyAbsent(String key, Object value) {
        EntryImpl entry = findEntry(key);
        if (entry == null) {
            EntryImpl newEntry = new EntryImpl(key, value);
            addEntry(newEntry);
            return null;
        } else {
            return entry.value;
        }
    }

    protected Object doGetOrCompute(String key, Function<String, Object> function) {
        Objects.requireNonNull(function);
        EntryImpl entry = findEntry(key);
        return entry == null ? function.apply(key) : entry.value;
    }

    protected Object doGetOrComputeAndPut(String key, Function<String, Object> function) {
        Objects.requireNonNull(function);
        EntryImpl entry = findEntry(key);
        if (entry == null) {
            EntryImpl newEntry = new EntryImpl(key, function.apply(key));
            addEntry(newEntry);
            return newEntry.value;
        } else {
            return entry.value;
        }
    }

    @SuppressWarnings("unchecked")
    protected  <T> T doGetOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        Objects.requireNonNull(valueType);
        Objects.requireNonNull(function);
        EntryImpl entry = findEntry(key);
        if (entry != null && valueType.isInstance(entry.value)) {
            return (T) entry.value;
        } else {
            EntryImpl newEntry = new EntryImpl(key, function.apply(key));
            addEntry(newEntry);
            return (T) newEntry.value;
        }
    }

    protected Object doRemove(String key) {
        EntryImpl entry = findEntry(key);
        if (entry == null) {
            return null;
        } else {
            removeEntry(entry);
            return entry.value;
        }
    }

    protected boolean doRemoveExactly(String key, Object value) {
        EntryImpl entry = findEntry(key);
        if (entry != null && entry.value == value) {
            removeEntry(entry);
            return true;
        } else {
            return false;
        }
    }

    protected Object doRemoveOrCompute(String key, Function<String, Object> supplier) {
        EntryImpl entry = findEntry(key);
        if (entry == null) {
            return supplier.apply(key);
        } else {
            removeEntry(entry);
            return entry.value;
        }
    }

    protected boolean doContainsValue(Object value) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].value, value)) {
                return true;
            }
        }
        return false;
    }

    protected void doClear() {
        entries = new EntryImpl[entries.length];
        size = 0;
    }

    /*Implementations*/

    public Object get(String key) {
        Context.Entry entry = findEntry(key);
        return entry == null ? null : entry.getValue();
    }

    public Object getOrDefault(String key, Object defaultValue) {
        Context.Entry entry = findEntry(key);
        return entry == null ? defaultValue : entry.getValue();
    }

    public boolean containsKey(String key) {
        return findEntry(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Context)) {
            return false;
        }
        Context context = (Context) obj;
        if (context.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(context.get(entries[i].key), entries[i].value)) {
                return false;
            }
        }
        return true;
    }

    public Context.Keys keys() {
        return new SeparatedKeys(entries, size);
    }

    public Context.Values values() {
        return new SeparatedValues(entries, size);
    }

    public Context.Entries entries() {
        return new SeparatedEntries(entries, size);
    }
}
