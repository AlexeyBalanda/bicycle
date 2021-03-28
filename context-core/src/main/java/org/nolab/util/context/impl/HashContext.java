package org.nolab.util.context.impl;

import org.nolab.util.context.Context;

import java.io.*;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * Context with elements stored in hash table.
 * When number of mappings becomes greater than specified threshold,
 * hash table is extended.
 * When number of mappings becomes lesser than specified threshold,
 * hash table is compressed.
 * Extension and compression are defined by extend and compress multiplicators
 * respectively. New extend and compress thresholds are defined by
 * extend and compress factors respectively.
 * Null keys not allowed.
 * Not thread-safe.
 */
public class HashContext extends AbstractLIContext {

    private static final long serialVersionUID = 3159305992984666851L;

    /**
     * An {@link IterNode} with additional next and previous for hash table.
     */
    private static class HashTableIterNode extends IterNode {

        private static final long serialVersionUID = -174433486268052073L;

        transient volatile HashTableIterNode prevHT;
        transient volatile HashTableIterNode nextHT;

        public HashTableIterNode(String key, Object value) {
            super(key, value);
        }
    }

    public static final int DEFAULT_CAPACITY = 16;
    public static final float DEFAULT_EXTEND_FACTOR = 2;
    public static final float DEFAULT_COMPRESS_FACTOR = 0;
    public static final float DEFAULT_EXTEND_MULTIPLICATOR = 2;
    public static final float DEFAULT_COMPRESS_MULTIPLICATOR = 0.5f;

    private transient HashTableIterNode[] hashTable;
    private int capacity;

    private float extendFactor;
    private int extendThreshold;
    private float extendMul;

    private float compressFactor;
    private int compressThreshold;
    private float compressMul;

