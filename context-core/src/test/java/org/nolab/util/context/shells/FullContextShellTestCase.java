package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class FullContextShellTestCase extends AbstractContextShellTestCase<FullContextShell> {

    @Override
    protected Function<Context, FullContextShell> getShellConstructor() {
        return FullContextShell::new;
    }
}
