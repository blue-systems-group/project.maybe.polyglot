package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.ast.Assign.Operator;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.TypeChecker;

public class MaybeAssignExt extends MaybeExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        Assign a = (Assign) this.node();
        Expr left = a.left();
        Expr right = a.right();
        Operator op = a.operator();
        if (child == left) {
            return child.type();
        }

        // See JLS 2nd ed. 15.26.2
        TypeSystem ts = av.typeSystem();
        if (op == Assign.ASSIGN) {
            return left.type();
        }
        if (op == Assign.ADD_ASSIGN) {
            if (ts.typeEquals(ts.String(), left.type())) {
                return child.type();
            }
        }
        if (op == Assign.ADD_ASSIGN || op == Assign.SUB_ASSIGN
                || op == Assign.MUL_ASSIGN || op == Assign.DIV_ASSIGN
                || op == Assign.MOD_ASSIGN || op == Assign.SHL_ASSIGN
                || op == Assign.SHR_ASSIGN || op == Assign.USHR_ASSIGN) {
            if (isNumeric(left.type()) && isNumeric(right.type())) {
                try {
                    return ts.promote(numericType(left.type()),
                                      numericType(child.type()));
                }
                catch (SemanticException e) {
                    throw new InternalCompilerError(e);
                }
            }
            // Assume the typechecker knew what it was doing
            return child.type();
        }
        if (op == Assign.BIT_AND_ASSIGN || op == Assign.BIT_OR_ASSIGN
                || op == Assign.BIT_XOR_ASSIGN) {
            if (left.type().isBoolean()) {
                return ts.Boolean();
            }
            if (isNumeric(left.type()) && isNumeric(right.type())) {
                try {
                    return ts.promote(numericType(left.type()),
                                      numericType(child.type()));
                }
                catch (SemanticException e) {
                    throw new InternalCompilerError(e);
                }
            }
            // Assume the typechecker knew what it was doing
            return child.type();
        }

        throw new InternalCompilerError("Unrecognized assignment operator "
                + op + ".");
    }

    private Node typeCheck(TypeChecker tc, Assign a, Type t, Type s, Expr left, Expr right) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        if (!(left instanceof Variable)) {
            throw new SemanticException("Target of assignment must be a variable.",
                    a.position());
        }

        if (a.operator() == Assign.ASSIGN) {
            if (!ts.isImplicitCastValid(s, t)
                    && !ts.typeEquals(s, t)
                    && !ts.numericConversionValid(t,
                    tc.lang()
                            .constantValue(right,
                                    tc.lang()))) {

                throw new SemanticException("Cannot assign " + s + " to " + t
                        + ".", a.position());
            }

            return a.type(t);
        }

        if (a.operator() == Assign.ADD_ASSIGN) {
            // t += s
            if (ts.typeEquals(t, ts.String())
                    && ts.canCoerceToString(s, tc.context())) {
                return a.type(ts.String());
            }

            if (isNumeric(t) && isNumeric(s)) {
                return a.type(ts.promote(numericType(t), numericType(s)));
            }

            throw new SemanticException("The " + a.operator()
                    + " operator must have "
                    + "numeric or String operands.",
                    a.position());
        }

        if (a.operator() == Assign.SUB_ASSIGN
                || a.operator() == Assign.MUL_ASSIGN
                || a.operator() == Assign.DIV_ASSIGN
                || a.operator() == Assign.MOD_ASSIGN) {
            if (isNumeric(t) && isNumeric(s)) {
                return a.type(ts.promote(numericType(t), numericType(s)));
            }

            throw new SemanticException("The " + a.operator()
                    + " operator must have "
                    + "numeric operands.",
                    a.position());
        }

        if (a.operator() == Assign.BIT_AND_ASSIGN
                || a.operator() == Assign.BIT_OR_ASSIGN
                || a.operator() == Assign.BIT_XOR_ASSIGN) {
            if (isBoolean(t) && isBoolean(s)) {
                return a.type(ts.Boolean());
            }

            if (ts.isImplicitCastValid(t, ts.Long())
                    && ts.isImplicitCastValid(s, ts.Long())) {
                return a.type(ts.promote(numericType(t), numericType(s)));
            }

            throw new SemanticException("The "
                    + a.operator()
                    + " operator must have "
                    + "integral or boolean operands.",
                    a.position());
        }

        if (a.operator() == Assign.SHL_ASSIGN
                || a.operator() == Assign.SHR_ASSIGN
                || a.operator() == Assign.USHR_ASSIGN) {
            if (ts.isImplicitCastValid(t, ts.Long())
                    && ts.isImplicitCastValid(s, ts.Long())) {
                // Only promote the left of a shift.
                return a.type(ts.promote(numericType(t)));
            }

            throw new SemanticException("The " + a.operator()
                    + " operator must have "
                    + "integral operands.",
                    a.position());
        }

        throw new InternalCompilerError("Unrecognized assignment operator "
                + a.operator() + ".");
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Assign a = (Assign) this.node();
        Expr left = a.left();
        Type t = left.type();
        // DONE: DO NOT use this, use real typeCheck!
        //       by remove right element in MaybeAssign
        if (a instanceof MaybeAssign) {
            MaybeAssign maybeAssign = (MaybeAssign) this.node();
            Expr label = maybeAssign.label();
            if (!(label instanceof StringLit)) {
                throw new SemanticException("Maybe label must be String Literal.",
                        label.position());
            }
            Node result = null;
            for (Expr right : maybeAssign.alternatives()) {
                result = this.typeCheck(tc, maybeAssign, t, right.type(), left, right);
            }
            return result;
        } else {
            Expr right = a.right();
            return this.typeCheck(tc, a, t, right.type(), left, right);
        }
    }

    public boolean isNumeric(Type t) {
        if (t.isNumeric()) return true;
        JL5TypeSystem ts = (JL5TypeSystem) t.typeSystem();

        if (ts.isPrimitiveWrapper(t)) {
            return ts.primitiveTypeOfWrapper(t).isNumeric();
        }
        return false;
    }

    public boolean isBoolean(Type t) {
        if (t.isBoolean()) return true;
        JL5TypeSystem ts = (JL5TypeSystem) t.typeSystem();

        if (ts.isPrimitiveWrapper(t)) {
            return ts.primitiveTypeOfWrapper(t).isBoolean();
        }
        return false;
    }

    public Type numericType(Type t) {
        if (t.isNumeric()) return t;
        JL5TypeSystem ts = (JL5TypeSystem) t.typeSystem();

        if (ts.isPrimitiveWrapper(t)) {
            return ts.primitiveTypeOfWrapper(t);
        }
        return t;
    }

}
