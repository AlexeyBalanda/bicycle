package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.exceptions.DeniedExecutionException;
import org.nolab.util.taskexe.impl.executors.shells.TaskControlShell;

import java.util.Objects;
import java.util.concurrent.*;

import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * A {@link TaskExecutorService} that wraps submitted task in {@link Runnable}
 * and delegates execution to inner {@link Executor}.
 */
public abstract class ExecutorProxyTaskExecutorService extends AbstractTaskExecutorService {

    protected final Executor executor;
    protected final boolean tryShutdownExecutor;

    /**
     * Create task executor service with specified {@link Executor}
     * for execution delegation.
     * If {@code tryShutdownExecutor} flag is {@code true} and specified
     * executor is instance of {@link ExecutorService},
     * than {@link ExecutorService#shutdown()} will be invoked
     * at invocation of {@link #shutdown()} and
     * {@link ExecutorService#shutdownNow()} will be invoked
     * at invocation of {@link #shutdownNow()}
     *
     * @param executor executor to delegate tasks execution
     * @param tryShutdownExecutor shutdown executor flag
     * @throws NullPointerException if specified executor is {@code null}
     */
    public ExecutorProxyTaskExecutorService(Executor executor, boolean tryShutdownExecutor) {
        this.executor = Objects.requireNonNull(executor);
        this.tryShutdownExecutor = tryShutdownExecutor;
    }

    /**
     * Create task executor service with specified {@link Executor}
     * for execution delegation.
     *
     * @param executor executor to delegate tasks execution
     * @throws NullPointerException if specified executor is {@code null}
     */
    public ExecutorProxyTaskExecutorService(Executor executor) {
        this(executor, false);
    }

    /**
     * Create new {@link InnerTaskControl} with given task and
     * context and add it to submitted tasks set.
     *
     * @param task task to execute
     * @param context context for execution
     * @return new task control
     */
    protected abstract InnerTaskControl createTaskControl(Task task, Context context);

    /**
     * Do special actions with specified task control, when task
     * is terminated.
     *
     * @param taskControl task control to utilize
     */
    protected abstract void utilizeTaskControl(InnerTaskControl taskControl);

    /**
     * Cancel all pending tasks.
     */
    protected abstract void cancelAllPendingTasks();

    /**
     * Check all tasks are complete? failed or cancelled.
     *
     * @return {@code true}, if all tasks finished or {@code false}
     */
    protected abstract boolean checkAllTasksFinished();

    @Override
    public TaskControl execute(Task task, Context context) {
        InnerTaskControl taskControl;
        lock.lock();
        try {
            if (terminating || terminated) {
                throw new DeniedExecutionException();
            }
            taskControl = createTaskControl(task, context);
            statePoint = new Object();
            condition.signalAll();
        } finally {
            lock.unlock();
        }
        boolean error = true;
        try {
            executor.execute(() -> {
                boolean started = taskControl.tryUpdateTaskStage(PENDING, RUNNING);
                if (started) {
                    lock.lock();
                    try {
                        statePoint = new Object();
                        condition.signalAll();
                    } finally {
                        lock.unlock();
                    }
                    try {
                        //---------------------
                        task.execute(context);
                        //---------------------
                        if ((task instanceof InterruptableTask)
                                && ((InterruptableTask) task).isInterrupted()) {
                            taskControl.updateTaskStage(CANCELLED);
                        } else {
                            taskControl.updateTaskStage(COMPLETE);
                        }
                    } catch (Throwable e) {
                        taskControl.setFailureCause(e);
                        taskControl.updateTaskStage(FAILED);
                    }
                }
                lock.lock();
                try {
                    if (terminating) {
                        terminated = checkAllTasksFinished();
                        terminating = !terminated;
                    }
                    if (started || terminated) {
                        statePoint = new Object();
                        condition.signalAll();
                    }
                    utilizeTaskControl(taskControl);
                } finally {
                    lock.unlock();
                }
            });
            error = false;
        } finally {
            if (error) {
                lock.lock();
                try {
                    utilizeTaskControl(taskControl);
                } finally {
                    lock.unlock();
                }
            }
        }
        return new TaskControlShell(taskControl);
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            terminating = true;
            terminated = checkAllTasksFinished();
            terminating = !terminated;
            statePoint = new Object();
            condition.signalAll();
        } finally {
            lock.unlock();
        }
        if (tryShutdownExecutor
                && (executor instanceof ExecutorService)) {
            ((ExecutorService) executor).shutdown();
        }
    }

    @Override
    public void shutdownNow() {
        lock.lock();
        try {
            terminating = true;
            cancelAllPendingTasks();
            terminated = checkAllTasksFinished();
            terminating = !terminated;
            statePoint = new Object();
            condition.signalAll();
        } finally {
            lock.unlock();
        }
        if (tryShutdownExecutor
                && (executor instanceof ExecutorService)) {
            ((ExecutorService) executor).shutdownNow();
        }
    }
}
