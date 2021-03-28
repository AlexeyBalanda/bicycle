package org.nolab.util.context.impl;

import org.nolab.util.context.Context;

import java.util.*;
import java.util.function.*;

/**
 * Context, based on encapsulated {@link Map<String, Object>}.
 * Has a {@link SeparatedIterator}.
 * Not thread-safe.
 */
public class MapBasedSIContext extends AbstractContext {

    private static final long serialVersionUID = 7126896132763235914L;

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
    public MapBasedSIContext(boolean supportNullKeys, boolean supportNullValues,
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
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedSIContext(boolean supportNullKeys, boolean supportNullValues,
                             Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier) {
        this(supportNullKeys, supportNullValues, defaultMapSupplier, capacityMapSupplier, DEFAULT_CAPACITY);
    }

    /**
     * Constructor with default and capacity map suppliers and
     * no {@code null} keys and values supported.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedSIContext(Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier) {
        this(false, false, defaultMapSupplier, capacityMapSupplier);
    }

    /**
     * Constructor with only default map supplier and
     * no {@code null} keys and values supported.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedSIContext(Supplier<Map<String, Object>> defaultMapSupplier) {
        this(false, false, defaultMapSupplier, null);
    }

    /**
     * Constructor with only capacity map supplier and
     * no {@code null} keys and values supported.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, int)
     */
    public MapBasedSIContext(IntFunction<Map<String, Object>> capacityMapSupplier) {
        this(false, false, null, capacityMapSupplier);
    }

    /**
     * Constructor with {@code null} keys and values option, default and capacity map supplier
     * and source map. All values from source map are copied.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, int)
     *
     * @throws NullPointerException if source map is {@code null}
     * @throws IllegalArgumentException if source map contains duplicated key
     */
    public MapBasedSIContext(boolean supportNullKeys, boolean supportNullValues,
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
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Key duplication: " + key);
            }
            map.put(key, value);
        }
    }

    /**
     * Constructor with default and capacity map supplier,
     * source map and no {@code null} keys and values supported.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, Map)
     */
    public MapBasedSIContext(Supplier<Map<String, Object>> defaultMapSupplier,
                             IntFunction<Map<String, Object>> capacityMapSupplier,
                             Map<String, Object> source) {
        this(false, false, defaultMapSupplier, capacityMapSupplier, source);
    }

    /**
     * Constructor with only default map supplier,
     * source map and no {@code null} keys and values supported.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, Map)
     */
    public MapBasedSIContext(Supplier<Map<String, Object>> defaultMapSupplier,
                             Map<String, Object> source) {
        this(false, false, defaultMapSupplier, null, source);
    }

    /**
     * Constructor with only capacity map supplier,
     * source map and no {@code null} keys and values supported.
     *
     * @see #MapBasedSIContext(boolean, boolean, Supplier, IntFunction, Map)
     */
    public MapBasedSIContext(IntFunction<Map<String, Object>> capacityMapSupplier,
                             Map<String, Object> source) {
        this(false, false, null, capacityMapSupplier, source);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Context {");
        for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
            String key = mapEntry.getKey();
            Object value = mapEntry.getValue();
            sb.append(key).append("=");
            if (value == this) {
                sb.append("(this Context)");
            } else {
                sb.append(value);
            }
            sb.append("; ");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Object put(String key, Object value) {
        checkNullKV(key, value);
        return map.put(key, value);
    }

    @Override
    public Object get(String key) {
        checkNullKey(key);
        return map.get(key);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        checkNullKV(key, value);
        return map.putIfAbsent(key, value);
    }

    @Override
    public Object putIfKeyAbsent(String key, Object value) {
        checkNullKV(key, value);
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return map.put(key, value);
    }

    @Override
    public Object getOrDefault(String key, Object defaultValue) {
        checkNullKey(key);
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return defaultValue;
    }

    @Override
    public Object getOrCompute(String key, Function<String, Object> function) {
        checkNullKey(key);
        Objects.requireNonNull(function);
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return function.apply(key);
    }

    @Override
    public Object getOrComputeAndPut(String key, Function<String, Object> function) {
        checkNullKey(key);
        Objects.requireNonNull(function);
        if (map.containsKey(key)) {
            return map.get(key);
        }
        Object newVal = function.apply(key);
        checkNullValue(newVal);
        map.put(key, newVal);
        return newVal;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        checkNullKey(key);
        Objects.requireNonNull(function);
        Object value = map.get(key);
        if (valueType.isInstance(value)) {
            return (T) value;
        }
        T newVal = function.apply(key);
        checkNullValue(newVal);
        map.put(key, newVal);
        return newVal;
    }

    @Override
    public Object remove(String key) {
        checkNullKey(key);
        return map.remove(key);
    }

    @Override
    public boolean removeExactly(String key, Object value) {
        checkNullKey(key);
        Object val = map.get(key);
        boolean same = val == value;
        if (same) {
            map.remove(key);
        }
        return same;
    }

    @Override
    public Object removeOrCompute(String key, Function<String, Object> function) {
        checkNullKey(key);
        Objects.requireNonNull(function);
        if (map.containsKey(key)) {
            return map.remove(key);
        }
        return function.apply(key);
    }

    @Override
    public boolean containsKey(String key) {
        checkNullKey(key);
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        checkNullValue(value);
        return map.containsValue(value);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
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
        if (cobj.size() != map.size()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!Objects.equals(entry.getValue(), cobj.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Context clear() {
        map.clear();
        return this;
    }

    @Override
    public Keys keys() {
        SeparatedKeys keys = new SeparatedKeys(map.size());
        for (String key : map.keySet()) {
            keys.add(key);
        }
        return keys;
    }

    @Override
    public Values values() {
        SeparatedValues values = new SeparatedValues(map.size());
        for (Object value : map.values()) {
            values.add(value);
        }
        return values;
    }

    @Override
    public Entries entries() {
        SeparatedEntries entries = new SeparatedEntries(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            entries.add(new SeparatedEntry(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    @Override
    public Context copy() {
        return new MapBasedSIContext(supportNullKeys, supportNullValues,
                defaultMapSupplier, capacityMapSupplier, map);
    }

    @Override
    public Context copy(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        MapBasedSIContext copy = new MapBasedSIContext(supportNullKeys, supportNullValues,
                defaultMapSupplier, capacityMapSupplier, map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (criteria.test(key, value)) {
                copy.map.put(key, value);
            }
        }
        return copy;
    }

    @Override
    public Context filter(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        List<String> keysToRemove = new LinkedList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!criteria.test(key, entry.getValue())) {
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            map.remove(key);
        }
        return this;
    }
}
