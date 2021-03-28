package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.impl.executors.shells.TaskControlShell;

import java.util.Objects;
import java.util.concurrent.Executor;

import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * A {@link TaskExecutor} that wraps submitted task in {@link Runnable}
 *  * and delegates execution to inner {@link Executor}.
 */
public class ExecutorProxyTaskExecutor implements TaskExecutor {

    private final Executor executor;

    /**
     * Create task executor with given proxy executor.
     *
     * @param executor proxy executor
     * @throws NullPointerException if specified executor is {@code null}
     */
    public ExecutorProxyTaskExecutor(Executor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    public TaskControl execute(Task task, Context context) {
        TaskControlImpl taskControl = new TaskControlImpl(task, context, this);
        executor.execute(() -> {
            if (taskControl.tryUpdateTaskStage(PENDING, RUNNING)) {
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
        });
        return new TaskControlShell(taskControl);
    }
}