    /**
     * Constructor with full parameters set.
     *
     * @param supportNullValues null values option
     * @param capacity initial capacity of hash table
     * @param extendFactor extend factor
     * @param compressFactor compress factor
     * @param extendMul extend multiplicator
     * @param compressMul compress multiplicator
     * @throws IllegalArgumentException if size-management parameters are incorrect
     */
    public HashContext(boolean supportNullValues, int capacity,
                       float extendFactor, float compressFactor,
                       float extendMul, float compressMul) {
        super(false, supportNullValues);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid capacity: " + capacity);
        }
        this.capacity = capacity;
        size = 0;
        hashTable = new HashTableIterNode[capacity];
        resize(extendFactor, compressFactor, extendMul, compressMul);
    }

    /**
     * @see #HashContext(boolean, int, float, float, float, float)
     */
    public HashContext(int capacity,
                       float extendFactor, float compressFactor,
                       float extendMul, float compressMul) {
        this(true, capacity, extendFactor, compressFactor, extendMul, compressMul);
    }

    /**
     * @see #HashContext(boolean, int, float, float, float, float)
     */
    public HashContext(boolean supportNullValues, int capacity,
                       float extendFactor, float compressFactor) {
        this(supportNullValues, capacity, extendFactor, compressFactor, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(int, float, float, float, float)
     */
    public HashContext(int capacity,
                       float extendFactor, float compressFactor) {
        this(capacity, extendFactor, compressFactor, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(boolean, int, float, float, float, float)
     */
    public HashContext(boolean supportNullValues, int capacity, float extendFactor) {
        this(supportNullValues, capacity, extendFactor, DEFAULT_COMPRESS_FACTOR, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(int, float, float, float, float)
     */
    public HashContext(int capacity, float extendFactor) {
        this(capacity, extendFactor, DEFAULT_COMPRESS_FACTOR, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(boolean, int, float, float, float, float)
     */
    public HashContext(boolean supportNullValues, int capacity) {
        this(supportNullValues, capacity, DEFAULT_EXTEND_FACTOR, DEFAULT_COMPRESS_FACTOR, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(int, float, float, float, float)
     */
    public HashContext(int capacity) {
        this(capacity, DEFAULT_EXTEND_FACTOR, DEFAULT_COMPRESS_FACTOR, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(boolean, int, float, float, float, float)
     */
    public HashContext(boolean supportNullValues) {
        this(supportNullValues, DEFAULT_CAPACITY, DEFAULT_EXTEND_FACTOR, DEFAULT_COMPRESS_FACTOR, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * @see #HashContext(int, float, float, float, float)
     */
    public HashContext() {
        this(DEFAULT_CAPACITY, DEFAULT_EXTEND_FACTOR, DEFAULT_COMPRESS_FACTOR, DEFAULT_EXTEND_MULTIPLICATOR, DEFAULT_COMPRESS_MULTIPLICATOR);
    }

    /**
     * Create context with all mappings in specified source.
     *
     * @param source source map
     * @param supportNullValues {@code null} values option
     * @throws NullPointerException if source map is {@code null} or if source map
     *                              contains {@code null} values, but they are
     *                              not supported
     * @throws IllegalArgumentException if source map contains duplicated key
     */
    public HashContext(Map<String, Object> source, boolean supportNullValues) {
        this(supportNullValues, source.size());
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            checkNullKV(key, value);
            IterNode node = findNode(key);
            if (node == null) {
                addNewNode(key, value);
            } else {
                throw new IllegalArgumentException("Key duplication: " + key);
            }
        }
    }

    /**
     * @see #HashContext(Map, boolean)
     */
    public HashContext(Map<String, Object> source) {
        this(source, true);
    }

    private void putNode(HashTableIterNode node, boolean resizing) {
        if (!resizing && size + 1 > extendThreshold) {
            resize(extendMul, extendFactor, compressFactor);
        }
        int pos = node.key.hashCode() % hashTable.length;
        HashTableIterNode cur = hashTable[pos];
        hashTable[pos] = node;
        node.prevHT = null;
        node.nextHT = cur;
        if (cur != null) {
            cur.prevHT = node;
        }
    }

    private void resize(int capacity) {
        if (capacity == 0) {
            capacity = 1;
        }
        HashTableIterNode[] oldHT = hashTable;
        int oldCap = this.capacity;
        boolean error = true;
        try {
            this.hashTable = new HashTableIterNode[capacity];
            this.capacity = capacity;
            HashTableIterNode node = (HashTableIterNode) head.next;
            while (node != null) {
                putNode(node, true);
                node = (HashTableIterNode) node.next;
            }
            error = false;
        } finally {
            if (error) {
                this.hashTable = oldHT;
                this.capacity = oldCap;
            }
        }
    }

    private void resize(float multiplicator, float extendFactor, float compressFactor) {
        resize(Math.round(capacity * multiplicator));
        extendThreshold = Math.round(capacity * extendFactor);
        compressThreshold = (int) (capacity * compressFactor);
    }

    /**
     * Resize hash table with new size-management parameters.
     *
     * @param extendFactor new extend factor
     * @param compressFactor new compress factor
     * @param extendMul new extend multiplicator
     * @param compressMul new compress multiplicator
     * @throws IllegalArgumentException if any parameter is incorrect
     */
    public void resize(float extendFactor, float compressFactor, float extendMul, float compressMul) {
        if (extendFactor <= 1 || Float.isNaN(extendFactor)) {
            throw new IllegalArgumentException("Invalid extend factor " + extendFactor);
        }
        int extendThreshold = Math.round(capacity * extendFactor);
        if (extendThreshold == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too large extend factor " + extendFactor);
        }

        if (compressFactor < 0 || compressFactor >= 1 || Float.isNaN(compressFactor)) {
            throw new IllegalArgumentException("Invalid compress factor " + compressFactor);
        }
        int compressThreshold = (int) (capacity * compressFactor);

        int r = Math.round(extendMul);
        if (r <= 1 || Float.isNaN(extendMul)) {
            throw new IllegalArgumentException("Invalid extend multiplicator " + extendMul);
        }
        if (r == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too large extend multiplicator " + extendMul);
        }

        if (compressMul <= 0 || compressMul >= 1 || Float.isNaN(compressMul)) {
            throw new IllegalArgumentException("Invalid compress multiplicator " + compressMul);
        }

        if (size > extendThreshold) {
            resize(extendMul, extendFactor, compressFactor);
        } else if (size < compressThreshold) {
            resize(compressMul, extendFactor, compressFactor);
        } else {
            this.extendThreshold = extendThreshold;
            this.compressThreshold = compressThreshold;
        }

        this.extendFactor = extendFactor;
        this.compressFactor = compressFactor;
        this.extendMul = extendMul;
        this.compressMul = compressMul;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        write(s);
    }

    private void readObject(ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        hashTable = new HashTableIterNode[capacity];
        size = 0;
        int sz = s.readInt();
        for (int i = 0; i < sz; i++) {
            HashTableIterNode node = (HashTableIterNode) s.readObject();
            putNode(node);
            addNodeToHead(node);
            size++;
        }
    }

    @Override
    protected IterNode createNewNode(String key, Object value) {
        return new HashTableIterNode(key, value);
    }

    @Override
    protected void putNode(IterNode node) {
        putNode((HashTableIterNode)node, false);
    }

    @Override
    protected IterNode findNode(String key) {
        HashTableIterNode node = hashTable[key.hashCode() % hashTable.length];
        while (node != null) {
            if (key.equals(node.key)) {
                break;
            }
            node = node.nextHT;
        }
        return node;
    }

    @Override
    protected void removeNode(IterNode node) {
        if (size - 1 < compressThreshold) {
            resize(compressMul, extendFactor, compressFactor);
        }
        HashTableIterNode nodeHT = (HashTableIterNode) node;
        int pos = node.key.hashCode() % hashTable.length;
        if (hashTable[pos] == nodeHT) {
            hashTable[pos] = nodeHT.nextHT;
        } else {
            nodeHT.prevHT.nextHT = nodeHT.nextHT;
        }
        if (nodeHT.nextHT != null) {
            nodeHT.nextHT.prevHT = nodeHT.prevHT;
        }
        nodeHT.remove();
    }

    @Override
    public Context clear() {
        Arrays.fill(hashTable, null);
        head.next = null;
        size = 0;
        if (size < compressThreshold) {
            resize(compressMul, extendFactor, compressFactor);
        }
        return this;
    }

    @Override
    public Context copy() {
        HashContext copy = new HashContext(supportNullValues, capacity,
                extendFactor, compressFactor,
                extendMul, compressMul);
        IterNode node = head.next;
        while (node != null) {
            copy.addNewNode(node.key, node.value);
            node = node.next;
        }
        return copy;
    }

    @Override
    public Context copy(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        HashContext copy = new HashContext(supportNullValues, capacity,
                extendFactor, compressFactor,
                extendMul, compressMul);
        IterNode node = head.next;
        while (node != null) {
            if (criteria.test(node.key, node.value)) {
                copy.addNewNode(node.key, node.value);
            }
            node = node.next;
        }
        return copy;
    }
}
