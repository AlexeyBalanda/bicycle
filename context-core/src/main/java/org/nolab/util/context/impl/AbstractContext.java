package org.nolab.util.context.impl;

import org.nolab.util.context.Context;

import java.io.Serializable;
import java.util.*;

/**
 * Basis for {@link Context} implementations. Provides serializability.
 * Selectively support or not support {@code null} keys or values.
 * Provides "for-each" loop tools.
 * Provides {@link #equals(Object)}.
 */
public abstract class AbstractContext implements Context, Serializable {

    /**
     * A simple immutable {@link Entry}
     * implementation.
     */
    protected static class SeparatedEntry implements Entry, Serializable {

        private static final long serialVersionUID = 1645588339409370351L;

        private final String key;
        private final Object value;

        /**
         * Just key and value.
         *
         * @param key key
         * @param value value
         */
        public SeparatedEntry(String key, Object value) {
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
     * Iterator, separated from original storage. Generated and filled,
     * it does not reflect original storage state.
     * It may be useful for small set of elements, frequently accessed by
     * multiple threads.
     */
    protected static class SeparatedIterator<E> implements Iterator<E>, Iterable<E> {

        /**
         * All elements, added to iterator.
         */
        private Object[] elements;

        /**
         * Index of element to be returned by {@link #next()}
         */
        private int ind = 0;

        /**
         * Index of last element.
         */
        private int end = -1;

        /**
         * Create iterator with initial capacity.
         *
         * @param capacity initial capacity
         */
        public SeparatedIterator(int capacity) {
            elements = new Object[capacity];
        }

        /**
         * Add new element.
         *
         * @param element new element
         */
        public void add(E element) {
            int e = end + 1;
            if (e == elements.length) {
                Object[] newElements = new Object[e + 1];
                System.arraycopy(elements, 0, newElements, 0, e);
                elements = newElements;
            }
            elements[e] = element;
            end = e;
        }

        @Override
        public boolean hasNext() {
            return ind <= end;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (ind > end) {
                throw new NoSuchElementException();
            }
            return (E) elements[ind++];
        }

        @Override
        public Iterator<E> iterator() {
            return this;
        }
    }

    /**
     * Separated iterator for nolab.util.context.context keys.
     */
    protected static class SeparatedKeys extends SeparatedIterator<String> implements Keys {

        /**
         * @param capacity initial capacity
         */
        public SeparatedKeys(int capacity) {
            super(capacity);
        }
    }

    /**
     * Separated iterator for nolab.util.context.context values.
     */
    protected static class SeparatedValues extends SeparatedIterator<Object> implements Values {

        /**
         * @param capacity initial capacity
         */
        public SeparatedValues(int capacity) {
            super(capacity);
        }
    }

    /**
     * Separated iterator for  nolab.util.context.context entries.
     */
    protected static class SeparatedEntries extends SeparatedIterator<Entry> implements Entries {

        /**
         * @param capacity initial capacity
         */
        public SeparatedEntries(int capacity) {
            super(capacity);
        }
    }

    /**
     * An iteration node, containing key, value and entry.
     * <p>May be chained with previous and next node.
     * <p>May be removed from chain.
     */
    protected static class IterNode implements Serializable {

        private static final long serialVersionUID = -5275051481394640180L;

        public String key;
        public Object value;
        public Entry entry;

        /**
         * Previous node.
         */
        public transient volatile IterNode prev;

        /**
         * Next node.
         */
        public transient volatile IterNode next;

        /**
         * Construct node with key and value.
         * Entry will  be added automatically.
         *
         * @param key key
         * @param value value
         */
        public IterNode(String key, Object value) {
            this.key = key;
            this.value = value;
            this.entry = new INEntry(this);
        }

        /**
         * Remove node from chain.
         * Next and previous nodes, if exists
         * will lose reference to it.
         */
        public void remove() {
            if (prev != null) {
                prev.next = next;
            }
            if (next != null) {
                next.prev = prev;
            }
        }
    }

    /**
     * Entry impl for {@link IterNode}.
     */
    protected static class INEntry implements Entry, Serializable {

        private static final long serialVersionUID = 5457510222217726569L;

        /**
         * Node, this entry is built in.
         */
        private final IterNode in;

        /**
         * Construct entry with specified node.
         *
         * @param in specified node
         */
        public INEntry(IterNode in) {
            this.in = in;
        }

        @Override
        public String getKey() {
            return in.key;
        }

        @Override
        public Object getValue() {
            return in.value;
        }
    }

    /**
     * Iterator, that linked to its context and reflects its state.
     */
    protected static class LinkedIterator {

        private volatile IterNode cur;
        private volatile IterNode next;

        /**
         * Construct iterator with zero node.
         * Pointer is set on zero node. Zero node
         * never returned by {@link #nextIN()}
         *
         * @param zero zero node
         * @throws NullPointerException if zero node is {@code null}
         */
        public LinkedIterator(IterNode zero) {
            this.cur = Objects.requireNonNull(zero);
            next = cur.next;
        }

        /**
         * Answers, is next node available.
         *
         * @return {@code true} if next node is available
         */
        public boolean hasNext() {
            next = cur.next;
            return next != null;
        }

        /**
         * Returns next node, if available.
         *
         * @return next node
         * @throws NoSuchElementException if no next node available
         */
        private IterNode nextIN() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            IterNode node = next;
            cur = next;
            next = cur.next;
            return node;
        }

        /**
         * Key from next node, if available.
         *
         * @return key from next node
         * @throws NoSuchElementException if no next node available
         */
        protected String nextKey() {
            return nextIN().key;
        }

        /**
         * Value from next node, if available.
         *
         * @return value from next node
         * @throws NoSuchElementException if no next node available
         */
        protected Object nextValue() {
            return nextIN().value;
        }

        /**
         * Entry from next node, if available.
         *
         * @return entry from next node
         * @throws NoSuchElementException if no next node available
         */
        protected Entry nextEntry() {
            return nextIN().entry;
        }
    }

