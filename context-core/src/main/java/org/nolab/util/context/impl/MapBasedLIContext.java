package org.nolab.util.context.impl;

import org.nolab.util.context.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Context, based on encapsulated {@link Map <String, Object>}.
 * Has a {@link LinkedIterator}.
 */
public class MapBasedLIContext extends AbstractLIContext {

    private static final long serialVersionUID = -267705991293185000L;

    private static final int DEFAULT_CAPACITY = 16;

    private final Map<String, Object> map;
    private final Supplier<Map<String, Object>> defaultMapSupplier;
    private final IntFunction<Map<String, Object>> capacityMapSupplier;

    /**
     * Constructor with {@code null} keys and values option, default and capacity map supplier
     * and initial capacity.
     * Suppliers are needed to create copies of maps.
     *
     * @param supportNullKeys {@code null} keys support option
     * @param supportNullValues {@code null} keys and values support option
     * @param defaultMapSupplier supplier of map with its default capacity
     * @param capacityMapSupplier  supplier of map with given capacity
     * @param initialCapacity the initial capacity
     * @throws NullPointerException if default and capacity suppliers both are {@code null}
     * @throws IllegalArgumentException if initial capacity is negative
     */
    public MapBasedLIContext(boolean supportNullKeys, boolean supportNullValues,
                             Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier,
                             int initialCapacity) {
        super(supportNullKeys, supportNullValues);
        this.defaultMapSupplier = defaultMapSupplier;
        this.capacityMapSupplier = capacityMapSupplier;
        if (capacityMapSupplier == null && defaultMapSupplier == null) {
            throw new NullPointerException("Need at least one supplier");
        }
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity: " + initialCapacity);
        }
        if (capacityMapSupplier != null) {
            map = capacityMapSupplier.apply(initialCapacity);
        } else {
            map = defaultMapSupplier.get();
        }
    }

    /**
     * Constructor with default and capacity map suppliers and
     * support {@code null} keys and values options.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedLIContext(boolean supportNullKeys, boolean supportNullValues,
                             Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier) {
        this(supportNullKeys, supportNullValues, defaultMapSupplier, capacityMapSupplier, DEFAULT_CAPACITY);
    }

    /**
     * Constructor with default and capacity map suppliers and
     * no {@code null} keys and values supported.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedLIContext(Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier) {
        this(false, false, defaultMapSupplier, capacityMapSupplier);
    }

    /**
     * Constructor with only default map supplier and
     * no {@code null} keys and values supported.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedLIContext(Supplier<Map<String, Object>> defaultMapSupplier) {
        this(false, false, defaultMapSupplier, null);
    }

    /**
     * Constructor with only capacity map supplier and
     * no {@code null} keys and values supported.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedLIContext(IntFunction<Map<String, Object>> capacityMapSupplier) {
        this(false, false, null, capacityMapSupplier);
    }

    /**
     * Constructor with {@code null} keys and values option, default and capacity map supplier
     * and source map. All values from source map are copied.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, int)
     *
     * @throws NullPointerException if source map is {@code null}
     * @throws IllegalArgumentException if source map contains duplicated key
     */
    public MapBasedLIContext(boolean supportNullKeys, boolean supportNullValues,
                             Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier,
                             Map<String, Object> source) {
        super(supportNullKeys, supportNullValues);
        this.defaultMapSupplier = defaultMapSupplier;
        this.capacityMapSupplier = capacityMapSupplier;
        if (defaultMapSupplier == null && capacityMapSupplier == null) {
            throw new NullPointerException("Need at least one supplier");
        }
        if (source == null) {
            throw new NullPointerException("Source is null");
        }
        if (defaultMapSupplier != null) {
            map = defaultMapSupplier.get();
        } else {
            map = capacityMapSupplier.apply(source.size());
        }

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
     * Constructor with default and capacity map supplier,
     * source map and no {@code null} keys and values supported.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, Map)
     */
    public MapBasedLIContext(Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier,
                             Map<String, Object> source) {
        this(false, false, defaultMapSupplier, capacityMapSupplier, source);
    }

    /**
     * Constructor with only default map supplier,
     * source map and no {@code null} keys and values supported.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, Map)
     */
    public MapBasedLIContext(Supplier<Map<String, Object>> defaultMapSupplier,
                             Map<String, Object> source) {
        this(false, false, defaultMapSupplier, null, source);
    }

    /**
     * Constructor with only capacity map supplier,
     * source map and no {@code null} keys and values supported.
     *
     * @see #MapBasedLIContext(boolean, boolean, Supplier, IntFunction, Map)
     */
    public MapBasedLIContext(IntFunction<Map<String, Object>> capacityMapSupplier,
                             Map<String, Object> source) {
        this(false, false, null, capacityMapSupplier, source);
    }

    @Override
    protected void putNode(IterNode node) {
        map.put(node.key, node);
    }
    
    @Override
    protected IterNode findNode(String key) {
        return (IterNode) map.get(key);
    }

    @Override
    protected void removeNode(IterNode node) {
        map.remove(node.key);
        node.remove();
    }

    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        size = 0;
        for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
            IterNode node = (IterNode) mapEntry.getValue();
            addNodeToHead(node);
            size++;
        }
    }

    @Override
    public Context clear() {
        map.clear();
        return super.clear();
    }

    @Override
    public Context copy() {
        MapBasedLIContext copy = new MapBasedLIContext(supportNullKeys, supportNullValues,
                defaultMapSupplier, capacityMapSupplier, size);
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
        MapBasedLIContext copy = new MapBasedLIContext(supportNullKeys, supportNullValues,
                defaultMapSupplier, capacityMapSupplier, size);
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
