package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.ast.Assign.Operator;

import java.util.ArrayList;
import java.util.List;

import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;

/**
 * A {@code FieldAssign} represents a Java assignment expression to
 * a field.  For instance, {@code this.x = e}.
 *
 * The class of the {@code Expr} returned by
 * {@code FieldAssign_c.left()}is guaranteed to be a {@code Field}.
 */
public class MaybeFieldAssign_c extends MaybeAssign_c implements MaybeFieldAssign {
    private static final long serialVersionUID = SerialVersionUID.generate();

//    @Deprecated
    public MaybeFieldAssign_c(Position pos, Field left, Operator op, Expr right) {
        this(pos, left, op, right, null);
    }

    public MaybeFieldAssign_c(Position pos, Field left, Operator op, Expr right,
            Ext ext) {
        super(pos, left, op, right, ext);
    }

    @Override
    public Field left() {
        return (Field) super.left();
    }

    @Override
    public MaybeAssign left(Expr left) {
        assertLeftType(left);
        return super.left(left);
    }

    private static void assertLeftType(Expr left) {
        if (!(left instanceof Field)) {
            throw new InternalCompilerError("left expression of an FieldAssign must be a field");
        }
    }

    @Override
    public Term firstChild() {
        Field f = left();
        if (f.target() instanceof Expr) {
            return (Expr) f.target();
        }
        else {
            if (operator() != Assign.ASSIGN) {
                return f;
            }
            else {
                return right();
            }
        }
    }

    @Override
    protected void acceptCFGAssign(CFGBuilder<?> v) {
        Field f = left();
        if (f.target() instanceof Expr) {
            Expr o = (Expr) f.target();

            //     o.f = e: visit o -> e -> (o.f = e)
            v.visitCFG(o, right(), ENTRY);
            v.visitCFG(right(), this, EXIT);
        }
        else {
            //       T.f = e: visit e -> (T.f OP= e)
            v.visitCFG(right(), this, EXIT);
        }

    }

    @Override
    protected void acceptCFGOpAssign(CFGBuilder<?> v) {
        /*
        Field f = (Field)left();
        if (f.target() instanceof Expr) {
            Expr o = (Expr) f.target();

            // o.f OP= e: visit o -> o.f -> e -> (o.f OP= e)
            v.visitCFG(o, f);
            v.visitThrow(f);
            v.edge(f, right().entry());
            v.visitCFG(right(), this);
        }
        else {
            // T.f OP= e: visit T.f -> e -> (T.f OP= e)
            v.visitThrow(f);
            v.edge(f, right().entry());
            v.visitCFG(right(), this);
        }
        */

        v.visitCFG(left(), right(), ENTRY);
        v.visitCFG(right(), this, EXIT);
    }

    @Override
    public List<Type> throwTypes(TypeSystem ts) {
        List<Type> l = new ArrayList<>(super.throwTypes(ts));

        Field f = left();
        if (f.target() instanceof Expr) {
            l.add(ts.NullPointerException());
        }

        return l;
    }

    @Override
    public NodeVisitor extRewriteEnter(ExtensionRewriter rw)
            throws SemanticException {
        return rw.bypass(left());
    }

    @Override
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        Expr left = visitChild(left(), rw);
        if (!left.isDisambiguated()) {
            // Need to have an ambiguous assignment
            return rw.nodeFactory().AmbAssign(position, left, op, right);
        }
        MaybeFieldAssign_c n = (MaybeFieldAssign_c) super.extRewrite(rw);
        n = left(n, left);
        return n;
    }
}
