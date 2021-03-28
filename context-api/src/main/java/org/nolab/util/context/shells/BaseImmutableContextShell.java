package org.nolab.util.context.shells;

import org.nolab.util.context.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.*;

/**
 * Immutable shell for encapsulating any {@link Context} instance.
 * Only abstract methods, {@link #equals(Object)} and {@link #toString()}
 * are delegated to encapsulated implementation.
 * Others are implemented by default.
 */
public class BaseImmutableContextShell implements ImmutableContext, Serializable {

    private static final long serialVersionUID = 6665786652001631300L;

    /**
     * Encapsulated instance.
     */
    private final Context encapsulated;

    /**
     * If this flag is {@code true}, copy methods
     * will return copy of encapsulated instance in shell.
     * Otherwise, return copy of encapsulated instance
     * without shell.
     * Flag is always reset to {@code true}, if
     * encapsulated instance is not {@link ImmutableContext}.
     */
    private volatile boolean copyInShell = true;

    /**
     * Construct shell with encapsulated instance.
     *
     * @param encapsulated encapsulated instance
     * @throws NullPointerException if {@code encapsulated} is null
     */
    public BaseImmutableContextShell(Context encapsulated) {
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
    public BaseImmutableContextShell(Context encapsulated, boolean copyInShell) {
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

    @Override
    public String toString() {
        return encapsulated.toString();
    }

    @Override
    public Object get(String key) {
        return encapsulated.get(key);
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
                ? new BaseImmutableContextShell(encapsulated.copy())
                : (ImmutableContext) encapsulated.copy();
    }

    @Override
    public ImmutableContext copy(BiPredicate<String, Object> criteria) {
        return copyInShell
                ? new BaseImmutableContextShell(encapsulated.copy(criteria))
                : (ImmutableContext) encapsulated.copy(criteria);
    }
}
