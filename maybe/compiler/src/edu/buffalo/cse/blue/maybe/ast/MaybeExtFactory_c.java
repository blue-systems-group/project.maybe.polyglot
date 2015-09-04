package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;

public final class MaybeExtFactory_c extends MaybeAbstractExtFactory_c {

    public MaybeExtFactory_c() {
        super();
    }

    public MaybeExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    @Override
    protected Ext extNodeImpl() {
        return new MaybeExt();
    }

    // TODO: Override factory methods for new extension nodes in the current
    // extension.
    @Override
    protected Ext extAssignImpl() {
        return new MaybeAssignExt();
    }
}
