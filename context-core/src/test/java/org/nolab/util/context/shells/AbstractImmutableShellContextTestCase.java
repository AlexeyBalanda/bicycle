package org.nolab.util.context.shells;

import org.nolab.util.context.*;
import org.nolab.util.context.test.ContextTI;

import java.util.Map;
import java.util.function.Function;

/**
 * Base class for testing immutable context shells.
 * <p>By default supposed {@code null} values are supported.
 *
 * @param <IC> the implementation
 */
public abstract class AbstractImmutableShellContextTestCase<IC extends ImmutableContext>
        extends AbstractImmutableContextTestCase<IC> {

    /**
     * Get constructor of testable shell with single
     * {@link Context} argument.
     *
     * @return constructor
     */
    protected abstract Function<Context, IC> getShellConstructor();

    @Override
    protected IC getTestableContext() {
        return getShellConstructor().apply(new ContextTI(1));
    }

    @Override
    protected IC getTestableContext(int capacity) {
        return getShellConstructor().apply(new ContextTI(capacity));
    }

    @Override
    protected IC getTestableContext(Map<String, Object> source) {
        return getShellConstructor().apply(new ContextTI(source));
    }

    @Override
    protected Context getSupportContext() {
        return super.getSupportContext();
    }

    @Override
    protected Context getSupportContext(int capacity) {
        return super.getSupportContext(capacity);
    }
}
