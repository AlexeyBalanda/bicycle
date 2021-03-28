package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.taskexe.TaskExecutorService;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Base class for testing {@link TaskExecutorService}, delegating
 * task execution to {@link ExecutorService}.
 *
 * @param <T> implementation
 */
public abstract class AbstractExecutorProxyServiceTestCase<T extends TaskExecutorService>
        extends AbstractTaskExecutorServiceTestCase<T> {

    protected final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Get constructor, accepting executor.
     *
     * @return constructor, accepting executor
     */
    protected abstract Function<Executor, T> getConstructor();

    @Override
    protected T getSynchronousTaskExecutor() {
        return getConstructor().apply(Runnable::run);
    }

    @Override
    protected T getAsynchronousSingleThreadTaskExecutor() {
        return  getConstructor().apply(executorService);
    }
}
