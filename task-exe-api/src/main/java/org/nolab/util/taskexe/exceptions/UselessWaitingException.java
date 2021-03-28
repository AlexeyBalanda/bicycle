package org.nolab.util.taskexe.exceptions;

import static org.nolab.util.taskexe.TaskControl.TaskStage;

/**
 * Thrown if trying to wait for next task stage with stage,
 * that has no next stages.
 */
public class UselessWaitingException extends Exception {

    private static final long serialVersionUID = 2882527311378349947L;

    /**
     * Create exception with current stage,
     * that has no next stages.
     *
     * @param stage current stage
     */
    public UselessWaitingException(TaskStage stage) {
        super(stage.toString());
    }
}
