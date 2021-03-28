package org.nolab.util.taskexe.impl.executors.shells;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;

import java.util.concurrent.TimeUnit;

/**
 * A {@link TaskExecutorService} implementation that encapsulates instance
 * of another implementation, hiding protected and default-access members.
 */
public class TaskExecutorServiceShell implements TaskExecutorService {

    private final TaskExecutorService original;

    /**
     * Constructor with original instance.
     *
     * @param original original instance
     */
    public TaskExecutorServiceShell(TaskExecutorService original) {
        this.original = original;
    }

    @Override
    public TaskControl execute(Task task, Context context) {
        return original.execute(task, context);
    }

    @Override
    public int getTaskCount() {
        return original.getTaskCount();
    }

    @Override
    public Tasks getTasks() {
        return original.getTasks();
    }

    @Override
    public void shutdown() {
        original.shutdown();
    }

    @Override
    public void shutdownNow() {
        original.shutdownNow();
    }

    @Override
    public boolean isTerminating() {
        return original.isTerminating();
    }

    @Override
    public boolean isTerminated() {
        return original.isTerminated();
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        original.awaitTermination();
    }

    @Override
    public boolean awaitTermination(long timeout) throws InterruptedException {
        return original.awaitTermination(timeout);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return original.awaitTermination(timeout, unit);
    }

    @Override
    public Object awaitAction(Object statePoint) throws InterruptedException {
        return original.awaitAction(statePoint);
    }

    @Override
    public Object awaitAction(Object statePoint, long timeout) throws InterruptedException {
        return original.awaitAction(statePoint, timeout);
    }

    @Override
    public Object awaitAction(Object statePoint, long timeout, TimeUnit unit) throws InterruptedException {
        return original.awaitAction(statePoint, timeout, unit);
    }
}
