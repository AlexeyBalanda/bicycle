package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class FullSyncContextShellTestCase extends AbstractContextShellTestCase<FullSyncContextShell> {

    @Override
    protected Function<Context, FullSyncContextShell> getShellConstructor() {
        return FullSyncContextShell::new;
    }
}
