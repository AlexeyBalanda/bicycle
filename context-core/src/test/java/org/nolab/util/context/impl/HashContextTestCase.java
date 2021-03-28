package org.nolab.util.context.impl;

import org.nolab.util.context.AbstractContextTestCase;

import java.util.Map;

public class HashContextTestCase extends AbstractContextTestCase<HashContext> {

    @Override
    protected HashContext getTestableContext() {
        return new HashContext(1, 2, 0.5f);
    }

    @Override
    protected HashContext getTestableContext(int capacity) {
        return new HashContext(capacity, 2, 0.5f);
    }

    @Override
    protected HashContext getTestableContext(Map<String, Object> source) {
        HashContext context = new HashContext(source);
        context.resize(2, 0.5f, 2, 0.5f);
        return context;
    }
}
