package org.nolab.util.taskexe.impl.executors;

import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Tests for {@link SeparatedTasksExecutorService}.
 */
public class SeparatedTasksExecutorServiceTestCase
        extends AbstractExecutorProxyServiceTestCase<SeparatedTasksExecutorService> {

    @Override
    protected Function<Executor, SeparatedTasksExecutorService> getConstructor() {
        return SeparatedTasksExecutorService::new;
    }
}
