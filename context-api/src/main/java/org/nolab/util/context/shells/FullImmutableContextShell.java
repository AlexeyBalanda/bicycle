package org.nolab.util.context.shells;

import org.nolab.util.context.Context;
import org.nolab.util.context.ImmutableContext;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

/**
 * Immutable shell for encapsulating any {@link Context} instance.
 * All non-mutable methods are delegated to encapsulated implementation.
 */
public class FullImmutableContextShell implements ImmutableContext, Serializable {

    private static final long serialVersionUID = 3059314190567365010L;

    /**
     * Encapsulated instance.
     */
    private final Context encapsulated;

    /**
     * If this flag is {@code true}, copy methods
     * will return copy of encapsulated instance in shell.
     * Otherwise, return copy of encapsulated instance
     * without shell.
     * Flag always self-resets to {@code true}, if
     * encapsulated instance is not {@link ImmutableContext}.
     */
    private volatile boolean copyInShell = true;

    /**
     * Construct shell with encapsulated instance.
     *
     * @param encapsulated encapsulated instance
     * @throws NullPointerException if {@code encapsulated} is null
     */
    public FullImmutableContextShell(Context encapsulated) {
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
    public FullImmutableContextShell(Context encapsulated, boolean copyInShell) {
        this.encapsulated = Objects.requireNonNull(encapsulated);
        this.copyInShell = copyInShell || !(encapsulated instanceof ImmutableContext);
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
     * Set {@code copyInShell} flag, if encapsulated
     * context is {@link ImmutableContext}.
     * Otherwise flag is resets to {@code true}.
     *
     * @param copyInShell flag
     */
    public void setCopyInShell(boolean copyInShell) {
        this.copyInShell = copyInShell || !(encapsulated instanceof ImmutableContext);
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
    public Object get(String key) {
        return encapsulated.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> valueType) {
        return encapsulated.get(key, valueType);
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
    public ImmutableContext copy() {
        return copyInShell
                ? new FullImmutableContextShell(encapsulated.copy())
                : (ImmutableContext) encapsulated.copy();
    }

    @Override
    public ImmutableContext copy(BiPredicate<String, Object> criteria) {
        return copyInShell
                ? new FullImmutableContextShell(encapsulated.copy(criteria))
                : (ImmutableContext) encapsulated.copy(criteria);
    }

    @Override
    public ImmutableContext copyTo(Context acceptor) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Context acceptor, ReplaceRule replaceRule) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, replaceRule);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Context acceptor, BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, criteria);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Context acceptor, ReplaceRule replaceRule,
                                   BiPredicate<String, Object> criteria) {
        checkNotThis(acceptor);
        encapsulated.copyTo(acceptor, replaceRule, criteria);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Map<String, Object> acceptor) {
        encapsulated.copyTo(acceptor);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Map<String, Object> acceptor, boolean replace) {
        encapsulated.copyTo(acceptor, replace);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        encapsulated.copyTo(acceptor, criteria);
        return this;
    }

    @Override
    public ImmutableContext copyTo(Map<String, Object> acceptor, boolean replace,
                                   BiPredicate<String, Object> criteria) {
        encapsulated.copyTo(acceptor, replace, criteria);
        return this;
    }

    @Override
    public ImmutableContext forEach(BiConsumer<String, Object> action) {
        encapsulated.forEach(action);
        return this;
    }

    @Override
    public ImmutableContext forEach(BiPredicate<String, Object> criteria,
                                    BiConsumer<String, Object> action) {
        encapsulated.forEach(criteria, action);
        return this;
    }
}
