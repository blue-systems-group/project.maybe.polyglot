package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.util.*;

import java.util.*;

/**
 * NodeFactory for maybe extension.
 */
public class MaybeNodeFactory_c extends NodeFactory_c implements MaybeNodeFactory {
    public MaybeNodeFactory_c(MaybeLang lang, MaybeExtFactory extFactory) {
        super(lang, extFactory);
    }

    @Override
    public MaybeExtFactory extFactory() {
        return (MaybeExtFactory) super.extFactory();
    }

    @Override
    public ConstArrayTypeNode ConstArrayTypeNode(Position pos, TypeNode base) {
        ConstArrayTypeNode_c n = new ConstArrayTypeNode_c(pos, base);
        n = ext(n, extFactory().extConstArrayTypeNode());
        return n;
    }

    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overridden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
