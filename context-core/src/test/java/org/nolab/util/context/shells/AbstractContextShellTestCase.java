package org.nolab.util.context.shells;

import org.nolab.util.context.*;
import org.nolab.util.context.test.ContextTI;

import java.util.Map;
import java.util.function.Function;

/**
 * Base class for testing context shells.
 * <p>By default supposed {@code null} values supported.
 *
 * @param <C> the implementation
 */
public abstract class AbstractContextShellTestCase<C extends Context>
        extends AbstractContextTestCase<C> {

    /**
     * Get constructor of testable shell with single
     * {@link Context} argument.
     *
     * @return constructor
     */
    protected abstract Function<Context, C> getShellConstructor();

    @Override
    protected C getTestableContext() {
        return getShellConstructor().apply(new ContextTI(1));
    }

    @Override
    protected C getTestableContext(int capacity) {
        return getShellConstructor().apply(new ContextTI(capacity));
    }

    @Override
    protected C getTestableContext(Map<String, Object> source) {
        return getShellConstructor().apply(new ContextTI(source));
    }
}
