package org.nolab.util.taskexe.impl.executors;

import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Tests for {@link LinkedTasksExecutorService}.
 */
public class LinkedTasksExecutorServiceTestCase
        extends AbstractExecutorProxyServiceTestCase<LinkedTasksExecutorService> {

    @Override
    protected Function<Executor, LinkedTasksExecutorService> getConstructor() {
        return LinkedTasksExecutorService::new;
    }
}
