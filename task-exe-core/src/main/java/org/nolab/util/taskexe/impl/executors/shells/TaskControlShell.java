package org.nolab.util.taskexe.impl.executors.shells;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.exceptions.UselessWaitingException;

import java.util.concurrent.TimeUnit;

/**
 * A {@link TaskControl} implementation that encapsulates instance
 * of another implementation, hiding protected and default-access members.
 */
public class TaskControlShell implements TaskControl {

    private final TaskControl original;

    /**
     * Constructor with original instance.
     *
     * @param original original instance
     */
    public TaskControlShell(TaskControl original) {
        this.original = original;
    }

    @Override
    public Task getTask() {
        return original.getTask();
    }

    @Override
    public Context getContext() {
        return original.getContext();
    }

    @Override
    public TaskExecutor getExecutor() {
        return original.getExecutor();
    }

    @Override
    public TaskStage getTaskStage() {
        return original.getTaskStage();
    }

    @Override
    public Throwable getFailureCause() {
        return original.getFailureCause();
    }

    @Override
    public TaskStage awaitNextStage(TaskStage startStage)
            throws UselessWaitingException, InterruptedException {
        return original.awaitNextStage(startStage);
    }

    @Override
    public TaskStage awaitNextStage(TaskStage startStage, long timeout)
            throws UselessWaitingException, InterruptedException {
        return original.awaitNextStage(startStage, timeout);
    }

    @Override
    public TaskStage awaitNextStage(TaskStage startStage, long timeout, TimeUnit unit) throws UselessWaitingException, InterruptedException {
        return original.awaitNextStage(startStage, timeout, unit);
    }

    @Override
    public void cancelTask() {
        original.cancelTask();
    }
}
