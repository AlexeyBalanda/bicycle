package org.nolab.util.context;

import java.util.*;
import java.util.function.*;

import static org.nolab.util.context.Context.ReplaceRule.PUT;

/**
 * Context, that provides execution of values {@link Object#equals(Object)}
 * methods, functions, predicates and consumers outer of synchronized body.
 * It helps to prevent deadlock if it will appear inside of them.
 */
public interface LockSafeContext extends Context {

    @Override
    default Object getOrCompute(String key, Function<String, Object> function) {
        Objects.requireNonNull(function);
        Object stub = new Object();
        Object value = getOrDefault(key, stub);
        if (value == stub) {
            value = function.apply(key);
        }
        return value;
    }

    /**
     * In case of concurrent access to this method for one instance
     * {@code function} can be called more than once.
     * It may be undesirable for {@code function} with side-effects.
     */
    @Override
    default Object getOrComputeAndPut(String key, Function<String, Object> function) {
        Objects.requireNonNull(function);
        Object stub = new Object();
        Object value = getOrDefault(key, stub);
        if (value == stub) {
            value = function.apply(key);
            put(key, value);
        }
        return value;
    }

    /**
     * In case of concurrent access to this method of one instance
     * {@code function} can be called more than once.
     * It may be undesired for {@code function} with side-effects.
     */
    @Override
    @SuppressWarnings("unchecked")
    default <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(valueType);
        Object value = get(key);
        if (valueType.isInstance(value)) {
            return (T) value;
        } else {
            value = function.apply(key);
            put(key, value);
            return (T) value;
        }
    }

    @Override
    default Object removeOrCompute(String key, Function<String, Object> function) {
        Objects.requireNonNull(function);
        Object stub = new Object();
        Object value = removeOrGetDefault(key, stub);
        if (value == stub) {
            value = function.apply(key);
        }
        return value;
    }

    @Override
    default boolean containsValue(Object value) {
        for (Object val : values()) {
            if (Objects.equals(val, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    LockSafeContext copy();

    @Override
    default LockSafeContext copy(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        LockSafeContext copy = copy();
        for (Entry entry : entries()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!criteria.test(key, value)) {
                copy.remove(key);
            }
        }
        return copy;
    }

    @Override
    default LockSafeContext filter(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        for (Entry entry : entries()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!criteria.test(key, value)) {
                remove(key);
            }
        }
        return this;
    }

    @Override
    default LockSafeContext copyTo(Context acceptor) {
        return copyTo(acceptor, PUT);
    }

    @Override
    default LockSafeContext copyTo(Context acceptor, ReplaceRule replaceRule) {
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
    default LockSafeContext copyTo(Context acceptor, BiPredicate<String, Object> criteria) {
        return copyTo(acceptor, PUT, criteria);
    }

    @Override
    default LockSafeContext copyTo(Context acceptor, ReplaceRule replaceRule,
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
    default LockSafeContext drainTo(Context acceptor) {
        return drainTo(acceptor, PUT);
    }

    @Override
    default LockSafeContext drainTo(Context acceptor, ReplaceRule replaceRule) {
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

    @Override
    default LockSafeContext drainTo(Context acceptor, BiPredicate<String, Object> criteria) {
        return drainTo(acceptor, PUT, criteria);
    }

    @Override
    default LockSafeContext drainTo(Context acceptor, ReplaceRule replaceRule,
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

    @Override
    default LockSafeContext copyFrom(Context source) {
        return copyFrom(source, PUT);
    }

    @Override
    default LockSafeContext copyFrom(Context source, ReplaceRule replaceRule) {
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

    @Override
    default LockSafeContext copyFrom(Context source,BiPredicate<String, Object> criteria) {
        return copyFrom(source, PUT, criteria);
    }

    @Override
    default LockSafeContext copyFrom(Context source, ReplaceRule replaceRule,
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

    @Override
    default LockSafeContext drainFrom(Context source) {
        return drainFrom(source, PUT);
    }

    @Override
    default LockSafeContext drainFrom(Context source, ReplaceRule replaceRule) {
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

    @Override
    default LockSafeContext drainFrom(Context source, BiPredicate<String, Object> criteria) {
        return drainFrom(source, PUT, criteria);
    }

    @Override
    default LockSafeContext drainFrom(Context source, ReplaceRule replaceRule,
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

    @Override
    default LockSafeContext copyTo(Map<String, Object> acceptor) {
        return copyTo(acceptor, true);
    }

    @Override
    default LockSafeContext copyTo(Map<String, Object> acceptor, boolean replace) {
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
    default LockSafeContext copyTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        return copyTo(acceptor, true, criteria);
    }

    @Override
    default LockSafeContext copyTo(Map<String, Object> acceptor, boolean replace,
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
    default LockSafeContext drainTo(Map<String, Object> acceptor) {
        return drainTo(acceptor, true);
    }

    @Override
    default LockSafeContext drainTo(Map<String, Object> acceptor, boolean replace) {
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

    @Override
    default LockSafeContext drainTo(Map<String, Object> acceptor, BiPredicate<String, Object> criteria) {
        return drainTo(acceptor, true, criteria);
    }

    @Override
    default LockSafeContext drainTo(Map<String, Object> acceptor, boolean replace,
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

    @Override
    default LockSafeContext copyFrom(Map<String, Object> source) {
        return copyFrom(source, PUT);
    }

    @Override
    default LockSafeContext copyFrom(Map<String, Object> source, ReplaceRule replaceRule) {
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

    @Override
    default LockSafeContext copyFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        return copyFrom(source, PUT, criteria);
    }

    @Override
    default LockSafeContext copyFrom(Map<String, Object> source, ReplaceRule replaceRule,
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

    @Override
    default LockSafeContext drainFrom(Map<String, Object> source) {
        return drainFrom(source, PUT);
    }

    @Override
    default LockSafeContext drainFrom(Map<String, Object> source, ReplaceRule replaceRule) {
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

    @Override
    default LockSafeContext drainFrom(Map<String, Object> source, BiPredicate<String, Object> criteria) {
        return drainFrom(source, PUT, criteria);
    }

    @Override
    default LockSafeContext drainFrom(Map<String, Object> source, ReplaceRule replaceRule,
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

    @Override
    default LockSafeContext forEach(BiConsumer<String, Object> action) {
        Objects.requireNonNull(action);
        for (Entry entry : entries()) {
            action.accept(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    default LockSafeContext forEach(BiPredicate<String, Object> criteria, BiConsumer<String, Object> action) {
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
