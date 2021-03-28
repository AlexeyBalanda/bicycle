package org.nolab.util.context;

import java.util.*;
import java.util.function.*;

import static org.nolab.util.context.Context.ReplaceRule.PUT;

/**
 * An object that maps string keys to any reference type values.
 * <p>It is intended to be used like alternative for {@link java.util.Map}
 * with key type {@link java.lang.String} and value type {@link java.lang.Object}.
 * Key type restricted to {@link java.lang.String} to ensure key immutability,
 * provide type-safety and support human-readability.
 * Value type extended to {@link java.lang.Object} to make this object
 * universal storage.
 * <p>This interface designed to be easy suitable for thread-safe implementations.
 */
public interface Context {

    /**
     * "For-each" loop tool for keys.
     */
    interface Keys extends Iterable<String> {

        /**
         * Not supported by default.
         */
        default Spliterator<String> spliterator() {
            throw new UnsupportedOperationException("spliterator");
        }
    }

    /**
     * "For-each" loop tool for values.
     */
    interface Values extends Iterable<Object> {

        /**
         * Not supported by default.
         */
        default Spliterator<Object> spliterator() {
            throw new UnsupportedOperationException("spliterator");
        }
    }

    /**
     * A key-value pair
     */
    interface Entry {

        /**
         * Key of this entry.
         *
         * @return key
         */
        String getKey();

        /**
         * Value of this entry.
         *
         * @return value
         */
        Object getValue();
    }

    /**
     * "For-each" loop tool for entries.
     */
    interface Entries extends Iterable<Entry> {

        /**
         * Not supported by default.
         */
        default Spliterator<Entry> spliterator() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * A rule for resolving key collisions when exchanging
     * key-value pairs between {@code Context} and {@code Context}
     * or between {@code Context} and {@code Map}
     * in {@code copyTo()}, {@code copyFrom()}, {@code drainTo()},
     * {@code drainFrom()} methods.
     */
    enum ReplaceRule {

        /**
         * @see #put(String, Object)
         */
        PUT,

        /**
         * @see #putIfAbsent(String, Object)
         */
        PUT_IF_ABSENT,

        /**
         * @see #putIfKeyAbsent(String, Object)
         */
        PUT_IF_KEY_ABSENT
    }

    /**
     * Associates the specified value with the specified key in this context.
     * If the context previously contained a mapping for
     * the key, the old value is replaceRuled by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key};
     *         a {@code null} return can also indicate that the context
     *         previously associated {@code null} with {@code key},
     *         if the implementation supports {@code null} values
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if value is {@code null} and implementation
     *                              does not support {@code null} values
     */
    Object put(String key, Object value);

    /**
     * Equals to {@code put(value.getClass().getName(), value)}.
     */
    default Object put(Object value) {
        return put(value.getClass().getName(), value);
    }

    /**
     * Returns the value to, associated to specified key,
     * or {@code null} if this context contains no mapping for the key.
     * <p>If this context permits null values, then a return value of
     * {@code null} does not necessarily indicate that the context
     * contains no mapping for the key; it's also possible that the context
     * explicitly maps the key to {@code null}. The {@link #containsKey(String)}
     * operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this context contains no mapping for the key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    Object get(String key);

    /**
     * Returns the value, associated to specified key,
     * only if this context contains mapping for the key
     * and associated value is assignment-compatible to the specified
     * value type. Otherwise returns {@code null}.
     *
     * @param <T> expected type of associated value
     * @param key the key whose associated value is to be returned
     * @param valueType class object of expected value type
     * @return the value, assignment-compatible to the specified
     *         value type, to which the specified key is mapped, or
     *         {@code null} if this context contains no mapping for the key
     *         or contains assignment-incompatible associated value
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <T> T get(String key, Class<T> valueType) {
        Objects.requireNonNull(valueType);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            return (T) value;
        } else {
            return null;
        }
    }

    /**
     * Equals to {@code get(valueType.getName(), valueType)}.
     */
    default <T> T get(Class<T> valueType) {
        return get(valueType.getName(), valueType);
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}), associates it with the specified value and returns
     * {@code null}, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key;
     *         a {@code null} return can also indicate that the map
     *         previously associated {@code null} with the key,
     *         if the implementation supports null values
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if value is {@code null} and implementation
     *                              does not support {@code null} values
     */
    Object putIfAbsent(String key, Object value);

