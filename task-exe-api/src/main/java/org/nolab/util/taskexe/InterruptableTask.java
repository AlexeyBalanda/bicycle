package org.nolab.util.taskexe;

/**
 * A {@link Task} with ability to be interrupted in process of execution.
 */
public interface InterruptableTask extends Task {

    /**
     * Interrupt task execution.
     * No effect if task execution finished or not started yet.
     */
    void interrupt();

    /**
     * Return {@code true} if task execution was interrupted via
     * {@link #interrupt()} call.
     * If {@link #interrupt()} was not called or
     * call did not affected execution, return {@code false}.
     *
     * @return {@code true} if task was interrupted
     *         or {@code false} otherwise
     */
    boolean isInterrupted();
}
