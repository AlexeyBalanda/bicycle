package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

/**
 * Shell for encapsulating any {@link Context} instance.
 * All methods are delegated to encapsulated implementation.
 * Not thread-safe.
 */
public class FullContextShell implements Context, Serializable {

    private static final long serialVersionUID = 760472921634271881L;

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
    public FullContextShell(Context encapsulated) {
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
    public FullContextShell(Context encapsulated, boolean copyInShell) {
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
    public Object put(String key, Object value) {
        return encapsulated.put(key, value);
    }

    @Override
    public Object get(String key) {
        return encapsulated.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> valueType) {
        return encapsulated.get(key, valueType);
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
    public <T> T getOrDefault(String key, Class<T> valueType, T defaultValue) {
        return encapsulated.getOrDefault(key, valueType, defaultValue);
    }

    @Override
    public Object getOrCompute(String key, Function<String, Object> function) {
        return encapsulated.getOrCompute(key, function);
    }

    @Override
    public <T> T getOrCompute(String key, Class<T> valueType, Function<String, T> function) {
        return encapsulated.getOrCompute(key, valueType, function);
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
    public boolean remove(String key, Object value) {
        return encapsulated.remove(key, value);
    }

    @Override
    public <T> T removeOfType(String key, Class<T> valueType) {
        return encapsulated.removeOfType(key, valueType);
    }

    @Override
    public Object removeOrGetDefault(String key, Object defaultValue) {
        return encapsulated.removeOrGetDefault(key, defaultValue);
    }

    @Override
    public <T> T removeOrGetDefault(String key, Class<T> valueType, T defaultValue) {
        return encapsulated.removeOrGetDefault(key, valueType, defaultValue);
    }

    @Override
    public Object removeOrCompute(String key, Function<String, Object> function) {
        return encapsulated.removeOrCompute(key, function);
    }

    @Override
    public <T> T removeOrCompute(String key, Class<T> valueType, Function<String, T> function) {
        return encapsulated.removeOrCompute(key, valueType, function);
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

    @Override
    public Context copyTo(Context acceptor) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor);
        return this;
    }

    @Override
    public Context copyTo(Context acceptor, ReplaceRule replaceRule) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, replaceRule);
        return this;
    }

    @Override
    public Context copyTo(Context acceptor, BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, criteria);
        return this;
    }

    @Override
    public Context copyTo(Context acceptor, ReplaceRule replaceRule,
                          BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, replaceRule, criteria);
        return this;
    }

    @Override
    public Context drainTo(Context acceptor) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor);
        return this;
    }

    @Override
    public Context drainTo(Context acceptor, ReplaceRule replaceRule) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor, replaceRule);
        return this;
    }

    @Override
    public Context drainTo(Context acceptor, BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor, criteria);
        return this;
    }

    @Override
    public Context drainTo(Context acceptor, ReplaceRule replaceRule,
                           BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.drainTo(acceptor, replaceRule, criteria);
        return this;
    }

    @Override
    public Context copyFrom(Context source) {
        checkNotThis(source);
        encapsulated.copyFrom(source);
        return this;
    }

    @Override
    public Context copyFrom(Context source, ReplaceRule replaceRule) {
        checkNotThis(source);
        encapsulated.copyFrom(source, replaceRule);
        return this;
    }

    @Override
    public Context copyFrom(Context source, BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.copyFrom(source, criteria);
        return this;
    }

    @Override
    public Context copyFrom(Context source, ReplaceRule replaceRule,
                            BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.copyFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public Context drainFrom(Context source) {
        checkNotThis(source);
        encapsulated.drainFrom(source);
        return this;
    }

    @Override
    public Context drainFrom(Context source, ReplaceRule replaceRule) {
        checkNotThis(source);
        encapsulated.drainFrom(source, replaceRule);
        return this;
    }

    @Override
    public Context drainFrom(Context source, BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.drainFrom(source, criteria);
        return this;
    }

    @Override
    public Context drainFrom(Context source, ReplaceRule replaceRule,
                             BiPredicate<String, Object> criteria) {
        checkNotThis(source);
        encapsulated.drainFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public Context copyTo(Map<String, Object> acceptor) {
        encapsulated.copyTo(acceptor);
        return this;
    }

    @Override
    public Context copyTo(Map<String, Object> acceptor, boolean replace) {
        encapsulated.copyTo(acceptor, replace);
        return this;
    }

    @Override
    public Context copyTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        encapsulated.copyTo(acceptor, criteria);
        return this;
    }

    @Override
    public Context copyTo(Map<String, Object> acceptor, boolean replace,
                          BiPredicate<String, Object> criteria) {
        encapsulated.copyTo(acceptor, replace, criteria);
        return this;
    }

    @Override
    public Context drainTo(Map<String, Object> acceptor) {
        encapsulated.drainTo(acceptor);
        return this;
    }

    @Override
    public Context drainTo(Map<String, Object> acceptor, boolean replace) {
        encapsulated.drainTo(acceptor, replace);
        return this;
    }

    @Override
    public Context drainTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        encapsulated.drainTo(acceptor, criteria);
        return this;
    }

    @Override
    public Context drainTo(Map<String, Object> acceptor, boolean replace,
                           BiPredicate<String, Object> criteria) {
        encapsulated.drainTo(acceptor, replace, criteria);
        return this;
    }

    @Override
    public Context copyFrom(Map<String, Object> source) {
        encapsulated.copyFrom(source);
        return this;
    }

    @Override
    public Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        encapsulated.copyFrom(source, replaceRule);
        return this;
    }

    @Override
    public Context copyFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        encapsulated.copyFrom(source, criteria);
        return this;
    }

    @Override
    public Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule,
                            BiPredicate<String, Object> criteria) {
        encapsulated.copyFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public Context drainFrom(Map<String, Object> source) {
        encapsulated.drainFrom(source);
        return this;
    }

    @Override
    public Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        encapsulated.drainFrom(source, replaceRule);
        return this;
    }

    @Override
    public Context drainFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        encapsulated.drainFrom(source, criteria);
        return this;
    }

    @Override
    public Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule,
                             BiPredicate<String, Object> criteria) {
        encapsulated.drainFrom(source, replaceRule, criteria);
        return this;
    }

    @Override
    public Context forEach(BiConsumer<String, Object> action) {
        encapsulated.forEach(action);
        return this;
    }

    @Override
    public Context forEach(BiPredicate<String, Object> criteria, BiConsumer<String, Object> action) {
        encapsulated.forEach(criteria, action);
        return this;
    }
}
