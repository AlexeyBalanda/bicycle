package org.nolab.util.taskexe.impl.executors;

import java.util.concurrent.*;

/**
 * Tests for {@link ExecutorProxyTaskExecutor}.
 */
public class ExecutorProxyTaskExecutorTestCase
        extends AbstractTaskExecutorTestCase<ExecutorProxyTaskExecutor> {

    protected final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected ExecutorProxyTaskExecutor getSynchronousTaskExecutor() {
        return new ExecutorProxyTaskExecutor(Runnable::run);
    }

    @Override
    protected ExecutorProxyTaskExecutor getAsynchronousSingleThreadTaskExecutor() {
        return new ExecutorProxyTaskExecutor(executorService);
    }
}
