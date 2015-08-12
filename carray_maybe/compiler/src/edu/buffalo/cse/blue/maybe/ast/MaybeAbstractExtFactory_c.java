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

    @Override
    public final Ext extConstArrayTypeNode() {
        Ext e = extConstArrayTypeNodeImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof MaybeExtFactory) {
            e2 = ((MaybeExtFactory) nextEF).extConstArrayTypeNode();
        } else {
            e2 = nextEF.extArrayTypeNode();
        }

        e = composeExts(e, e2);
        return postExtConstArrayTypeNode(e);
    }

    protected Ext extConstArrayTypeNodeImpl() {
        return extArrayTypeNodeImpl();
    }

    protected Ext postExtConstArrayTypeNode(Ext e) {
        return postExtArrayTypeNode(e);
    }

    // TODO: Implement factory methods for new extension nodes in future
    // extensions.  This entails calling the factory method for extension's
    // AST superclass.
}
