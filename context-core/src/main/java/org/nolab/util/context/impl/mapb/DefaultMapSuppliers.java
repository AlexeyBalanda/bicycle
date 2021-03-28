package org.nolab.util.context.impl.mapb;

import java.util.*;
import java.util.function.Supplier;

/**
 * Serializable suppliers of map with default initial capacity.
 */
public enum DefaultMapSuppliers implements Supplier<Map<String, Object>> {

    HASH_MAP {
        @Override
        public Map<String, Object> get() {
            return new HashMap<>();
        }
    },

    IDENTITY_HASH_MAP {
        @Override
        public Map<String, Object> get() {
            return new IdentityHashMap<>();
        }
    },

    WEAK_HASH_MAP {
        @Override
        public Map<String, Object> get() {
            return new WeakHashMap<>();
        }
    },

    TREE_MAP {
        @Override
        public Map<String, Object> get() {
            return new TreeMap<>();
        }
    };

    private static final long serialVersionUID = 0L;
}
