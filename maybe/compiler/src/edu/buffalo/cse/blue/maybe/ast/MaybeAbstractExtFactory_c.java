package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.AbstractExtFactory_c;
import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;

public abstract class MaybeAbstractExtFactory_c extends AbstractExtFactory_c
        implements MaybeExtFactory {

    public MaybeAbstractExtFactory_c() {
        super();
    }

    public MaybeAbstractExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    // TODO: Implement factory methods for new extension nodes in future
    // extensions.  This entails calling the factory method for extension's
    // AST superclass.
}
