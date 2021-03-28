package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.*;

/**
 * Thread-safe shell for encapsulating any {@link Context} instance.
 * Only abstract methods and, {@link #equals(Object)} and {@link #toString()}
 * are delegated to encapsulated implementation.
 * Others are implemented by default.
 */
public class BaseSyncContextShell implements Context, Serializable {

    private static final long serialVersionUID = -2545790895430451022L;

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
    public BaseSyncContextShell(Context encapsulated) {
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
    public BaseSyncContextShell(Context encapsulated, boolean copyInShell) {
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
     * Set {@code copyInShell} flag
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
    public synchronized Object put(String key, Object value) {
        return encapsulated.put(key, value);
    }

    @Override
    public synchronized Object get(String key) {
        return encapsulated.get(key);
    }

    @Override
    public synchronized Object putIfAbsent(String key, Object value) {
        return encapsulated.putIfAbsent(key, value);
    }

    @Override
    public synchronized Object putIfKeyAbsent(String key, Object value) {
        return encapsulated.putIfKeyAbsent(key, value);
    }

    @Override
    public synchronized Object getOrDefault(String key, Object defaultValue) {
        return encapsulated.getOrDefault(key, defaultValue);
    }

    @Override
    public synchronized Object getOrCompute(String key, Function<String, Object> function) {
        return encapsulated.getOrCompute(key, function);
    }

    @Override
    public synchronized Object getOrComputeAndPut(String key, Function<String, Object> function) {
        return encapsulated.getOrComputeAndPut(key, function);
    }

    @Override
    public synchronized <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        return encapsulated.getOrComputeAndPut(key, valueType, function);
    }

    @Override
    public synchronized Object remove(String key) {
        return encapsulated.remove(key);
    }

    @Override
    public synchronized boolean removeExactly(String key, Object value) {
        return encapsulated.removeExactly(key, value);
    }

    @Override
    public synchronized Object removeOrCompute(String key, Function<String, Object> function) {
        return encapsulated.removeOrCompute(key, function);
    }

    @Override
    public synchronized boolean containsKey(String key) {
        return encapsulated.containsKey(key);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        return encapsulated.containsValue(value);
    }

    @Override
    public synchronized int size() {
        return encapsulated.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return encapsulated.isEmpty();
    }

    @Override
    public synchronized boolean equals(Object obj) {
        return encapsulated.equals(obj);
    }

    @Override
    public synchronized Context clear() {
        return encapsulated.clear();
    }

    @Override
    public synchronized Keys keys() {
        return encapsulated.keys();
    }

    @Override
    public synchronized Values values() {
        return encapsulated.values();
    }

    @Override
    public synchronized Entries entries() {
        return encapsulated.entries();
    }

    @Override
    public synchronized Context copy() {
        return copyInShell
                ? new BaseContextShell(encapsulated.copy())
                : encapsulated.copy();
    }

    @Override
    public synchronized Context copy(BiPredicate<String, Object> criteria) {
        return copyInShell
                ? new BaseContextShell(encapsulated.copy(criteria))
                : encapsulated.copy(criteria);
    }

    @Override
    public synchronized Context filter(BiPredicate<String, Object> criteria) {
        encapsulated.filter(criteria);
        return this;
    }
}