    /**
     * Equals to {@code putIfAbsent(value.getClass().getName(), value)}.
     */
    default Object putIfAbsent(Object value) {
        return putIfAbsent(value.getClass().getName(), value);
    }

    /**
     * Associates specified key to the specified value and returns {@code null},
     * if context contains no specified key. Otherwise returns current value.
     * <p>Unlike {@link #putIfAbsent(String, Object)} {@code null} value,
     * associated with the key (if supported), will not be replaced and
     * will be returned as current value. The {@link #containsKey(String)}
     * operation may be used to distinguish these two cases.
     *
     * @param key key, specified value is to be associated to
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if value is {@code null} and implementation
     *                              does not support {@code null} values
     */
    Object putIfKeyAbsent(String key, Object value);

    /**
     * Equals to {@code putIfKeyAbsent(value.getClass().getName(), value)}.
     */
    default Object putIfKeyAbsent(Object value) {
        return putIfKeyAbsent(value.getClass().getName(), value);
    }

    /**
     * Returns the value to which the specified key is mapped (even {@code null}),
     * or {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    Object getOrDefault(String key, Object defaultValue);

    /**
     * Returns the value to which the specified key is associated,
     * only if this context contains mapping for the key
     * and associated value is assignment-compatible to the specified
     * value type and nonnull. Otherwise returns {@code defaultValue}.
     *
     * @param <T> expected type of associated value
     * @param key key the key whose associated value is to be returned
     * @param valueType class object of expected value type
     * @param defaultValue the default mapping of the key
     * @return the value, assignment-compatible to the specified
     *         value type, to which the specified key is mapped, or
     *         {@code defaultValue} if this context contains no mapping
     *         for the key or contains assignment-incompatible associated
     *         value or contains {@code null}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <T> T getOrDefault(String key, Class<T> valueType, T defaultValue) {
        Objects.requireNonNull(valueType);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            return (T) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Equals to {@code getOrDefault(valueType.getName(), valueType, defaultValue)}.
     */
    default <T> T getOrDefault(Class<T> valueType, T defaultValue) {
        return getOrDefault(valueType.getName(), valueType, defaultValue);
    }

    /**
     * Returns the value to which the specified key is mapped (even {@code null}).
     * If context contains no mapping for specified key return value is computed
     * by specified function with key argument.
     * 
     * @param key key the key whose associated value is to be returned
     * @param function function to compute default value
     * @return the value to which the specified key is mapped, or value, computed
     *         by function with key argument if this map contains no mapping for the key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code function} is {@code null}
     */
    Object getOrCompute(String key, Function<String, Object> function);

    /**
     * Returns the value to which the specified key is mapped.
     * If context contains no mapping for specified key or associated value
     * is {@code null} or assignment-incompatible to specified value type
     * return value is computed by specified function with key argument.
     * 
     * @param <T> expected type of associated value
     * @param key key key the key whose associated value is to be returned
     * @param valueType class object of expected value type
     * @param function function to compute default value
     * @return the value to which the specified key is mapped, or value, computed
     *         by function with key argument if this map contains no mapping for
     *         the key or contains assignment-incompatible associated value
     *         or contains {@code null}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} or {@code function} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <T> T getOrCompute(String key, Class<T> valueType, Function<String, T> function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(valueType);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            return (T) value;
        } else {
            return function.apply(key);
        }
    }

    /**
     * Equals to {getOrCompute(valueType.getName(), valueType, function)}.
     */
    default <T> T getOrCompute(Class<T> valueType, Function<String, T> function) {
        return getOrCompute(valueType.getName(), valueType, function);
    }

    /**
     * Returns the value to which the specified key is mapped (even {@code null}).
     * If context contains no mapping for specified key return value is computed
     * by specified function with key argument.
     * Computed value becomes associated to specified key.
     *
     * @param key key the key whose associated value is to be returned
     * @param function function to compute default value
     * @return the value to which the specified key is mapped, or value, supplied
     *         by function if this map contains no mapping for the key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} or {@code function} is {@code null}
     * @throws NullPointerException if function produces {@code null} value and implementation
     *                              does not support {@code null} values
     */
    Object getOrComputeAndPut(String key, Function<String, Object> function);

