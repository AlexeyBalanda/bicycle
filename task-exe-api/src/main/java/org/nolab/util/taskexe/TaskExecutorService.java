package org.nolab.util.taskexe;

import java.util.Spliterator;
import java.util.concurrent.TimeUnit;

/**
 * An {@link TaskExecutor}, that provides methods for monitoring
 * and termination of submitted tasks execution.
 */
public interface TaskExecutorService extends TaskExecutor {

    /**
     * "For-each" loop tool for submitted tasks.
     */
    interface Tasks extends Iterable<TaskControl> {

        /**
         * Not supported by default.
         */
        @Override
        default Spliterator<TaskControl> spliterator() {
            throw new UnsupportedOperationException("spliterator");
        }
    }

    /**
     * Get overall count of all tasks, exists in this service in any stage.
     *
     * @return count of all tasks
     */
    int getTaskCount();

    /**
     * Get "for-each" tool for all submitted tasks.
     * Order of tasks in "for-each" loop depends on implementation.
     * Returned {@link Tasks} reflects actual service state:
     * while "for-each" loop in process any task may be submitted,
     * but not got in loop, as well as task, got in loop, may be finished
     * and excluded from service before loop process ends.
     * This reflection is implementation dependent.
     *
     * @return iterable of submitted tasks
     */
    Tasks getTasks();

    /**
     * Stop service to accept new tasks for execution.
     * All submitted tasks will be executed in
     * implementation-depended order.
     * Method does not wait submitted tasks tasks to terminate.
     * No effect if this method or {@link #shutdownNow()}
     * already invoked.
     */
    void shutdown();

    /**
     * Stop service to accept new tasks for execution,
     * cancel all pending tasks.
     * Running tasks may be attempted to interrupt,
     * depends on implementation.
     * Method does not wait running tasks to terminate.
     * No effect if this method already invoked.
     */
    void shutdownNow();

    /**
     * Returns {@code true} if this service receive command
     * to shutdown and executing or cancelling already submitted
     * tasks.
     * If {@link #shutdown()} or {@link #shutdownNow()} not invoked
     * or all shutdown procedure finished, returns {@code false}.
     *
     * @return {@code true} if in process if termination
     *         or {@code false} otherwise
     */
    boolean isTerminating();

    /**
     * Return {@code true} if this service no more enable to execute
     * tasks and oll submitted tasks are finished or cancelled.
     *
     * @return {@code true} if terminated or {@code false} otherwise
     */
    boolean isTerminated();

    /**
     * Await until service will be terminated.
     * <p>
     * @see #isTerminated()
     *
     * @throws InterruptedException if current thread interrupted
     */
    void awaitTermination() throws InterruptedException;

    /**
     * Await for specified timeout in milliseconds
     * until service will be terminated.
     * Return {@code true} if service was terminated
     * or {@code false} if timeout elapsed.
     * If specified timeout is 0 result equal
     * to {@link #isTerminated()}.
     *
     * @param timeout timeout to wait in milliseconds
     * @return {@code true} if service was terminated
     *         or {@code false} if timeout elapsed
     * @throws IllegalArgumentException if specified timeout lesser than 0
     * @throws InterruptedException if current thread interrupted
     */
    boolean awaitTermination(long timeout) throws InterruptedException;

    /**
     * Await for specified timeout in specified time unit
     * until service will be terminated.
     * If specified timeout is 0 result equal
     * to {@link #isTerminated()}.
     * <p>
     * @see #awaitTermination(long)
     *
     * @param timeout timeout to wait
     * @param unit unit of timeout
     * @return {@code true} if service was terminated
     *         or {@code false} if timeout elapsed
     * @throws IllegalArgumentException if specified timeout lesser than 0
     * @throws NullPointerException if specified time unit is {@code null}
     * @throws InterruptedException if current thread interrupted
     */
     default boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
         return awaitTermination(unit.toMillis(timeout));
     }

    /**
     * Await any action in service: submission task for execution
     * or stage moving for any task or receiving shutdown command
     * or terminating.
     * <p>
     * To prevent action skipping between several calls and possibly
     * endless waiting expected point of state is specified.
     * Waiting continues while specified state point is equal to actual.
     * By default equality determines as {@code o1 == o2}.
     * Method returns actual state point, that must not be {@code null}.
     * If specified expected state point is {@code null}, actual point of state
     * is instantly returned.
     * <p>
     * State change monitoring may be processed like this:
     * <p>
     * {@code
     *     Object statePoint = null;
     *     while (someCondition) {
     *         statePoint = taskExecutorService.awaitAction(statePoint);
     *         someCondition = ... //any check actions
     *     }
     * }
     *
     * @param statePoint expected point of state
     * @return actual point of state
     * @throws InterruptedException if current thread interrupted
     */
     Object awaitAction(Object statePoint) throws InterruptedException;

    /**
     * Await any action in service for specified amount in milliseconds.
     * <p>
     * @see #awaitAction(Object)
     * <p>
     *
     * @param statePoint expected point of state
     * @param timeout timeout to wait in milliseconds
     * @return actual point of state
     * @throws IllegalArgumentException if specified timeout lesser than 0
     * @throws InterruptedException if current thread interrupted
     */
     Object awaitAction(Object statePoint, long timeout) throws InterruptedException;

    /**
     * Await any action in service for specified amount of specified
     * time unit.
     * <p>
     * @see #awaitAction(Object)
     * <p>
     *
     * @param statePoint expected point of state
     * @param timeout timeout to wait
     * @param unit unit of timeout
     * @return actual point of state
     * @throws NullPointerException if specified time unit is {@code null}
     * @throws InterruptedException if current thread interrupted
     */
     default Object awaitAction(Object statePoint, long timeout, TimeUnit unit)
             throws InterruptedException {
         return awaitAction(statePoint, unit.toMillis(timeout));
     }
}
