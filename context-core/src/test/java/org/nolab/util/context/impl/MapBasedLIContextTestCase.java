package org.nolab.util.context.impl;

import org.nolab.util.context.AbstractContextTestCase;
import org.nolab.util.context.impl.mapb.*;

import java.util.Map;

/**
 * Tests for {@link MapBasedLIContext}.
 */
public class MapBasedLIContextTestCase extends AbstractContextTestCase<MapBasedLIContext> {
    
    @Override
    protected MapBasedLIContext getTestableContext() {
        return new MapBasedLIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP);
    }

    @Override
    protected MapBasedLIContext getTestableContext(int capacity) {
        return new MapBasedLIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP,
                capacity);
    }

    @Override
    protected MapBasedLIContext getTestableContext(Map<String, Object> source) {
        return new MapBasedLIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP,
                source);
    }
}