    /**
     * Linked iterator for nolab.util.context.context keys.
     */
    protected static class LinkedKeys extends LinkedIterator implements Iterator<String>, Keys {

        /**
         * @param zero zero node
         */
        public LinkedKeys(IterNode zero) {
            super(zero);
        }

        @Override
        public String next() {
            return nextKey();
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }
    }

    /**
     * Linked iterator for nolab.util.context.context values.
     */
    protected static class LinkedValues extends LinkedIterator implements Iterator<Object>, Values {

        /**
         * @param zero zero node
         */
        public LinkedValues(IterNode zero) {
            super(zero);
        }

        @Override
        public Object next() {
            return nextValue();
        }

        @Override
        public Iterator<Object> iterator() {
            return this;
        }
    }

    /**
     * Linked iterator for nolab.util.context.context entries.
     */
    protected static class LinkedEntries extends LinkedIterator implements Iterator<Entry>, Entries {

        /**
         * @param zero zero node
         */
        public LinkedEntries(IterNode zero) {
            super(zero);
        }

        @Override
        public Entry next() {
            return nextEntry();
        }

        @Override
        public Iterator<Entry> iterator() {
            return this;
        }
    }

    protected static final String NULL_KEYS_NOT_SUPPORTED = "Null keys not supported";
    protected static final String NULL_VALUES_NOT_SUPPORTED = "Null values not supported";

    /**
     * Are {@code null} keys supported.
     */
    protected volatile boolean supportNullKeys = false;

    /**
     * Are {@code null} values supported.
     */
    protected volatile boolean supportNullValues = true;

    /**
     * Default constructor.
     */
    public AbstractContext() {
    }

    /**
     * Constructor, that defines supportive
     * or not supportive null keys or values.
     *
     * @param supportNullKeys {@code true} if {@code null} keys supported
     * @param supportNullValues {@code true} if {@code null} values supported
     */
    public AbstractContext(boolean supportNullKeys, boolean supportNullValues) {
        this.supportNullKeys = supportNullKeys;
        this.supportNullValues = supportNullValues;
    }

    /**
     * Check key for {@code null} with regard to supportive.
     *
     * @param key checked key
     * @throws NullPointerException if {@code key} is {@code null}
     *         and {@code null} keys are not supported
     */
    protected void checkNullKey(String key) {
        if (key == null && !supportNullKeys) {
            throw new NullPointerException(NULL_KEYS_NOT_SUPPORTED);
        }
    }

    /**
     * Check key for {@code null} with regard to supportive.
     *
     * @param value checked value
     * @throws NullPointerException if {@code value} is {@code null}
     *         and {@code null} values are not supported
     */
    protected void checkNullValue(Object value) {
        if (value == null && !supportNullValues) {
            throw new NullPointerException(NULL_VALUES_NOT_SUPPORTED);
        }
    }

    /**
     * Check key and value for {@code null} with regard to supportive.
     *
     * @param key checked key
     * @param value checked value
     * @throws NullPointerException if {@code key} or {@code value} is {@code null}
     *         and {@code null} keys or values are not supported
     */
    protected void checkNullKV(String key, Object value) {
        if (key == null && !supportNullKeys) {
            throw new NullPointerException(NULL_KEYS_NOT_SUPPORTED);
        }
        if (value == null && !supportNullValues) {
            throw new NullPointerException(NULL_VALUES_NOT_SUPPORTED);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Context)) {
            return false;
        }
        Context cobj = (Context) obj;
        if (cobj.size() != size()) {
            return false;
        }
        for (Entry entry : entries()) {
            if (!Objects.equals(entry.getValue(), cobj.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }
}
