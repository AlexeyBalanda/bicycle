package org.nolab.util.context.test;

import org.nolab.util.context.ImmutableContext;

import java.util.*;
import java.util.function.*;

/**
 * Test implementation of {@link ImmutableContext}.
 */
public class ImmutableContextTI extends TIBasis implements ImmutableContext {

    private static final long serialVersionUID = 9031711128717014909L;

    public ImmutableContextTI(int capacity) {
        super(capacity);
    }

    protected ImmutableContextTI(EntryImpl[] entries, int size) {
        super(entries, size);
    }

    public ImmutableContextTI(Map<String, Object> source) {
        super(source);
    }

    @Override
    public Object getOrCompute(String key, Function<String, Object> function) {
        return doGetOrCompute(key, function);
    }

    public boolean containsValue(Object value) {
        return doContainsValue(value);
    }

    @Override
    public ImmutableContext copy() {
        return createCopy(ImmutableContextTI::new);
    }

    @Override
    public ImmutableContext copy(BiPredicate<String, Object> criteria) {
        return createCopy(ImmutableContextTI::new, entry -> criteria.test(entry.key, entry.value));
    }
}
