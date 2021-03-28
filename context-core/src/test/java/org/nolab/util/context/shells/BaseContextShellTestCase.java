package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class BaseContextShellTestCase extends AbstractContextShellTestCase<BaseContextShell> {

    @Override
    protected Function<Context, BaseContextShell> getShellConstructor() {
        return BaseContextShell::new;
    }
}
