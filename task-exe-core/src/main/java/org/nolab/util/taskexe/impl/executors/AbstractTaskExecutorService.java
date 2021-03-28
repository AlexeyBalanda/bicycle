package org.nolab.util.taskexe.impl.executors;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.impl.executors.shells.TaskControlShell;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * An abstract {@link TaskExecutorService} with common functional.
 */
public abstract class AbstractTaskExecutorService implements TaskExecutorService {

    /**
     * A {@link TaskControlImpl}, bounded to task executor state.
     */
    protected class InnerTaskControl extends TaskControlImpl {

        /**
         * Call to super.
         */
        public InnerTaskControl(Task task, Context context, TaskExecutor executor) {
            super(task, context, executor);
        }

        @Override
        public void cancelTask() {
            super.cancelTask();
            AbstractTaskExecutorService.this.lock.lock();
            try {
                AbstractTaskExecutorService.this.statePoint = new Object();
                AbstractTaskExecutorService.this.condition.signalAll();
            } finally {
                AbstractTaskExecutorService.this.lock.unlock();
            }
        }
    }

    /**
     * A {@link Tasks} implementation, separated of executor state.
     */
    protected static class SeparatedTasks implements Iterator<TaskControl>, Tasks {

        private TaskControl[] taskControls;
        int cur;
        int end;

        /**
         * Create new iterator with given initial capacity.
         *
         * @param capacity initial capacity
         */
        public SeparatedTasks(int capacity) {
            taskControls = new TaskControl[capacity];
            cur = end = -1;
        }

        public void add(TaskControl taskControl) {
            int i = end + 1;
            if (i >= taskControls.length) {
                taskControls = new TaskControl[i + 1];
            }
            end = i;
            taskControls[i] = taskControl;
        }

        @Override
        public boolean hasNext() {
            return cur < end;
        }

        @Override
        public TaskControl next() {
            if (cur >= end) {
                throw new NoSuchElementException();
            }
            return taskControls[++cur];
        }

        @Override
        public Iterator<TaskControl> iterator() {
            return this;
        }
    }

    /**
     * A {@link InnerTaskControl}, able to be chained with others.
     */
    protected class TaskControlNode extends InnerTaskControl {
        
        public volatile TaskControlNode prev;
        public volatile TaskControlNode next;

        /**
         * Call to super.
         */
        public TaskControlNode(Task task, Context context, TaskExecutor executor) {
            super(task, context, executor);
        }

        /**
         * Remove node from chain.
         * Next and previous nodes, if exists
         * will lose reference to it.
         */
        public void remove() {
            if (prev != null) {
                prev.next = next;
            }
            if (next != null) {
                next.prev = prev;
            }
        }
    }

    /**
     * A {@link Tasks} implementation that uses chained task controls
     * to iterate.
     */
    protected static class LinkedTasks implements Iterator<TaskControl>, Tasks {

        private volatile TaskControlNode cur;
        private volatile TaskControlNode next;

        /**
         * Construct iterator with zero node.
         * Pointer is set on zero node.
         * Zero node never returned by {@link #next()}
         *
         * @param zero zero node
         */
        public LinkedTasks(TaskControlNode zero) {
            cur = zero;
            next = cur.next;
        }

        @Override
        public boolean hasNext() {
            next = cur.next;
            return next != null;
        }

        @Override
        public TaskControl next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            TaskControlNode node = next;
            cur = next;
            next = cur.next;
            return new TaskControlShell(node);
        }

        @Override
        public Iterator<TaskControl> iterator() {
            return this;
        }
    }

    protected final Lock lock = new ReentrantLock(true);
    protected final Condition condition = lock.newCondition();

    protected volatile boolean terminating = false;
    protected volatile boolean terminated = false;

    protected volatile Object statePoint = new Object();

    @Override
    public boolean isTerminating() {
        return terminating;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        lock.lock();
        try {
            while (!terminated) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean awaitTermination(long timeout) throws InterruptedException {
        return awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
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
            while (!terminated) {
                if ((nanosTimeout = nanosTimeout - (System.nanoTime() - nanosStart)) <= 0) {
                    return false;
                }
                condition.awaitNanos(nanosTimeout);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object awaitAction(Object statePoint) throws InterruptedException {
        lock.lock();
        try {
            while (statePoint == this.statePoint) {
                condition.await();
            }
            return this.statePoint;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object awaitAction(Object statePoint, long timeout) throws InterruptedException {
        return awaitAction(statePoint, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object awaitAction(Object statePoint, long timeout, TimeUnit unit) throws InterruptedException {
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
            while (statePoint == this.statePoint) {
                if ((nanosTimeout = nanosTimeout - (System.nanoTime() - nanosStart)) <= 0) {
                    break;
                }
                condition.awaitNanos(nanosTimeout);
            }
            return this.statePoint;
        } finally {
            lock.unlock();
        }
    }
}
