package org.nolab.util.context.multithread;

import org.nolab.util.context.impl.HashContext;

import java.util.Map;

public interface HashContextProvider
        extends ContextImplementationProvider<HashContext> {

    @Override
    default HashContext getContext() {
        return new HashContext();
    }

    @Override
    default HashContext getContext(int capacity) {
        return new HashContext(capacity);
    }

    @Override
    default HashContext getContext(Map<String, Object> source) {
        return new HashContext(source);
    }
}
