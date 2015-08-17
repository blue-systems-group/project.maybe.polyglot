package edu.buffalo.cse.blue.maybe.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.ast.Assign.Operator;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.TypeChecker;

/**
 * A {@code AmbAssign} represents a Java assignment expression to
 * an ambiguous expression.
 */
public class MaybeAmbAssign_c extends MaybeAssign_c implements MaybeAmbAssign {
    private static final long serialVersionUID = SerialVersionUID.generate();

//    @Deprecated
    public MaybeAmbAssign_c(Position pos, Expr left, Operator op, Expr maybeLabel, List<Expr> right) {
        this(pos, left, op, maybeLabel, right, null);
    }

    public MaybeAmbAssign_c(Position pos, Expr left, Operator op, Expr maybeLabel, List<Expr> right, Ext ext) {
        super(pos, left, op, maybeLabel, right, ext);
    }

    @Override
    public Term firstChild() {
        if (operator() != Assign.ASSIGN) {
            return left();
        }

        return right().get(0);
    }

    @Override
    protected void acceptCFGAssign(CFGBuilder<?> v) {
        for (Expr e : right()) {
            v.visitCFG(e, this, EXIT);
        }
    }

    @Override
    protected void acceptCFGOpAssign(CFGBuilder<?> v) {
        for (Expr e : right()) {
            v.visitCFG(left(), e, ENTRY);
            v.visitCFG(e, this, EXIT);
        }
    }

    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        MaybeAssign n = (MaybeAssign) super.disambiguate(ar);

        if (n.left() instanceof Local) {
            return ((MaybeNodeFactory) ar.nodeFactory()).MaybeLocalAssign(n.position(),
                                                (Local) left(),
                                                operator(),
                                                label(),
                                                right());
        }
        else if (n.left() instanceof Field) {
            return ((MaybeNodeFactory) ar.nodeFactory()).MaybeFieldAssign(n.position(),
                                                (Field) left(),
                                                operator(),
                                                label(),
                                                right());
        }
        else if (n.left() instanceof ArrayAccess) {
            return ((MaybeNodeFactory) ar.nodeFactory()).MaybeArrayAccessAssign(n.position(),
                                                      (ArrayAccess) left(),
                                                      operator(),
                                                      label(),
                                                      right());
        }

        // LHS is still ambiguous.  The pass should get rerun later.
        return this;
        // throw new SemanticException("Could not disambiguate left side of assignment!", n.position());
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // Didn't finish disambiguation; just return.
        return this;
    }

    @Override
    public Node copy(NodeFactory nf) {
        return ((MaybeNodeFactory) nf).MaybeAmbAssign(this.position, this.left, this.op, this.maybeLabel, this.right);
    }
}
