package org.nolab.util.taskexe.test;

import org.nolab.util.context.Context;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Test implementation of {@link Context}.
 */
public class ContextTI extends TIBasis implements Context {

    private static final long serialVersionUID = 5437109559857699026L;

    public ContextTI(int capacity) {
        super(capacity);
    }

    public ContextTI(EntryImpl[] entries, int size) {
        super(entries, size);
    }

    public ContextTI(Map<String, Object> source) {
        super(source);
    }

    @Override
    public Object put(String key, Object value) {
        return doPut(key, value);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        return doPutIfAbsent(key, value);
    }

    @Override
    public Object putIfKeyAbsent(String key, Object value) {
        return doPutIfKeyAbsent(key, value);
    }

    @Override
    public Object getOrCompute(String key, Function<String, Object> function) {
        return doGetOrCompute(key, function);
    }

    @Override
    public Object getOrComputeAndPut(String key, Function<String, Object> function) {
        return doGetOrComputeAndPut(key, function);
    }

    @Override
    public <T> T getOrComputeAndPut(String key, Class<T> valueType, Function<String, T> function) {
        return doGetOrComputeAndPut(key, valueType, function);
    }

    @Override
    public Object remove(String key) {
        return doRemove(key);
    }

    @Override
    public boolean removeExactly(String key, Object value) {
        return doRemoveExactly(key, value);
    }

    @Override
    public Object removeOrCompute(String key, Function<String, Object> function) {
        return doRemoveOrCompute(key, function);
    }

    @Override
    public boolean containsValue(Object value) {
        return doContainsValue(value);
    }

    @Override
    public Context clear() {
        doClear();
        return this;
    }

    @Override
    public Context copy() {
        return createCopy(ContextTI::new);
    }

    @Override
    public Context copy(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        return createCopy(ContextTI::new, entry -> criteria.test(entry.key, entry.value));
    }

    @Override
    public Context filter(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        filter(entry -> criteria.test(entry.key, entry.value));
        return this;
    }
}
