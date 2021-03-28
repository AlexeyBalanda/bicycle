package org.nolab.util.context.test;

import org.nolab.util.context.Context;
import org.nolab.util.context.impl.AbstractLIContext;

import java.io.*;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * Test implementation for {@link AbstractLIContext}.
 */
public class LIContextTI extends AbstractLIContext {

    private static final long serialVersionUID = 4254636013441679627L;

    /**
     * Default constructor.
     */
    public LIContextTI() {
    }

    /**
     * Construct context from source map.
     *
     * @param source source map
     */
    public LIContextTI(Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            addNewNode(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Construct context from original with selection criteria.
     *
     * @param original original context
     * @param criteria selection criteria
     */
    private LIContextTI(LIContextTI original, BiPredicate<String, Object> criteria) {
        IterNode node = original.head.next;
        while (node != null) {
            if (criteria.test(node.key, node.value)) {
                addNewNode(node.key, node.value);
            }
            node = node.next;
        }
    }

    @Override
    protected IterNode findNode(String key) {
        IterNode node = head.next;
        while (node != null) {
            if (Objects.equals(node.key, key)) {
                return node;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    protected void putNode(IterNode node) {
    }

    @Override
    protected void removeNode(IterNode node) {
        node.remove();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        write(s);
    }

    private void readObject(ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        read(s);
    }

    @Override
    public Context copy() {
        return new LIContextTI(this, (key, value) -> true);
    }

    @Override
    public Context copy(BiPredicate<String, Object> criteria) {
        Objects.requireNonNull(criteria);
        return new LIContextTI(this, criteria);
    }
}
