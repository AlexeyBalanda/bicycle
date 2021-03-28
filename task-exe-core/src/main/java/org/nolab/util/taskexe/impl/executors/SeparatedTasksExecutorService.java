package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.impl.executors.shells.TaskControlShell;

import java.util.*;
import java.util.concurrent.Executor;

import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * A {@link ExecutorProxyTaskExecutorService} with separated task controls
 * iterator returned by {@link #getTasks()}.
 */
public class SeparatedTasksExecutorService extends ExecutorProxyTaskExecutorService {

    private Set<InnerTaskControl> taskControls = new HashSet<>();

    /**
     * Call to super.
     */
    public SeparatedTasksExecutorService(Executor executor, boolean tryShutdownExecutor) {
        super(executor, tryShutdownExecutor);
    }

    /**
     * Call to super.
     */
    public SeparatedTasksExecutorService(Executor executor) {
        super(executor);
    }

    @Override
    protected InnerTaskControl createTaskControl(Task task, Context context) {
        InnerTaskControl taskControl = new InnerTaskControl(task, context, this);
        taskControls.add(taskControl);
        return taskControl;
    }

    @Override
    protected void utilizeTaskControl(InnerTaskControl taskControl) {
        taskControls.remove(taskControl);
    }

    @Override
    protected void cancelAllPendingTasks() {
        for (InnerTaskControl taskControl : taskControls) {
            taskControl.tryUpdateTaskStage(PENDING, CANCELLED);
        }
    }

    @Override
    protected boolean checkAllTasksFinished() {
        for (InnerTaskControl taskControl : taskControls) {
            TaskControl.TaskStage taskStage = taskControl.getTaskStage();
            if (taskStage != COMPLETE && taskStage != CANCELLED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getTaskCount() {
        return taskControls.size();
    }

    @Override
    public Tasks getTasks() {
        lock.lock();
        try {
            SeparatedTasks tasks = new SeparatedTasks(taskControls.size());
            for (InnerTaskControl taskControl : taskControls) {
                tasks.add(new TaskControlShell(taskControl));
            }
            return tasks;
        } finally {
            lock.unlock();
        }
    }
}
