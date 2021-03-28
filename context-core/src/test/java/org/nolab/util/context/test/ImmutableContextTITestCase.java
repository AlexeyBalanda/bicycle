package org.nolab.util.context.test;

import org.nolab.util.context.AbstractImmutableContextTestCase;
import org.nolab.util.context.test.ImmutableContextTI;

import java.util.Map;

/**
 * Tests for {@link ImmutableContextTI}.
 */
public class ImmutableContextTITestCase extends AbstractImmutableContextTestCase<ImmutableContextTI> {

    @Override
    protected ImmutableContextTI getTestableContext() {
        return new ImmutableContextTI(1);
    }

    @Override
    protected ImmutableContextTI getTestableContext(int capacity) {
        return new ImmutableContextTI(capacity);
    }

    @Override
    protected ImmutableContextTI getTestableContext(Map<String, Object> source) {
        return new ImmutableContextTI(source);
    }
}
