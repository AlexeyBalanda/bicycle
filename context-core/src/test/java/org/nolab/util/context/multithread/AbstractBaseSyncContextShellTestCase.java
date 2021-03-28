package org.nolab.util.context.multithread;

import org.nolab.util.context.*;
import org.nolab.util.context.shells.BaseSyncContextShell;

import java.util.Map;

/**
 * Tests for context implementations, encapsulated in {@link BaseSyncContextShell}.
 *
 * @param <C> implementation
 */
public abstract class AbstractBaseSyncContextShellTestCase<C extends Context>
        extends AbstractSynchronizedThreadContextTestCase<BaseSyncContextShell>
        implements ContextImplementationProvider<C> {

    @Override
    protected BaseSyncContextShell getTestableContext() {
        return new BaseSyncContextShell(getContext());
    }

    @Override
    protected BaseSyncContextShell getTestableContext(int capacity) {
        return new BaseSyncContextShell(getContext(capacity));
    }

    @Override
    protected BaseSyncContextShell getTestableContext(Map<String, Object> source) {
        return new BaseSyncContextShell(getContext(source));
    }
}