    /**
     * Returns the value to which the specified key is mapped.
     * If context contains no mapping for specified key or associated value
     * is {@code null} or assignment-incompatible to specified value type,
     * returns value, supplied by specified function.
     * Supplied value becomes associated to specified key.
     *
     * @param <T> expected type of associated value
     * @param key key key the key whose associated value is to be returned
     * @param valueType class object of expected value type
     * @param function function to compute default value
     * @return the value to which the specified key is mapped, or value, supplied
     *         by function if this map contains no mapping for the key or contains
     *         assignment-incompatible associated value or contains {@code null}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} or {@code function} is {@code null}
     * @throws NullPointerException if function produces {@code null} value and implementation
     *                              does not support {@code null} values
     */
    <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function);

    /**
     * Equals to {getOrComputeAndPut(valueType.getName(), valueType, function)}.
     */
    default <T> T getOrComputeAndPut(Class<T> valueType, Function<String, T> function) {
        return getOrComputeAndPut(valueType.getName(), valueType, function);
    }

    /**
     * Removes the mapping for a key from this context and return
     * previously associated value if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    Object remove(String key);

    /**
     * Removes the entry for the specified key only if it is currently
     * mapped exactly to the specified value, that means
     * {@code get(key) == value} is true.
     * This method may be very useful in non-atomic removing operations.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if entry was removed or {@code false} otherwise
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    boolean removeExactly(String key, Object value);

    /**
     * Removes the entry for the specified key only if it is currently
     * mapped to the specified value.
     *
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if entry was removed or {@code otherwise}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    default boolean remove(String key, Object value) {
        Object curVal = get(key);
        if (Objects.equals(value, curVal)) {
            removeExactly(key, curVal);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the mapping for a key from this context and return
     * previously associated value only if it is present and associated
     * value is nonnull and assignment-compatible to the specified
     * value type. Otherwise return {@code null}.
     *
     * @param <T> expected type of associated value
     * @param key key whose mapping is to be removed from the map
     * @param valueType class object of expected value type
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key} or
     *         associated value is not assignment-compatible to the
     *         specified value type is {@code null}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default  <T> T removeOfType(String key, Class<T> valueType) {
        Objects.requireNonNull(valueType);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            removeExactly(key, value);
            return (T) value;
        } else {
            return null;
        }
    }

    /**
     * Removes the mapping for a key from this context and return
     * previously associated value if it is present.
     * Otherwise return {@code defaultValue}.
     *
     * @param key key whose mapping is to be removed from the map
     * @param defaultValue the default mapping of the key
     * @return the previous value associated with {@code key}, or
     *         {@code defaultValue} if there was no mapping for {@code key}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    default Object removeOrGetDefault(String key, Object defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        } else {
            removeExactly(key, value);
            return value;
        }
    }

    /**
     * Removes the mapping for a key from this context and return
     * previously associated value only if it is present and associated value
     * is nonnull and assignment-compatible to the specified value type.
     * Otherwise return {@code defaultValue}.
     *
     * @param <T> expected type of associated value
     * @param key key whose mapping is to be removed from the map
     * @param valueType class object of expected value type
     * @param defaultValue the default mapping of the key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key} or
     *         associated value is non assignment-compatible to the
     *         specified value type
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default  <T> T removeOrGetDefault(String key, Class<T> valueType, T defaultValue) {
        Objects.requireNonNull(valueType);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            removeExactly(key, value);
            return (T) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Removes the mapping for a key from this context and return
     * previously associated value if it is present (even {@code null}).
     * Otherwise value is computed by specified function with key argument.
     *
     * @param key key whose mapping is to be removed from the context
     * @param function function to compute default value
     * @return the previous value associated with {@code key} or value, computed
     *         by function with key argument, if this context contains no mapping
     *         for the key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code function} is {@code null}
     */
    Object removeOrCompute(String key, Function<String, Object> function);

    /**
     * Removes the mapping for a key from this context and return
     * previously associated value if it is present, nonnull and
     * assignment-compatible to the specified value type.
     * Otherwise returns value, computed by specified function
     * with key argument.
     *
     * @param <T> expected type of associated value
     * @param key key key the key whose associated value is to be returned
     * @param valueType class object of expected value type
     * @param function function to compute default value
     * @return the value to which the specified key is mapped, or value, computed
     *         by function with key argument if this context contains no mapping
     *         for the key or contains assignment-incompatible associated value
     *         or contains {@code null}
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     * @throws NullPointerException if {@code valueType} or {@code function} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <T> T removeOrCompute(String key, Class<T> valueType, Function<String, T> function) {
        Objects.requireNonNull(valueType);
        Objects.requireNonNull(function);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            removeExactly(key, value);
        } else {
            value = function.apply(key);
        }
        return (T) value;
    }

    /**
     * Returns {@code true} if this context contains a mapping for the specified key.
     *
     * @param key key whose presence in this context is to be tested
     * @return {@code true} if this context contains a mapping for the specified key
     * @throws NullPointerException if key is {@code null} and implementation
     *                              does not support {@code null} keys
     */
    boolean containsKey(String key);

    /**
     * Returns {@code true} if this context maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this context is to be tested
     * @return {@code true} if this context maps one or more keys to the
     *         specified value
     * @throws NullPointerException if value is {@code null} and implementation
     *                              does not support {@code null} values
     */
    boolean containsValue(Object value);

    /**
     * Number of key-value mappings in this context.
     *
     * @return number of key-value mappings
     */
    int size();

    /**
     * Is this context contains no key-value mappings.
     *
     * @return {@code true} if this context contains no mappings
     */
    boolean isEmpty();

    /**
     * Compares this context to specified object for equality.
     * Returns {@code true} if specified object also {@link Context}
     * and contains identical mapping set.
     * <p>If this or another context is accessed by multiple threads,
     * returned value actuality is not guaranteed.
     *
     * @param obj object to be compared for equality with this context
     * @return {@code true} if specified object equals to this context
     */
    boolean equals(Object obj);

    /**
     * Removes all of the mappings from this context.
     * The context will be empty after this call returns.
     *
     * @return this
     */
    Context clear();

    /**
     * Iterable of keys.
     *
     * @return keys for-loop
     */
    Keys keys();

    /**
     * Iterable of values.
     *
     * @return values for-loop
     */
    Values values();

    /**
     * Iterable of entries.
     *
     * @return entries for-loop
     */
    Entries entries();

    /**
     * Returns {@code Context} with copied key-value mapping set.
     * <p>Changes in original context must not be reflected in returned context
     * and visa-versa.
     *
     * @return copy of this context
     */
    Context copy();

    /**
     * Returns new {@code Context} with copied key-value mappings matches specified criteria.
     * <p>Changes in original context must not be reflected in returned context
     * and visa-versa.
     *
     * @param criteria criteria for key and value selection
     * @return new context with selected key-value mappings
     * @throws NullPointerException if {@code criteria} is {@code null}
     */
    Context copy(BiPredicate<String, Object> criteria);

    /**
     * Retains only those key-value pairs, that match specified criteria.
     * <p>This is similar to {@link #copy(BiPredicate)},
     * except object to be returned: this method returns this context.
     *
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code criteria} is {@code null}
     */
    Context filter(BiPredicate<String, Object> criteria);

    /**
     * Copies all key-value mappings to the specified acceptor context.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies to be stored into
     * @return this
     * @throws NullPointerException if {@code acceptor} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context copyTo(Context acceptor) {
        return copyTo(acceptor, PUT);
    }

    /**
     * Copies all key-value mappings to the specified acceptor context.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, action is defined by specified replace rule.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies be stored into
     * @param replaceRule rule to be applied to resolve key collisions
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code replaceRule} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context copyTo(Context acceptor, ReplaceRule replaceRule) {
        Objects.requireNonNull(acceptor);
        Objects.requireNonNull(replaceRule);
        if (acceptor == this) {
            throw new IllegalArgumentException();
        }
        switch (replaceRule) {
            case PUT:
                for (Entry entry : entries()) {
                    acceptor.put(entry.getKey(), entry.getValue());
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : entries()) {
                    acceptor.putIfAbsent(entry.getKey(), entry.getValue());
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : entries()) {
                    acceptor.putIfKeyAbsent(entry.getKey(), entry.getValue());
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria to the
     * specified acceptor context.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies be stored into
     * @param criteria rule to be applied to resolve key collisions
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context copyTo(Context acceptor, BiPredicate<String, Object> criteria) {
        return copyTo(acceptor, PUT, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the
     * specified acceptor context.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, action is defined by specified replace rule.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies be stored into
     * @param replaceRule rule to be applied to resolve key collisions
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context copyTo(Context acceptor, ReplaceRule replaceRule,
                           BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(acceptor);
        Objects.requireNonNull(replaceRule);
        Objects.requireNonNull(criteria);
        if (acceptor == this) {
            throw new IllegalArgumentException();
        }
        String key;
        Object value;
        switch (replaceRule) {
            case PUT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        acceptor.put(entry.getKey(), entry.getValue());
                    }
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        acceptor.putIfAbsent(entry.getKey(), entry.getValue());
                    }
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        acceptor.putIfKeyAbsent(entry.getKey(), entry.getValue());
                    }
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings to the specified acceptor context
     * and removes them from this context.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies to be stored into
     * @return this
     * @throws NullPointerException if {@code acceptor} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context drainTo(Context acceptor) {
        return drainTo(acceptor, PUT);
    }

    /**
     * Copies all key-value mappings to the specified acceptor context
     * and removes them from this context.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, action is defined by specified replace rule.
     * Anyway mapping will be removed form this.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies to be stored into
     * @param replaceRule rule for resolving key collisions
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code replaceRule} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context drainTo(Context acceptor, ReplaceRule replaceRule) {
        Objects.requireNonNull(acceptor);
        Objects.requireNonNull(replaceRule);
        if (acceptor == this) {
            throw new IllegalArgumentException();
        }
        String key;
        Object value;
        switch (replaceRule) {
            case PUT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    acceptor.put(key, value);
                    removeExactly(key, value);
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    acceptor.putIfAbsent(key, value);
                    removeExactly(key, value);
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    acceptor.putIfKeyAbsent(key, value);
                    removeExactly(key, value);
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the acceptor context
     * and removes them from this context. Unmatched mappings are retained.
     * <p>Acceptor cannot be this.
     *
     * @param acceptor context key-value mapping copies be stored into
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context drainTo(Context acceptor, BiPredicate<String, Object> criteria) {
        return drainTo(acceptor, PUT, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the acceptor context
     * and removes them from this context. Unmatched mappings are retained.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, action is defined by specified replace rule.
     * Anyway mapping will be removed form this.
     * <p>Acceptor cannot be this.
     * 
     * @param acceptor context key-value mapping copies be stored into
     * @param replaceRule rule to be applied to resolve key collisions
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code acceptor} is this
     */
    default Context drainTo(Context acceptor, ReplaceRule replaceRule,
                            BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(acceptor);
        Objects.requireNonNull(replaceRule);
        Objects.requireNonNull(criteria);
        if (acceptor == this) {
            throw new IllegalArgumentException();
        }
        String key;
        Object value;
        switch (replaceRule) {
            case PUT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        acceptor.put(key, value);
                        removeExactly(key, value);
                    }
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        acceptor.putIfAbsent(key, value);
                        removeExactly(key, value);
                    }
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        acceptor.putIfKeyAbsent(key, value);
                        removeExactly(key, value);
                    }
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings from the specified source context to this.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @return this
     * @throws NullPointerException if {@code source} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context copyFrom(Context source) {
        return copyFrom(source, PUT);
    }

    /**
     * Copies all key-value mappings from the specified source context to this.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context copyFrom(Context source, ReplaceRule replaceRule) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        if (source == this) {
            throw new IllegalArgumentException();
        }
        switch (replaceRule) {
            case PUT:
                for (Entry entry : source.entries()) {
                    put(entry.getKey(), entry.getValue());
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : source.entries()) {
                    putIfAbsent(entry.getKey(), entry.getValue());
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : source.entries()) {
                    putIfKeyAbsent(entry.getKey(), entry.getValue());
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria,
     * from the specified source context to this.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context copyFrom(Context source,BiPredicate<String, Object> criteria) {
        return copyFrom(source, PUT, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria,
     * from the specified source context to this.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context copyFrom(Context source, ReplaceRule replaceRule,
                             BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        Objects.requireNonNull(criteria);
        if (source == this) {
            throw new IllegalArgumentException();
        }
        String key;
        Object value;
        switch (replaceRule) {
            case PUT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        put(key, value);
                    }
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfAbsent(key, value);
                    }
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfKeyAbsent(key, value);
                    }
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings from the specified source context to this
     * and removes them from source. Unmatched mappings are retained.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @return this
     * @throws NullPointerException if {@code source} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context drainFrom(Context source) {
        return drainFrom(source, PUT);
    }

    /**
     * Copies all key-value mappings from the specified source context to this
     * and removes them from source. Unmatched mappings are retained.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     * Anyway it will be removed form source.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context drainFrom(Context source, ReplaceRule replaceRule) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        if (source == this) {
            throw new IllegalArgumentException();
        }
        String key;
        Object value;
        switch (replaceRule) {
            case PUT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    put(key, value);
                    source.removeExactly(key, value);
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    putIfAbsent(key, value);
                    source.removeExactly(key, value);
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    putIfKeyAbsent(key, value);
                    source.removeExactly(key, value);
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria,
     * from the specified source context to this
     * and removes them from source. Unmatched mappings are retained.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context drainFrom(Context source, BiPredicate<String, Object> criteria) {
        return drainFrom(source, PUT, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria,
     * from the specified source context to this
     * and removes them from source. Unmatched mappings are retained.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     * Anyway it will be removed form source. Unmatched mappings are retained.
     * <p>Source cannot be this.
     *
     * @param source context, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     * @throws IllegalArgumentException if {@code source} is this
     */
    default Context drainFrom(Context source, ReplaceRule replaceRule,
                              BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        Objects.requireNonNull(criteria);
        if (source == this) {
            throw new IllegalArgumentException();
        }
        String key;
        Object value;
        switch (replaceRule) {
            case PUT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        put(key, value);
                        source.removeExactly(key, value);
                    }
                }
                break;
            case PUT_IF_ABSENT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfAbsent(key, value);
                        source.removeExactly(key, value);
                    }
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Entry entry : source.entries()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfKeyAbsent(key, value);
                        source.removeExactly(key, value);
                    }
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings to the specified acceptor map.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @return this
     * @throws NullPointerException if {@code acceptor} is {@code null}
     */
    default Context copyTo(Map<String, Object> acceptor) {
        return copyTo(acceptor, true);
    }

    /**
     * Copies all key-value mappings to the specified acceptor map.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, it's associated value will be replaced if
     * {@code replace} is {@code true} or retained otherwise.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @param replace flag for replacing key-value mapping
     * @return this
     * @throws NullPointerException if {@code acceptor} is {@code null}
     */
    default Context copyTo(Map<String, Object> acceptor, boolean replace) {
        Objects.requireNonNull(acceptor);
        if (replace) {
            for (Entry entry : entries()) {
                acceptor.put(entry.getKey(), entry.getValue());
            }
        } else {
            for (Entry entry : entries()) {
                acceptor.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the specified acceptor map.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code criteria} is {@code null}
     */
    default Context copyTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        return copyTo(acceptor, true, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the specified acceptor map.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, it's associated value will be replaced if
     * {@code replace} is {@code true} or retained otherwise.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @param replace flag for replacing key-value mapping
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code criteria} is {@code null}
     */
    default Context copyTo(Map<String, Object> acceptor, boolean replace,
                           BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(acceptor);
        Objects.requireNonNull(criteria);
        String key;
        Object value;
        if (replace) {
            for (Entry entry : entries()) {
                key = entry.getKey();
                value = entry.getValue();
                if (criteria.test(key, value)) {
                    acceptor.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            for (Entry entry : entries()) {
                key = entry.getKey();
                value = entry.getValue();
                if (criteria.test(key, value)) {
                    acceptor.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
        }
        return this;
    }

    /**
     * Copies all key-value mappings to the specified acceptor map
     * and removes them from this context. Unmatched mappings are retained.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @return this
     * @throws NullPointerException if {@code acceptor} is {@code null}
     */
    default Context drainTo(Map<String, Object> acceptor) {
        return drainTo(acceptor, true);
    }

    /**
     * Copies all key-value mappings to the specified acceptor map
     * and removes them from this context. Unmatched mappings are retained.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, it's associated value will be replaced if
     * {@code replace} is {@code true} or retained otherwise.
     * Anyway mapping will be removed from this.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @param replace flag for replacing key-value mapping
     * @return this
     * @throws NullPointerException if {@code acceptor} is {@code null}
     */
    default Context drainTo(Map<String, Object> acceptor, boolean replace) {
        Objects.requireNonNull(acceptor);
        String key;
        Object value;
        if (replace) {
            for (Entry entry : entries()) {
                key = entry.getKey();
                value = entry.getValue();
                acceptor.put(key, value);
                removeExactly(key, value);
            }
        } else {
            for (Entry entry : entries()) {
                key = entry.getKey();
                value = entry.getValue();
                acceptor.putIfAbsent(key, value);
                removeExactly(key, value);
            }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the specified acceptor map
     * and removes them from this context. Unmatched mappings are retained.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code criteria} is {@code null}
     */
    default Context drainTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        return drainTo(acceptor, true, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria, to the specified acceptor map
     * and removes them from this context. Unmatched mappings are retained.
     * <p>If acceptor already contains mapping with same key as key
     * from this context, it's associated value will be replaced if
     * {@code replace} is {@code true} or retained otherwise.
     * Anyway mapping will be removed from this.
     *
     * @param acceptor map key-value mapping copies to be stored into
     * @param replace flag for replacing key-value mapping
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code acceptor} or {@code criteria} is {@code null}
     */
    default Context drainTo(Map<String, Object> acceptor, boolean replace,
                            BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(acceptor);
        Objects.requireNonNull(criteria);
        String key;
        Object value;
        if (replace) {
            for (Entry entry : entries()) {
                key = entry.getKey();
                value = entry.getValue();
                if (criteria.test(key, value)) {
                    acceptor.put(key, value);
                    removeExactly(key, value);
                }
            }
        } else {
            for (Entry entry : entries()) {
                key = entry.getKey();
                value = entry.getValue();
                if (criteria.test(key, value)) {
                    acceptor.putIfAbsent(key, value);
                    removeExactly(key, value);
                }
            }
        }
        return this;
    }

    /**
     * Copies all key-value mappings from the specified source map to this context.
     *
     * @param source  map, provides key-value mappings
     * @return this
     * @throws NullPointerException if {@code source} is {@code null}
     */
    default Context copyFrom(Map<String, Object> source) {
        return copyFrom(source, PUT);
    }

    /**
     * Copies all key-value mappings from the specified source map to this context.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     *
     * @param source  map, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule} is {@code null}
     */
    default Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        Set<Map.Entry<String, Object>> entrySet = source.entrySet();
        switch (replaceRule) {
            case PUT:
                for (Map.Entry<String, Object> entry : entrySet) {
                    put(entry.getKey(), entry.getValue());
                }
                break;
            case PUT_IF_ABSENT:
                for (Map.Entry<String, Object> entry : entrySet) {
                    putIfAbsent(entry.getKey(), entry.getValue());
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Map.Entry<String, Object> entry : entrySet) {
                    putIfKeyAbsent(entry.getKey(), entry.getValue());
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings, match specified criteria,
     * from the specified source map to this context. Unmatched mappings are retained.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     *
     * @param source  map, provides key-value mappings
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code criteria} is {@code null}
     */
    default Context copyFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        return copyFrom(source, PUT, criteria);
    }

    /**
     * Copies all key-value mappings, match specified criteria,
     * from the specified source map to this context.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     *
     * @param source  map, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     */
    default Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule,
                             BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        Objects.requireNonNull(criteria);
        String key;
        Object value;
        Set<Map.Entry<String, Object>> entrySet = source.entrySet();
        switch (replaceRule) {
            case PUT:
                for (Map.Entry<String, Object> entry : entrySet) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        put(key, value);
                    }
                }
                break;
            case PUT_IF_ABSENT:
                for (Map.Entry<String, Object> entry : entrySet) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfAbsent(key, value);
                    }
                }
                break;
            case PUT_IF_KEY_ABSENT:
                for (Map.Entry<String, Object> entry : entrySet) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfKeyAbsent(key, value);
                    }
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings from the specified source map to this context
     * and removes them from source. Unmatched mappings are retained.
     *
     * @implSpec
     * The default implementation uses {@link Iterator#remove()} to remove mappings from
     * source. Source implementation may non support it and throw
     * {@link UnsupportedOperationException}
     * @param source  map, provides key-value mappings
     * @return this
     * @throws NullPointerException if {@code source} is {@code null}
     * @throws UnsupportedOperationException if {@code source} implementation
     *         does not support {@link Iterator#remove()}
     */
    default Context drainFrom(Map<String, Object> source) {
        return drainFrom(source, PUT);
    }

    /**
     * Copies all key-value mappings from the specified source map to this context
     * and removes them from source. Unmatched mappings are retained.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     * Anyway mapping will be removed from source.
     *
     * @implSpec
     * The default implementation uses {@link Iterator#remove()} to remove mappings from
     * source. Source implementation may non support it and throw
     * {@link UnsupportedOperationException}
     * @param source  map, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule} is {@code null}
     * @throws UnsupportedOperationException if {@code source} implementation
     *         does not support {@link Iterator#remove()}
     */
    default Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        String key;
        Object value;
        Map.Entry<String, Object> entry;
        Iterator<Map.Entry<String, Object>> srcIter = source.entrySet().iterator();
        switch (replaceRule) {
            case PUT:
                while (srcIter.hasNext()) {
                    entry = srcIter.next();
                    key = entry.getKey();
                    value = entry.getValue();
                    put(key, value);
                    srcIter.remove();
                }
                break;
            case PUT_IF_ABSENT:
                while (srcIter.hasNext()) {
                    entry = srcIter.next();
                    key = entry.getKey();
                    value = entry.getValue();
                    putIfAbsent(key, value);
                    srcIter.remove();
                }
                break;
            case PUT_IF_KEY_ABSENT:
                while (srcIter.hasNext()) {
                    entry = srcIter.next();
                    key = entry.getKey();
                    value = entry.getValue();
                    putIfKeyAbsent(key, value);
                    srcIter.remove();
                }
        }
        return this;
    }

    /**
     * Copies all key-value mappings from the specified source map to this context
     * and removes them from source. Unmatched mappings are retained.
     *
     * @implSpec
     * The default implementation uses {@link Iterator#remove()} to remove mappings from
     * source. Source implementation may non support it and throw
     * {@link UnsupportedOperationException}
     * @param source  map, provides key-value mappings
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code criteria} is {@code null}
     * @throws UnsupportedOperationException if {@code source} implementation
     *         does not support {@link Iterator#remove()}
     */
    default Context drainFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        return drainFrom(source, PUT, criteria);
    }

    /**
     * Copies all key-value mappings from the specified source map to this context
     * and removes them from source. Unmatched mappings are retained.
     * <p>If this context already contains mapping with same key as key
     * from source, action is defined by specified replace rule.
     * Anyway mapping will be removed from source.
     *
     * @implSpec
     * The default implementation uses {@link Iterator#remove()} to remove mappings from
     * source. Source implementation may non support it and throw
     * {@link UnsupportedOperationException}
     * @param source  map, provides key-value mappings
     * @param replaceRule rule for resolving key collisions
     * @param criteria criteria for key and value selection
     * @return this
     * @throws NullPointerException if {@code source} or {@code replaceRule}
     *         or {@code criteria} is {@code null}
     * @throws UnsupportedOperationException if {@code source} implementation
     *         does not support {@link Iterator#remove()}
     */
    default Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule,
                              BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(replaceRule);
        String key;
        Object value;
        Map.Entry<String, Object> entry;
        Iterator<Map.Entry<String, Object>> srcIter = source.entrySet().iterator();
        switch (replaceRule) {
            case PUT:
                while (srcIter.hasNext()) {
                    entry = srcIter.next();
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        put(key, value);
                        srcIter.remove();
                    }
                }
                break;
            case PUT_IF_ABSENT:
                while (srcIter.hasNext()) {
                    entry = srcIter.next();
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfAbsent(key, value);
                        srcIter.remove();
                    }
                }
                break;
            case PUT_IF_KEY_ABSENT:
                while (srcIter.hasNext()) {
                    entry = srcIter.next();
                    key = entry.getKey();
                    value = entry.getValue();
                    if (criteria.test(key, value)) {
                        putIfKeyAbsent(key, value);
                        srcIter.remove();
                    }
                }
        }
        return this;
    }

    /**
     * Applies specified key-value operator to all key-value pairs.
     *
     * @param action operator to be applied to key-value pairs
     * @return this
     * @throws NullPointerException if {@code action} is {@code null}
     */
    default Context forEach(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        for (Entry entry : entries()) {
            action.accept(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Applies specified key-value operator to all key-value pairs, match specified criteria.
     *
     * @param criteria criteria for key and value selection
     * @param action operator to be applied to key-value pairs
     * @return this
     * @throws NullPointerException if {@code action} or {@code criteria} is {@code null}
     */
    default Context forEach(BiPredicate<String, Object> criteria, BiConsumer<String, Object> action) {
        Objects.requireNonNull(criteria);
        Objects.requireNonNull(action);
        String key;
        Object value;
        for (Entry entry : entries()) {
            key = entry.getKey();
            value = entry.getValue();
            if (criteria.test(key, value)) {
                action.accept(key, value);
            }
        }
        return this;
    }
}
