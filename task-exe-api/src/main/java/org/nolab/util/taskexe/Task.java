package org.nolab.util.taskexe;

import org.nolab.util.context.Context;

/**
 * This interface represents any tasks, that requires some
 * context, and, maybe, changes it, during execution.
 * May be useful for implementing by functions, that requires
 * complex parameters and produce complex results.
 */
@FunctionalInterface
public interface Task {

    /**
     * Execute task, using context for providing arguments
     * and store results.
     *
     * @param context task context
     * @throws Throwable any throwable in execution
     */
    void execute(Context context) throws Throwable;
}
