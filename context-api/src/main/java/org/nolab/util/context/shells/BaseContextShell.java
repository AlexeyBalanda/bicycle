package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.*;

/**
 * Shell for encapsulating any {@link Context} instance.
 * Only abstract methods, {@link #equals(Object)} and {@link #toString()}
 * are delegated to encapsulated implementation.
 * Others are implemented by default.
 * Not thread-safe.
 */
public class BaseContextShell implements Context, Serializable {

    private static final long serialVersionUID = -4049653173562620422L;

    /**
     * Encapsulated instance.
     */
    private final Context encapsulated;

    /**
     * If this flag is {@code true}, copy methods
     * will return copy of encapsulated instance in shell.
     * Otherwise, return copy of encapsulated instance
     * without shell.
     */
    private volatile boolean copyInShell = true;

    /**
     * Construct shell with encapsulated instance.
     *
     * @param encapsulated encapsulated instance
     * @throws NullPointerException if {@code encapsulated} is null
     */
    public BaseContextShell(Context encapsulated) {
        this.encapsulated = Objects.requireNonNull(encapsulated);
    }

    /**
     * Construct shell with encapsulated instance
     * and {@code copyInShell} flag.
     *
     * @param encapsulated encapsulated instance
     * @param copyInShell flag
     * @throws NullPointerException if {@code encapsulated} is null
     */
    public BaseContextShell(Context encapsulated, boolean copyInShell) {
        this.encapsulated = Objects.requireNonNull(encapsulated);
        this.copyInShell = copyInShell;
    }

    /**
     * Get {@code copyInShell} flag.
     *
     * @return flag
     */
    public boolean isCopyInShell() {
        return copyInShell;
    }

    /**
     * Set {@code copyInShell} flag.
     *
     * @param copyInShell flag
     */
    public void setCopyInShell(boolean copyInShell) {
        this.copyInShell = copyInShell;
    }

    @Override
    public String toString() {
        return encapsulated.toString();
    }

    @Override
    public Object put(String key, Object value) {
        return encapsulated.put(key, value);
    }

    @Override
    public Object get(String key) {
        return encapsulated.get(key);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return encapsulated.putIfAbsent(key, value);
    }

    @Override
    public Object putIfKeyAbsent(String key, Object value) {
        return encapsulated.putIfKeyAbsent(key, value);
    }

    @Override
    public Object getOrDefault(String key, Object defaultValue) {
        return encapsulated.getOrDefault(key, defaultValue);
    }

    @Override
    public Object getOrCompute(String key, Function<String, Object> function) {
        return encapsulated.getOrCompute(key, function);
    }

    @Override
    public Object getOrComputeAndPut(String key, Function<String, Object> function) {
        return encapsulated.getOrComputeAndPut(key, function);
    }

    @Override
    public <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        return encapsulated.getOrComputeAndPut(key, valueType, function);
    }

    @Override
    public Object remove(String key) {
        return encapsulated.remove(key);
    }

    @Override
    public boolean removeExactly(String key, Object value) {
        return encapsulated.removeExactly(key, value);
    }

    @Override
    public Object removeOrCompute(String key, Function<String, Object> function) {
        return encapsulated.removeOrCompute(key, function);
    }

    @Override
    public boolean containsKey(String key) {
        return encapsulated.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return encapsulated.containsValue(value);
    }

    @Override
    public int size() {
        return encapsulated.size();
    }

    @Override
    public boolean isEmpty() {
        return encapsulated.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return encapsulated.equals(obj);
    }

    @Override
    public Context clear() {
        return encapsulated.clear();
    }

    @Override
    public Keys keys() {
        return encapsulated.keys();
    }

    @Override
    public Values values() {
        return encapsulated.values();
    }

    @Override
    public Entries entries() {
        return encapsulated.entries();
    }

    @Override
    public Context copy() {
        return copyInShell
                ? new BaseContextShell(encapsulated.copy())
                : encapsulated.copy();
    }

    @Override
    public Context copy(BiPredicate<String, Object> criteria) {
        return copyInShell
                ? new BaseContextShell(encapsulated.copy(criteria))
                : encapsulated.copy(criteria);
    }

    @Override
    public Context filter(BiPredicate<String, Object> criteria) {
        encapsulated.filter(criteria);
        return this;
    }
}
