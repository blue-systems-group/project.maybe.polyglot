package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.ast.Assign.Operator;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;

/**
 * A {@code ArrayAccessAssign_c} represents a Java assignment expression
 * to an array element.  For instance, {@code A[3] = e}.
 *
 * The class of the {@code Expr} returned by
 * {@code ArrayAccessAssign_c.left()}is guaranteed to be an
 * {@code ArrayAccess}.
 */
public class MaybeArrayAccessAssign_c extends MaybeAssign_c implements MaybeArrayAccessAssign {
    private static final long serialVersionUID = SerialVersionUID.generate();

//    @Deprecated
    public MaybeArrayAccessAssign_c(Position pos, ArrayAccess left, Operator op,
            Expr maybeLabel, List<Expr> alternatives) {
        this(pos, left, op, maybeLabel, alternatives, null);
    }

    public MaybeArrayAccessAssign_c(Position pos, ArrayAccess left, Operator op,
            Expr maybeLabel, List<Expr> alternatives, Ext ext) {
        super(pos, left, op, maybeLabel, alternatives, ext);
    }

    @Override
    public ArrayAccess left() {
        return (ArrayAccess) super.left();
    }

    @Override
    public MaybeAssign left(Expr left) {
        assertLeftType(left);
        return super.left(left);
    }

    private void assertLeftType(Expr left) {
        if (!(left instanceof ArrayAccess)) {
            throw new InternalCompilerError("left expression of an ArrayAccessAssign must be an array access");
        }
    }

    @Override
    public Term firstChild() {
        if (operator() == ASSIGN) {
            return left().array();
        }
        else {
            return left();
        }
    }

    @Override
    protected void acceptCFGAssign(CFGBuilder<?> v) {
        ArrayAccess a = left();

        //    a[i] = e: visit a -> i -> e -> (a[i] = e)
        for (Expr expr : alternatives()) {
            v.visitCFG(a.array(), a.index(), ENTRY);
            v.visitCFG(a.index(), expr, ENTRY);
            v.visitCFG(expr, this, EXIT);
        }
    }

    @Override
    protected void acceptCFGOpAssign(CFGBuilder<?> v) {
        /*
        ArrayAccess a = (ArrayAccess)left();

        // a[i] OP= e: visit a -> i -> a[i] -> e -> (a[i] OP= e)
        v.visitCFG(a.array(), a.index().entry());
        v.visitCFG(a.index(), a);
        v.visitThrow(a);
        v.edge(a, alternatives().entry());
        v.visitCFG(alternatives(), this);
        */

        for (Expr expr : alternatives()) {
            v.visitCFG(left(), expr, ENTRY);
            v.visitCFG(expr, this, EXIT);
        }
    }

    @Override
    public List<Type> throwTypes(TypeSystem ts) {
        List<Type> l = new ArrayList<>(super.throwTypes(ts));

        if (op == ASSIGN && left.type().isReference()) {
            l.add(ts.ArrayStoreException());
        }

        l.add(ts.NullPointerException());
        l.add(ts.OutOfBoundsException());

        return l;
    }

}
