package org.nolab.util.context.shells;

import org.nolab.util.context.Context;

import java.util.function.Function;

public class FullImmutableContextShellTestCase
        extends AbstractImmutableShellContextTestCase<FullImmutableContextShell> {

    @Override
    protected Function<Context, FullImmutableContextShell> getShellConstructor() {
        return FullImmutableContextShell::new;
    }
}
