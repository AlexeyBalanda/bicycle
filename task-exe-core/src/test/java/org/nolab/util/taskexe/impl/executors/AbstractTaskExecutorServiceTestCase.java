package org.nolab.util.taskexe.impl.executors;

import org.junit.Test;
import org.nolab.util.context.Context;
import org.nolab.util.taskexe.*;
import org.nolab.util.taskexe.TaskControl.TaskStage;
import org.nolab.util.taskexe.exceptions.DeniedExecutionException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;
import static org.nolab.util.taskexe.TaskControl.TaskStage.*;

/**
 * Base class for testing {@link TaskExecutorService}.
 *
 * @param <T> implementation
 */
public abstract class AbstractTaskExecutorServiceTestCase<T extends TaskExecutorService>
        extends AbstractTaskExecutorTestCase<T> {

    @Test
    public void testTasks() throws Exception {

        final T taskExecutorService = getAsynchronousSingleThreadTaskExecutor();
        final Context context = getContext();
        final Phaser phaser = new Phaser(2);
        final Queue<TaskControl> taskControls = new LinkedList<>();
        final int maxTasks = 5;

        final Task task = c -> c.get(Phaser.class).arriveAndAwaitAdvance();

        int count;

        context.put(phaser);

        for (int i = 0; i < maxTasks; i++) {
            taskControls.add(taskExecutorService.execute(task, context));
        }
        assertEquals(maxTasks, taskExecutorService.getTaskCount());

        count = 0;
        int runningTasks = 0;
        for (TaskControl tc : taskExecutorService.getTasks()) {
            count++;
            TaskStage stage = tc.getTaskStage();
            if (stage.equals(RUNNING)) {
                assertEquals(1, ++runningTasks);
            } else {
                assertEquals(PENDING, stage);
            }
        }
        assertEquals(maxTasks, count);

        for (int i = 0; i < maxTasks; i++) {
            phaser.arriveAndAwaitAdvance();
        }

        count = 0;
        for (TaskControl tc : taskExecutorService.getTasks()) {
            count++;
            TaskStage stage = tc.awaitNextStage(RUNNING);
            assertEquals(COMPLETE, stage);
        }
        assertTrue(count <= maxTasks);
        assertTrue(taskExecutorService.getTaskCount() <= maxTasks);
        assertTrue(count >= taskExecutorService.getTaskCount());

        for (TaskControl tc : taskControls) {
            assertEquals(COMPLETE, tc.getTaskStage());
        }
    }

    @Test
    public void testShutdown() throws Exception {

        final T taskExecutorService = getAsynchronousSingleThreadTaskExecutor();
        final Context context = getContext();
        final Phaser phaser = new Phaser(2);
        final Queue<TaskControl> taskControls = new LinkedList<>();
        final int executedTasksCount = 5;
        final long timeout = 100;
        final long timeoutNanos = 100000;

        final Task task = c -> c.get(Phaser.class).arriveAndAwaitAdvance();

        context.put(phaser);

        for (int i = 0; i < executedTasksCount; i++) {
            taskControls.add(taskExecutorService.execute(task, context));
        }
        assertEquals(executedTasksCount, taskExecutorService.getTaskCount());

        assertFalse(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        long start;

        start = System.currentTimeMillis();
        assertFalse(taskExecutorService.awaitTermination(timeout));
        assertTrue(System.currentTimeMillis() - start >= timeout);

        taskExecutorService.shutdown();

        assertTrue(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        start = System.currentTimeMillis();
        assertFalse(taskExecutorService.awaitTermination(timeout));
        assertTrue(System.currentTimeMillis() - start >= timeout);

        start = System.currentTimeMillis();
        assertFalse(taskExecutorService.awaitTermination(timeoutNanos, NANOSECONDS));
        assertTrue(System.currentTimeMillis() - start >= NANOSECONDS.toMillis(timeoutNanos));

        assertTrue(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        try {
            taskControls.add(taskExecutorService.execute(task, context));
            fail();
        } catch (DeniedExecutionException e) {
        }

        for (int i = 0; i < executedTasksCount; i++) {
            phaser.arriveAndAwaitAdvance();
        }

        taskExecutorService.awaitTermination();

        assertFalse(taskExecutorService.isTerminating());
        assertTrue(taskExecutorService.isTerminated());

        for (TaskControl tc : taskControls) {
            assertEquals(COMPLETE, tc.getTaskStage());
        }

        try {
            taskControls.add(taskExecutorService.execute(task, context));
            fail();
        } catch (DeniedExecutionException e) {
        }
    }

    @Test
    public void testShutdownNow() throws Exception {

        final T taskExecutorService = getAsynchronousSingleThreadTaskExecutor();
        final Context context = getContext();
        final Phaser phaser = new Phaser(2);
        final Queue<TaskControl> taskControls = new LinkedList<>();
        final int executedTasksCount = 3;
        final int cancelledTasksCount = 3;
        final int allTasksCount = executedTasksCount + cancelledTasksCount;
        final long timeout = 100;
        final long timeoutNanos = 100000;

        final Task task = c -> c.get(Phaser.class).arriveAndAwaitAdvance();

        context.put(phaser);

        for (int i = 0; i < allTasksCount; i++) {
            taskControls.add(taskExecutorService.execute(task, context));
        }
        assertEquals(allTasksCount, taskExecutorService.getTaskCount());

        assertFalse(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        for (int i = 0; i < executedTasksCount - 1; i++) {
            phaser.arriveAndAwaitAdvance();
        }

        assertFalse(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        long start;

        start = System.currentTimeMillis();
        assertFalse(taskExecutorService.awaitTermination(timeout));
        assertTrue(System.currentTimeMillis() - start >= timeout);

        start = System.currentTimeMillis();
        assertFalse(taskExecutorService.awaitTermination(timeoutNanos, NANOSECONDS));
        assertTrue(System.currentTimeMillis() - start >= NANOSECONDS.toMillis(timeoutNanos));

        taskExecutorService.shutdownNow();

        try {
            taskControls.add(taskExecutorService.execute(task, context));
            fail();
        } catch (DeniedExecutionException e) {
        }

        assertTrue(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        start = System.currentTimeMillis();
        assertFalse(taskExecutorService.awaitTermination(timeout));
        assertTrue(System.currentTimeMillis() - start >= timeout);

        assertTrue(taskExecutorService.isTerminating());
        assertFalse(taskExecutorService.isTerminated());

        phaser.arriveAndAwaitAdvance();

        taskExecutorService.awaitTermination();

        assertFalse(taskExecutorService.isTerminating());
        assertTrue(taskExecutorService.isTerminated());

        int etc = 0;
        int ctc = 0;
        for (TaskControl tc : taskControls) {
            TaskStage ts = tc.getTaskStage();
            if (ts.equals(COMPLETE)) {
                etc++;
            } else if (ts.equals(CANCELLED)) {
                ctc++;
            } else {
                fail();
            }
        }
        assertEquals(executedTasksCount, etc);
        assertEquals(cancelledTasksCount, ctc);

        try {
            taskControls.add(taskExecutorService.execute(task, context));
            fail();
        } catch (DeniedExecutionException e) {
        }
    }

    @Test
    public void testAwaitAction() throws Throwable {

        final T taskExecutorService = getAsynchronousSingleThreadTaskExecutor();
        final Context context = getContext();
        final AtomicInteger notedActions = new AtomicInteger(0);
        final AtomicInteger detectedActions = new AtomicInteger(0);
        final AtomicInteger maxActions = new AtomicInteger(0);
        final AtomicReference<Throwable> errorRef = new AtomicReference<>();
        final Phaser phaser = new Phaser(2);
        final long timeout = 100;
        final long timeoutNanos = MILLISECONDS.toNanos(timeout);
        final long pause = timeout / 2;

        final Task task = c -> c.get(Phaser.class).arriveAndAwaitAdvance();
        final Task failedTask = c -> {
            c.get(Phaser.class).arriveAndAwaitAdvance();
            throw new Exception();
        };

        final Thread actionsWaiter = new Thread(() -> {
            try {
                Object statePoint = taskExecutorService.awaitAction(null);
                Object newStatePoint;
                int variant = 0;
                long start;
                long time;

                phaser.arriveAndAwaitAdvance();
                while (!taskExecutorService.isTerminated()) {
                    variant = (++variant) % 3;
                    switch (variant) {
                        case 1:
                            start = System.currentTimeMillis();
                            newStatePoint = taskExecutorService.awaitAction(statePoint, timeout);
                            time = System.currentTimeMillis() - start;
                            assertTrue(time < timeout);
                            break;
                        case 2:
                            start = System.currentTimeMillis();
                            newStatePoint = taskExecutorService.awaitAction(statePoint, timeoutNanos, NANOSECONDS);
                            time = System.currentTimeMillis() - start;
                            assertTrue(time < NANOSECONDS.toMillis(timeoutNanos));
                            break;
                        default:
                            start = System.currentTimeMillis();
                            newStatePoint = taskExecutorService.awaitAction(statePoint);
                            time = System.currentTimeMillis() - start;
                            assertTrue(time < timeout);
                    }
                    assertNotEquals(statePoint, newStatePoint);
                    statePoint = newStatePoint;
                    detectedActions.incrementAndGet();
                }

                start = System.currentTimeMillis();
                assertEquals(statePoint, taskExecutorService.awaitAction(statePoint, timeout));
                time = System.currentTimeMillis() - start;
                assertTrue(time >= timeout);

                start = System.currentTimeMillis();
                assertEquals(statePoint, taskExecutorService.awaitAction(statePoint, timeoutNanos, NANOSECONDS));
                time = System.currentTimeMillis() - start;
                assertTrue(time >= NANOSECONDS.toMillis(timeoutNanos));
            } catch (InterruptedException e) {
                errorRef.set(e);
            }
        });
        actionsWaiter.setUncaughtExceptionHandler((t, e) -> {
            errorRef.set(e);
        });
        actionsWaiter.start();

        context.put(phaser);

        TaskControl taskControl;

        phaser.arriveAndAwaitAdvance();

        taskExecutorService.execute(task, context);
        notedActions.incrementAndGet();
        maxActions.addAndGet(2);

        Thread.sleep(pause);

        taskControl = taskExecutorService.execute(task, context);
        notedActions.incrementAndGet();
        maxActions.addAndGet(2);

        Thread.sleep(pause);

        taskControl.cancelTask();
        notedActions.incrementAndGet();
        maxActions.addAndGet(2);

        Thread.sleep(pause);

        taskExecutorService.execute(failedTask, context);
        notedActions.incrementAndGet();
        maxActions.addAndGet(2);

        Thread.sleep(pause);

        taskExecutorService.execute(task, context);
        notedActions.incrementAndGet();
        maxActions.addAndGet(2);

        Thread.sleep(pause);

        phaser.arriveAndAwaitAdvance();
        notedActions.incrementAndGet();
        maxActions.addAndGet(1);

        Thread.sleep(pause);

        phaser.arriveAndAwaitAdvance();
        notedActions.incrementAndGet();
        maxActions.addAndGet(1);

        Thread.sleep(pause);

        taskExecutorService.shutdown();
        notedActions.incrementAndGet();
        maxActions.addAndGet(1);

        Thread.sleep(pause);

        phaser.arriveAndAwaitAdvance();
        notedActions.incrementAndGet();
        maxActions.addAndGet(1);

        actionsWaiter.join();

        Throwable e = errorRef.get();
        if (e != null) {
            throw e;
        }

        assertTrue(notedActions.get() <= detectedActions.get());
        assertTrue(detectedActions.get() <= maxActions.get());
    }
}
