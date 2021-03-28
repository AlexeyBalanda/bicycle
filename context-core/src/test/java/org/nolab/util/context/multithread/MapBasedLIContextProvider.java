package org.nolab.util.context.multithread;

import org.nolab.util.context.impl.MapBasedLIContext;
import org.nolab.util.context.impl.mapb.*;

import java.util.Map;

public interface MapBasedLIContextProvider
        extends ContextImplementationProvider<MapBasedLIContext> {

    @Override
    default MapBasedLIContext getContext() {
        return new MapBasedLIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP);
    }

    @Override
    default MapBasedLIContext getContext(int capacity) {
        return new MapBasedLIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP,
                capacity);
    }

    @Override
    default MapBasedLIContext getContext(Map<String, Object> source) {
        return new MapBasedLIContext(false, true,
                DefaultMapSuppliers.HASH_MAP,
                CapacityMapSuppliers.HASH_MAP,
                source);
    }
}
