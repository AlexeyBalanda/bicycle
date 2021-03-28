package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

/**
 * Thread-safe shell for encapsulating any {@link Context} instance.
 * All methods are delegated to encapsulated implementation.
 */
public class FullSyncContextShell implements Context, Serializable {

    private static final long serialVersionUID = -3170581056834059549L;

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
    public FullSyncContextShell(Context encapsulated) {
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
    public FullSyncContextShell(Context encapsulated, boolean copyInShell) {
        this.encapsulated = Objects.requireNonNull(encapsulated);
        this.copyInShell = copyInShell;
    }

    /**
     * Get {@code copyInShell} flag.
     *
     * @return flag
     */
    public synchronized boolean isCopyInShell() {
        return copyInShell;
    }

    /**
     * Set {@code copyInShell} flag
     *
     * @param copyInShell flag
     */
    public synchronized void setCopyInShell(boolean copyInShell) {
        this.copyInShell = copyInShell;
    }

    /**
     * Check given object != this.
     *
     * @param obj object to check
     * @throws IllegalArgumentException if given object == this
     */
    private void checkNotThis(Object obj) {
        if (obj == this) {
            throw new IllegalArgumentException();
        }
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
    public synchronized <T> T get(String key, Class<T> valueType) {
        return encapsulated.get(key, valueType);
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
    public synchronized <T> T getOrDefault(String key, Class<T> valueType, T defaultValue) {
        return encapsulated.getOrDefault(key, valueType, defaultValue);
    }

    @Override
    public synchronized Object getOrCompute(String key, Function<String, Object> function) {
        return encapsulated.getOrCompute(key, function);
    }

    @Override
    public synchronized <T> T getOrCompute(String key, Class<T> valueType, Function<String, T> function) {
        return encapsulated.getOrCompute(key, valueType, function);
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
    public synchronized boolean remove(String key, Object value) {
        return encapsulated.remove(key, value);
    }

    @Override
    public synchronized <T> T removeOfType(String key, Class<T> valueType) {
        return encapsulated.removeOfType(key, valueType);
    }

    @Override
    public synchronized Object removeOrGetDefault(String key, Object defaultValue) {
        return encapsulated.removeOrGetDefault(key, defaultValue);
    }

    @Override
    public synchronized <T> T removeOrGetDefault(String key, Class<T> valueType, T defaultValue) {
        return encapsulated.removeOrGetDefault(key, valueType, defaultValue);
    }

    @Override
    public synchronized Object removeOrCompute(String key, Function<String, Object> function) {
        return encapsulated.removeOrCompute(key, function);
    }

    @Override
    public synchronized <T> T removeOrCompute(String key, Class<T> valueType, Function<String, T> function) {
        return encapsulated.removeOrCompute(key, valueType, function);
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

    @Override
    public synchronized Context copyTo(Context acceptor) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor);
        return this;
    }

    @Override
    public synchronized Context copyTo(Context acceptor, ReplaceRule replaceRule) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, replaceRule);
        return this;
    }

    @Override
    public synchronized Context copyTo(Context acceptor, BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, criteria);
        return this;
    }

    @Override
    public synchronized Context copyTo(Context acceptor, ReplaceRule replaceRule,
                          BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, replaceRule, criteria);
        return this;
    }

    @Override
    public synchronized Context drainTo(Context acceptor) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor);
        return this;
    }

    @Override
    public synchronized Context drainTo(Context acceptor, ReplaceRule replaceRule) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor, replaceRule);
        return this;
    }

    @Override
    public synchronized Context drainTo(Context acceptor, BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor, criteria);
        return this;
    }

    @Override
    public synchronized Context drainTo(Context acceptor, ReplaceRule replaceRule,
                           BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor, replaceRule, criteria);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Context source) {
        checkNotThis(source);
        encapsulated.copyFrom(source);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Context source, ReplaceRule replaceRule) {
        checkNotThis(source);
        encapsulated.copyFrom(source, replaceRule);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Context source, BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.copyFrom(source, criteria);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Context source, ReplaceRule replaceRule,
                            BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.copyFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Context source) {
        checkNotThis(source);
        encapsulated.drainFrom(source);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Context source, ReplaceRule replaceRule) {
        checkNotThis(source);
        encapsulated.drainFrom(source, replaceRule);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Context source, BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.drainFrom(source, criteria);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Context source, ReplaceRule replaceRule,
                             BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.drainFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public synchronized Context copyTo(Map<String, Object> acceptor) {
        encapsulated.copyTo(acceptor);
        return this;
    }

    @Override
    public synchronized Context copyTo(Map<String, Object> acceptor, boolean replace) {
        encapsulated.copyTo(acceptor, replace);
        return this;
    }

    @Override
    public synchronized Context copyTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        encapsulated.copyTo(acceptor, criteria);
        return this;
    }

    @Override
    public synchronized Context copyTo(Map<String, Object> acceptor, boolean replace,
                          BiPredicate<String, Object> criteria) {
        encapsulated.copyTo(acceptor, replace, criteria);
        return this;
    }

    @Override
    public synchronized Context drainTo(Map<String, Object> acceptor) {
        encapsulated.drainTo(acceptor);
        return this;
    }

    @Override
    public synchronized Context drainTo(Map<String, Object> acceptor, boolean replace) {
        encapsulated.drainTo(acceptor, replace);
        return this;
    }

    @Override
    public synchronized Context drainTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        encapsulated.drainTo(acceptor, criteria);
        return this;
    }

    @Override
    public synchronized Context drainTo(Map<String, Object> acceptor, boolean replace,
                           BiPredicate<String, Object> criteria) {
        encapsulated.drainTo(acceptor, replace, criteria);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Map<String, Object> source) {
        encapsulated.copyFrom(source);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        encapsulated.copyFrom(source, replaceRule);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        encapsulated.copyFrom(source, criteria);
        return this;
    }

    @Override
    public synchronized Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule,
                            BiPredicate<String, Object> criteria) {
        encapsulated.copyFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Map<String, Object> source) {
        encapsulated.drainFrom(source);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        encapsulated.drainFrom(source, replaceRule);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        encapsulated.drainFrom(source, criteria);
        return this;
    }

    @Override
    public synchronized Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule,
                             BiPredicate<String, Object> criteria) {
        encapsulated.drainFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public synchronized Context forEach(BiConsumer<String, Object> action) {
        encapsulated.forEach(action);
        return this;
    }

    @Override
    public synchronized Context forEach(BiPredicate<String, Object> criteria, BiConsumer<String, Object> action) {
        encapsulated.forEach(criteria, action);
        return this;
    }
}
