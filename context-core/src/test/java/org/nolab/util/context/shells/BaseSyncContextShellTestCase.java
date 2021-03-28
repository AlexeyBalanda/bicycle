package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class BaseSyncContextShellTestCase extends AbstractContextShellTestCase<BaseSyncContextShell> {

    @Override
    protected Function<Context, BaseSyncContextShell> getShellConstructor() {
        return BaseSyncContextShell::new;
    }
}
