package org.nolab.util.context.impl;

import org.nolab.util.context.AbstractContextTestCase;
import org.nolab.util.context.impl.mapb.*;

import java.util.Map;

/**
 * Tests for {@link MapBasedSIContext}.
 */
public class MapBasedSIContextTestCase extends AbstractContextTestCase<MapBasedSIContext> {

    @Override
    protected MapBasedSIContext getTestableContext() {
        return new MapBasedSIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP);
    }

    @Override
    protected MapBasedSIContext getTestableContext(int capacity) {
        return new MapBasedSIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP,
                capacity);
    }

    @Override
    protected MapBasedSIContext getTestableContext(Map<String, Object> source) {
        return new MapBasedSIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP,
                source);
    }
}
