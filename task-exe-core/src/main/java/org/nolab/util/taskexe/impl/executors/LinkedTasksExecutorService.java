package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;

import java.util.concurrent.Executor;

import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * A {@link ExecutorProxyTaskExecutorService} with linked task controls
 * iterator returned by {@link #getTasks()}.
 * Task controls are iterated in order, they were submitted.
 */
public class LinkedTasksExecutorService extends ExecutorProxyTaskExecutorService {

    private volatile int taskCount = 0;
    private final TaskControlNode head = new TaskControlNode(null, null, null);
    private volatile TaskControlNode tail = head;

    /**
     * Call to super.
     */
    public LinkedTasksExecutorService(Executor executor, boolean tryShutdownExecutor) {
        super(executor, tryShutdownExecutor);
    }

    /**
     * Call to super.
     */
    public LinkedTasksExecutorService(Executor executor) {
        super(executor);
    }

    @Override
    protected InnerTaskControl createTaskControl(Task task, Context context) {
        TaskControlNode node = new TaskControlNode(task, context, this);
        if (++taskCount < 0) {
            taskCount--;
            throw new Error("Task count overflow");
        }
        tail.next = node;
        node.prev = tail;
        tail = node;
        return node;
    }

    @Override
    protected void utilizeTaskControl(InnerTaskControl taskControl) {
        TaskControlNode node = (TaskControlNode) taskControl;
        if (node == tail) {
            tail = node.prev;
        }
        node.remove();
        taskCount--;
    }

    @Override
    protected void cancelAllPendingTasks() {
        TaskControlNode node = head.next;
        while (node != null) {
            node.tryUpdateTaskStage(PENDING, CANCELLED);
            node = node.next;
        }
    }

    @Override
    protected boolean checkAllTasksFinished() {
        TaskControlNode node = head.next;
        while (node != null) {
            TaskControl.TaskStage taskStage = node.getTaskStage();
            if (taskStage != COMPLETE && taskStage != CANCELLED) {
                return false;
            }
            node = node.next;
        }
        return true;
    }

    @Override
    public int getTaskCount() {
        return taskCount;
    }

    @Override
    public Tasks getTasks() {
        return new LinkedTasks(head);
    }
}
