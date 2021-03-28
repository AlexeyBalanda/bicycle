package org.nolab.util.context.impl.mapb;

import java.util.*;
import java.util.function.IntFunction;

/**
 * Serializable suppliers of map with specified initial capacity.
 */
public enum CapacityMapSuppliers implements IntFunction<Map<String, Object>> {

    HASH_MAP {
        @Override
        public Map<String, Object> apply(int value) {
            return new HashMap<>(value);
        }
    },

    IDENTITY_HASH_MAP {
        @Override
        public Map<String, Object> apply(int value) {
            return new IdentityHashMap<>(value);
        }
    },

    WEAK_HASH_MAP {
        @Override
        public Map<String, Object> apply(int value) {
            return new WeakHashMap<>(value);
        }
    }
}
