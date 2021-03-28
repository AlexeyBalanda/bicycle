package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class BaseImmutableContextShellTestCase
        extends AbstractImmutableShellContextTestCase<BaseImmutableContextShell> {

    @Override
    protected Function<Context, BaseImmutableContextShell> getShellConstructor() {
        return BaseImmutableContextShell::new;
    }
}
