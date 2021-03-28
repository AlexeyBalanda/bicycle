package org.nolab.util.context.multithread;

import org.nolab.util.context.Context;

import java.util.Map;

/**
 * Provides implementation of {@link Context}
 *
 * @param <C> implementation
 */
public interface ContextImplementationProvider<C extends Context> {

    /**
     * Get default context instance.
     *
     * @return new context instance
     */
    C getContext();

    /**
     * Get context instance with given initial capacity.
     *
     * @param capacity initial capacity
     * @return new context instance
     */
    C getContext(int capacity);

    /**
     * Get instance of context with mappings pre-defined
     * in specified map.
     * <p>Context state does non reflect specified map state
     * and visa-versa.
     *
     * @param source map with pre-defined mappings
     * @return new context instance
     */
    C getContext(Map<String, Object> source);
}
