package org.nolab.util.context.test;

import org.nolab.util.context.AbstractContextTestCase;
import org.nolab.util.context.test.LIContextTI;

import java.util.Map;

/**
 * Tests for {@link LIContextTI}.
 */
public class LIContextTITestCase extends AbstractContextTestCase<LIContextTI> {

    @Override
    protected LIContextTI getTestableContext() {
        return new LIContextTI();
    }

    @Override
    protected LIContextTI getTestableContext(int capacity) {
        return new LIContextTI();
    }

    @Override
    protected LIContextTI getTestableContext(Map<String, Object> source) {
        return new LIContextTI(source);
    }
}
