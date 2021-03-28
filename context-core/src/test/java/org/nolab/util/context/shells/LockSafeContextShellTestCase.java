package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class LockSafeContextShellTestCase extends AbstractContextShellTestCase<LockSafeContextShell> {

    @Override
    protected Function<Context, LockSafeContextShell> getShellConstructor() {
        return LockSafeContextShell::new;
    }
}
