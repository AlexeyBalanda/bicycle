package org.nolab.util.context.test;

import org.nolab.util.context.AbstractContextTestCase;
import org.nolab.util.context.test.ContextTI;

import java.util.Map;

/**
 * Tests for {@link ContextTI}.
 */
public class ContextTITestCase extends AbstractContextTestCase<ContextTI> {

    @Override
    protected ContextTI getTestableContext() {
        return new ContextTI(1);
    }

    @Override
    protected ContextTI getTestableContext(int capacity) {
        return new ContextTI(capacity);
    }

    @Override
    protected ContextTI getTestableContext(Map<String, Object> source) {
        return new ContextTI(source);
    }
}
