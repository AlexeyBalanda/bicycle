package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class LockSafeSynchContextShellTestCase
        extends AbstractContextShellTestCase<LockSafeSynchContextShell> {

    @Override
    protected Function<Context, LockSafeSynchContextShell> getShellConstructor() {
        return LockSafeSynchContextShell::new;
    }
}
