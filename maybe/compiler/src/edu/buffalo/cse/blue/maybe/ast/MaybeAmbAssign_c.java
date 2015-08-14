package edu.buffalo.cse.blue.maybe.ast;

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
    public MaybeAmbAssign_c(Position pos, Expr left, Operator op, Expr right) {
        this(pos, left, op, right, null);
    }

    public MaybeAmbAssign_c(Position pos, Expr left, Operator op, Expr right, Ext ext) {
        super(pos, left, op, right, ext);
    }

    @Override
    public Term firstChild() {
        if (operator() != Assign.ASSIGN) {
            return left();
        }

        return right();
    }

    @Override
    protected void acceptCFGAssign(CFGBuilder<?> v) {
        v.visitCFG(right(), this, EXIT);
    }

    @Override
    protected void acceptCFGOpAssign(CFGBuilder<?> v) {
        v.visitCFG(left(), right(), ENTRY);
        v.visitCFG(right(), this, EXIT);
    }

    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        MaybeAssign n = (MaybeAssign) super.disambiguate(ar);

        if (n.left() instanceof Local) {
            return ar.nodeFactory().LocalAssign(n.position(),
                                                (Local) left(),
                                                operator(),
                                                right());
        }
        else if (n.left() instanceof Field) {
            return ar.nodeFactory().FieldAssign(n.position(),
                                                (Field) left(),
                                                operator(),
                                                right());
        }
        else if (n.left() instanceof ArrayAccess) {
            return ar.nodeFactory().ArrayAccessAssign(n.position(),
                                                      (ArrayAccess) left(),
                                                      operator(),
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
        return nf.AmbAssign(this.position, this.left, this.op, this.right);
    }
}
