package org.nolab.util.taskexe.impl.tasks;

import org.nolab.util.context.Context;
import org.nolab.util.taskexe.InterruptableTask;

/**
 * Abstract implementation of {@link InterruptableTask} with
 * provided method for checking task interruption.
 * After finishing or interrupting task instance cannot be restarted.
 *
 *
 * <p>
 * It may be used like this:
 * <p>
 * {@code
 *   InterruptableTask task = new AbstractInterruptableTask() {
 *       {@literal @}Override
 *       protected void doExecute(Context context) throws Throwable {
 *           //some action
 *           checkpoint();
 *           //some another action
 *       }
 *   }
 * }
 */
public abstract class AbstractInterruptableTask implements InterruptableTask {

    /**
     * Signal to interrupt task execution.
     */
    private static class InterruptSignal extends Error {
    }

    private volatile boolean started = false;
    private volatile boolean orderedToInterrupt = false;
    private volatile boolean interrupted = false;

    /**
     * Point of checking interruption.
     */
    protected final void checkpoint() {
        if (orderedToInterrupt) {
            interrupted = true;
            throw new InterruptSignal();
        }
    }

    /**
     * This method should be overridden instead of {@link #execute(Context)}.
     */
    protected abstract void doExecute(Context context) throws Throwable;

    @Override
    public final void execute(Context context) throws Throwable {
        started = true;
        try {
            doExecute(context);
        } catch (Throwable e) {
            if (!(e instanceof InterruptSignal)) {
                throw e;
            }
        }
    }

    @Override
    public final void interrupt() {
        if (started) {
            orderedToInterrupt = true;
        }
    }

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }
}
