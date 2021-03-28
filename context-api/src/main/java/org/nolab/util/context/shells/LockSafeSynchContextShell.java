package org.nolab.util.context.shells;

import org.nolab.util.context.Context;
import org.nolab.util.context.LockSafeContext;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thread-safe, lock-safe shell for encapsulating any {@link Context} instance.
 * Only abstract methods, {@link #equals(Object)} and {@link #toString()}
 * are delegated to encapsulated implementation.
 * Others are implemented by default.
 */
public class LockSafeSynchContextShell implements LockSafeContext, Serializable {

    private static final long serialVersionUID = 6206283238016215274L;

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
     * encapsulated instance is not {@link LockSafeContext}.
     */
    private volatile boolean copyInShell = true;

    /**
     * Construct shell with encapsulated instance.
     *
     * @param encapsulated encapsulated instance
     * @throws NullPointerException if {@code encapsulated} is null
     */
    public LockSafeSynchContextShell(Context encapsulated) {
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
    public LockSafeSynchContextShell(Context encapsulated, boolean copyInShell) {
        this.encapsulated = Objects.requireNonNull(encapsulated);
        this.copyInShell = copyInShell || !(encapsulated instanceof LockSafeContext);
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
     * context is {@link LockSafeContext}.
     * Otherwise flag is resets to {@code true}.
     *
     * @param copyInShell flag
     */
    public void setCopyInShell(boolean copyInShell) {
        this.copyInShell = copyInShell || !(encapsulated instanceof LockSafeContext);
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
    public synchronized Object remove(String key) {
        return encapsulated.remove(key);
    }

    @Override
    public synchronized boolean removeExactly(String key, Object value) {
        return encapsulated.removeExactly(key, value);
    }

    @Override
    public synchronized boolean containsKey(String key) {
        return encapsulated.containsKey(key);
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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Context)) {
            return false;
        }
        Context cobj = (Context) obj;
        if (encapsulated.size() != cobj.size()) {
            return false;
        }
        for (Entry entry : encapsulated.entries()) {
            if (!Objects.equals(entry.getValue(), cobj.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
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
    public synchronized LockSafeContext copy() {
        return copyInShell
                ? new LockSafeSynchContextShell(encapsulated.copy())
                : (LockSafeContext) encapsulated.copy();
    }
}
