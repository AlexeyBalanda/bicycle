package org.nolab.util.context;

import java.util.*;
import java.util.function.*;

/**
 * Context intended to be immutable.
 */
public interface ImmutableContext extends Context {

    /*These operations are overriden for optimization*/

    @Override
    default Object getOrDefault(String key, Object defaultValue) {
        Object value = get(key);
        if (value == null && !containsKey(key)) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    default Object getOrCompute(String key, Function<String, Object> function) {
        Objects.requireNonNull(function);
        Object value = get(key);
        if (value == null && !containsKey(key)) {
            return function.apply(key);
        } else {
            return value;
        }
    }

    /*These operations are allowed and overriden to return {@code ImmutableContext}*/

    @Override
    ImmutableContext copy();

    @Override
    ImmutableContext copy(BiPredicate<String, Object> criteria);

    @Override
    default ImmutableContext copyTo(Context acceptor) {
        return copyTo(acceptor, ReplaceRule.PUT);
    }

    @Override
    default ImmutableContext copyTo(Context acceptor, ReplaceRule replaceRule) {
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

    @Override
    default ImmutableContext copyTo(Context acceptor, BiPredicate<String, Object> criteria) {
        return copyTo(acceptor, ReplaceRule.PUT, criteria);
    }

    @Override
    default ImmutableContext copyTo(Context acceptor, ReplaceRule replaceRule,
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

    @Override
    default ImmutableContext copyTo(Map<String, Object> acceptor) {
        return copyTo(acceptor, true);
    }

    @Override
    default ImmutableContext copyTo(Map<String, Object> acceptor, boolean replace) {
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

    @Override
    default ImmutableContext copyTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        return copyTo(acceptor, true, criteria);
    }

    @Override
    default ImmutableContext copyTo(Map<String, Object> acceptor, boolean replace,
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


    @Override
    default ImmutableContext forEach(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        for (Entry entry : entries()) {
            action.accept(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    default ImmutableContext forEach(BiPredicate<String, Object> criteria, BiConsumer<String, Object> action) {
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

    /*These operations break immutability and not allowed*/

    @Override
    default Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object putIfAbsent(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object putIfKeyAbsent(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object getOrComputeAndPut(String key, Function<String, Object> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object remove(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean removeExactly(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean remove(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T removeOfType(String key, Class<T> valueType) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object removeOrGetDefault(String key, Object defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T removeOrGetDefault(String key, Class<T> valueType, T defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object removeOrCompute(String key, Function<String, Object> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T removeOrCompute(String key, Class<T> valueType, Function<String, T> supplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context filter(BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Context acceptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Context acceptor, ReplaceRule replaceRule) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Context acceptor, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Context acceptor, ReplaceRule replaceRule, 
                            BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Context source) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Context source, ReplaceRule replaceRule) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Context source, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Context source, ReplaceRule replaceRule, 
                             BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Context source) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Context source, ReplaceRule replaceRule) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Context source, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Context source, ReplaceRule replaceRule, 
                              BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Map<String, Object> acceptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Map<String, Object> acceptor, boolean replace) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainTo(Map<String, Object> acceptor, boolean replace, 
                            BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Map<String, Object> source) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context copyFrom(Map<String, Object> source, ReplaceRule replaceRule, 
                             BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Map<String, Object> source) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Context drainFrom(Map<String, Object> source, ReplaceRule replaceRule, BiPredicate<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }
}
