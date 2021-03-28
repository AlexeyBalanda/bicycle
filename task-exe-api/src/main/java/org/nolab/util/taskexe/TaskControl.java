package org.nolab.util.taskexe;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.exceptions.UselessWaitingException;

import java.util.concurrent.TimeUnit;

/**
 * Interface, that provides monitoring and control actions over
 * task, submitted for execution in {@link TaskExecutor}.
 */
public interface TaskControl {

    /**
     * Current stage of task execution.
     * Each stage has possible previous and/or next
     * Each stage has level, defining possible
     * previous and next stages.
     * Task stage can be followed by one of possible next stages.
     */
    enum TaskStage {

        /**
         * Task submitted and awaiting execution.
         * No possible previous stages.
         * Possible next stages: {@link #RUNNING},
         * {@link #CANCELLED}, {@link #COMPLETE}, {@link #FAILED}.
         */
        PENDING(0),

        /**
         * Task execution is process now.
         * Possible previous stages: {@link #PENDING}.
         * Possible next stages: {@link #CANCELLED},
         * {@link #COMPLETE}, {@link #FAILED}
         */
        RUNNING(1),

        /**
         * Task execution cancelled.
         * It may be before execution or in time (if implementation supports).
         * Possible previous stages: {@link #PENDING}, {@link #RUNNING}.
         * No possible next stages.
         */
        CANCELLED(2),

        /**
         * Task execution finished normally.
         * Possible previous stages: {@link #PENDING}, {@link #RUNNING}.
         * No possible next stages.
         */
        COMPLETE(2),

        /**
         * Task execution finished abruptly with throwing {@link Throwable}.
         */
        FAILED(2);

        public static final int MIN_LEVEL = 0;
        public static final int MAX_LEVEL = 2;

        private final int level;

        /**
         * @param level stage level
         */
        TaskStage(int level) {
            this.level = level;
        }

        /**
         * Get level value of this stage.
         *
         * @return level value
         */
        public int getLevel() {
            return level;
        }

        /**
         * Is this stage may be before specified stage.
         *
         * @param stage stage to compare
         * @return {@code true} if this stage may be before specified stage
         *         or else {@code false}
         */
        public boolean isBefore(TaskStage stage) {
            return this.level < stage.level;
        }

        /**
         * Is this stage may be after specified stage.
         *
         * @param stage stage to compare
         * @return {@code true} if this stage may be after specified stage
         *         or else {@code false}
         */
        public boolean isAfter(TaskStage stage) {
            return this.level > stage.level;
        }

        /**
         * Is this stage may be followed by next stages.
         *
         * @return {@code true} if this stage may be preceded by previous stages
         *         or else {@code false}
         */
        public boolean hasBefore() {
            return level > MIN_LEVEL;
        }

        /**
         * Is this stage may be followed by next stages.
         *
         * @return {@code true} if this stage may be followed by next stages
         *         or else {@code false}
         */
        public boolean hasAfter() {
            return level < MAX_LEVEL;
        }
    }

    /**
     * Get controlled task instance.
     *
     * @return task instance
     */
    public Task getTask();

    /**
     * Get context instance for controlled task.
     *
     * @return context instance
     */
    Context getContext();

    /**
     * Get executor, controlled task submitted to.
     *
     * @return task executor
     */
    TaskExecutor getExecutor();

    /**
     * Get current task stage.
     *
     * @return task stage
     */
    TaskStage getTaskStage();

    /**
     * Get throwable, caused to finish abruptly.
     * If task is running or finished normally, returns {@code null}.
     *
     * @return cause of failure or {@code null}
     */
    Throwable getFailureCause();

    /**
     * Await until task reach stage, following after
     * specified start stage and return actual next stage.
     * Next stage may be one of possible next stages.
     *
     * @param startStage expected start stage
     * @return actual next stage
     * @throws NullPointerException if start stage is {@code null}
     * @throws UselessWaitingException if start stage has no possible
     *                                 next stages
     * @throws InterruptedException if current thread interrupted
     */
    TaskStage awaitNextStage(TaskStage startStage)
            throws UselessWaitingException, InterruptedException;

    /**
     * Await next stage for specified timeout in milliseconds
     * and return actual next stage.
     * If timeout elapsed or timeout is 0, current task stage is returned.
     * @see #awaitNextStage(TaskStage)
     *
     * @param startStage expected start stage
     * @param timeout timeout to wait in milliseconds
     * @return actual next stage or current stage if timeout elapsed or 0
     * @throws NullPointerException if start stage is {@code null}
     * @throws IllegalArgumentException if timeout lesser than 0
     * @throws UselessWaitingException if start stage has no possible
     *                                 next stages
     * @throws InterruptedException if current thread interrupted
     */
    TaskStage awaitNextStage(TaskStage startStage, long timeout)
            throws UselessWaitingException, InterruptedException;

    /**
     * Await next stage for specified timeout in specified time unit
     * and return actual next stage.
     * If timeout elapsed or timeout is 0, current task stage is returned.
     * @see #awaitNextStage(TaskStage)
     *
     * @param startStage expected start stage
     * @param timeout timeout to wait
     * @param unit time unit of timeout
     * @return actual next stage or current stage if timeout elapsed or 0
     * @throws NullPointerException if start stage or time unit is {@code null}
     * @throws IllegalArgumentException if timeout lesser than 0
     * @throws UselessWaitingException if start stage has no possible
     *                                 next stages
     * @throws InterruptedException if current thread interrupted
     */
    default TaskStage awaitNextStage(TaskStage startStage, long timeout, TimeUnit unit)
            throws UselessWaitingException, InterruptedException {
        return awaitNextStage(startStage, unit.toMillis(timeout));
    }

    /**
     * Attempt to cancel task execution.
     * Effect of this call depends on current task stage
     * and implementation, but task stage must not change
     * to previous.
     */
    void cancelTask();
}
