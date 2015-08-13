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

    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overridden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
    @Override
    public Maybe Maybe(Position pos, Expr cond, Block consequent, List<Block> alternatives) {
        Maybe n = new Maybe_c(pos, cond, consequent, alternatives);
        n = ext(n, extFactory().extIf());
        n = del(n, delFactory().delIf());
        return n;
    }
}
