package edu.buffalo.cse.blue.maybe.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.ast.Assign.Operator;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;

/**
 * A {@code LocalAssign} represents a Java assignment expression
 * to a local variable.  For instance, {@code x = e}.
 *
 * The class of the {@code Expr} returned by
 * {@code LocalAssign_c.left()}is guaranteed to be an {@code Local}.
 */
public class MaybeLocalAssign_c extends MaybeAssign_c implements MaybeLocalAssign {
    private static final long serialVersionUID = SerialVersionUID.generate();

//    @Deprecated
    public MaybeLocalAssign_c(Position pos, Local left, Operator op, Expr maybeLabel, List<Expr> alternatives) {
        this(pos, left, op, maybeLabel, alternatives, null);
    }

    public MaybeLocalAssign_c(Position pos, Local left, Operator op, Expr maybeLabel, List<Expr> alternatives,
            Ext ext) {
        super(pos, left, op, maybeLabel, alternatives, ext);
    }

    @Override
    public Local left() {
        return (Local) super.left();
    }

    @Override
    public MaybeAssign left(Expr left) {
        assertLeftType(left);
        return super.left(left);
    }

    private static void assertLeftType(Expr left) {
        if (!(left instanceof Local)) {
            throw new InternalCompilerError("left expression of an LocalAssign must be a local");
        }
    }

    @Override
    public Term firstChild() {
        if (operator() != Assign.ASSIGN) {
            return left();
        }

        return alternatives().get(0);
    }

    @Override
    protected void acceptCFGAssign(CFGBuilder<?> v) {
        // do not visit left()
        // l = e: visit e -> (l = e)
        for (Expr expr : alternatives()) {
            v.visitCFG(expr, this, EXIT);
        }
    }

    @Override
    protected void acceptCFGOpAssign(CFGBuilder<?> v) {
        /*
        Local l = (Local)left();

        // l OP= e: visit l -> e -> (l OP= e)
        v.visitThrow(l);
        v.edge(l, alternatives().entry());
        v.visitCFG(alternatives(), this);
        */

        for (Expr expr : alternatives()) {
            v.visitCFG(left(), expr, ENTRY);
            v.visitCFG(expr, this, EXIT);
        }
    }
}
