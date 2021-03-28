package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.exceptions.UselessWaitingException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * Base implementation of {@link TaskControl}.
 */
public class TaskControlImpl implements TaskControl {

    protected final Lock lock = new ReentrantLock(true);
    protected final Condition condition = lock.newCondition();

    protected final Task task;
    protected final Context context;
    protected final TaskExecutor executor;

    protected volatile TaskStage taskStage;
    protected volatile Throwable failureCause;

    /**
     * Create new control with given task, context and executor.
     *
     * @param task task
     * @param context context
     * @param executor executor
     */
    public TaskControlImpl(Task task, Context context, TaskExecutor executor) {
        this.task = task;
        this.context = context;
        this.executor = executor;
        taskStage = PENDING;
    }

    private void checkStage(TaskStage taskStage) throws UselessWaitingException {
        if (!taskStage.hasAfter()) {
            throw new UselessWaitingException(taskStage);
        }
    }

    /**
     * Update task stage, setting specified stage, and signal
     * to all stage change waiters.
     *
     * @param newStage new task stage
     */
    public void updateTaskStage(TaskStage newStage) {
        lock.lock();
        try {
            this.taskStage = newStage;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * If current stage is such as specified, update task stage,
     * setting specified stage, signal to all stage change waiters
     * and return {@code true}. Otherwise return {@code false}.
     *
     * @param expectedStage expected current task stage
     * @param newStage new task stage
     * @return {@code true} if current stage is expected, or {@code false} otherwise
     */
    public boolean tryUpdateTaskStage(TaskStage expectedStage, TaskStage newStage) {
        lock.lock();
        try {
            if (taskStage == expectedStage) {
                taskStage = newStage;
                condition.signalAll();
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set given {@link Throwable} as cause of task failure.
     *
     * @param failureCause cause of task failure
     */
    public void setFailureCause(Throwable failureCause) {
        this.failureCause = failureCause;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public TaskExecutor getExecutor() {
        return executor;
    }

    @Override
    public TaskStage getTaskStage() {
        return taskStage;
    }

    @Override
    public Throwable getFailureCause() {
        return failureCause;
    }

    @Override
    public TaskStage awaitNextStage(TaskStage startStage)
            throws UselessWaitingException, InterruptedException {
        checkStage(startStage);
        lock.lock();
        try {
            while (startStage.isAfter(taskStage) || startStage == taskStage) {
                condition.await();
            }
            return taskStage;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TaskStage awaitNextStage(TaskStage startStage, long timeout)
            throws UselessWaitingException, InterruptedException {
        return awaitNextStage(startStage, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public TaskStage awaitNextStage(TaskStage startStage, long timeout, TimeUnit unit)
            throws UselessWaitingException, InterruptedException {
        checkStage(startStage);
        if (timeout < 0) {
            throw new IllegalArgumentException("Invalid timeout: " + timeout);
        }
        lock.lock();
        try {
            long nanosTimeout = unit.toNanos(timeout);
            if (nanosTimeout < 0) {
                nanosTimeout = Long.MAX_VALUE;
            }
            long nanosStart = System.nanoTime();
            while (!taskStage.isAfter(startStage)) {
                if ((nanosTimeout = nanosTimeout - (System.nanoTime() - nanosStart)) <= 0) {
                    break;
                }
                condition.awaitNanos(nanosTimeout);
            }
            return taskStage;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cancelTask() {
        if (!tryUpdateTaskStage(PENDING, CANCELLED)
                && (task instanceof InterruptableTask)) {
            ((InterruptableTask) task).interrupt();
        }
    }
}
