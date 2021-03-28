package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.exceptions.UselessWaitingException;

import java.util.Objects;

import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * A {@link TaskExecutor}, that executes task synchronously
 * and return {@link TaskControl} with finished task.
 */
public class SynchronousTaskExecutor implements TaskExecutor {

    /**
     * Simple control for already finished task.
     */
    private static class PojoTaskControl implements TaskControl {

        private final Task task;
        private final Context context;
        private final TaskExecutor executor;

        private volatile TaskStage finalStage;
        private volatile Throwable failureCause;

        public PojoTaskControl(Task task, Context context, TaskExecutor executor) {
            this.task = task;
            this.context = context;
            this.executor = executor;
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
            return finalStage;
        }

        @Override
        public Throwable getFailureCause() {
            return failureCause;
        }

        @Override
        public TaskStage awaitNextStage(TaskStage startStage)
                throws UselessWaitingException {
            if (!startStage.isBefore(finalStage)) {
                throw new UselessWaitingException(startStage);
            }
            return finalStage;
        }

        @Override
        public TaskStage awaitNextStage(TaskStage startStage, long timeout)
                throws UselessWaitingException {
            if (!startStage.isBefore(finalStage)) {
                throw new UselessWaitingException(startStage);
            }
            return finalStage;
        }

        @Override
        public void cancelTask() {
            //do nothing
        }
    }

    @Override
    public TaskControl execute(Task task, Context context) {
        Objects.requireNonNull(task);
        PojoTaskControl control = new PojoTaskControl(task, context, this);
        try {
            task.execute(context);
            control.finalStage = COMPLETE;
        } catch (Throwable e) {
            control.finalStage = FAILED;
            control.failureCause = e;
        }
        return control;
    }
}
