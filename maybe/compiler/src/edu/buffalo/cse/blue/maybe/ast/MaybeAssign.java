package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.ast.Assign.Operator;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.SerialVersionUID;
import polyglot.util.Enum;

/**
 * An {@code Assign} represents a Java assignment expression.
 */
public interface MaybeAssign extends Expr {
    /** Assignment operator. */
    // public static class Operator extends Enum {
    //     private static final long serialVersionUID =
    //             SerialVersionUID.generate();
    //
    //     private final Binary.Operator binOp;
    //
    //     public Operator(String name, Binary.Operator binOp) {
    //         super(name);
    //         this.binOp = binOp;
    //     }
    //
    //     public Binary.Operator binaryOperator() {
    //         return binOp;
    //     }
    // }

    public static final Operator ASSIGN = new Assign.Operator("=", null);
    public static final Operator ADD_ASSIGN = new Assign.Operator("+=", Binary.ADD);
    public static final Operator SUB_ASSIGN = new Assign.Operator("-=", Binary.SUB);
    public static final Operator MUL_ASSIGN = new Assign.Operator("*=", Binary.MUL);
    public static final Operator DIV_ASSIGN = new Assign.Operator("/=", Binary.DIV);
    public static final Operator MOD_ASSIGN = new Assign.Operator("%=", Binary.MOD);
    public static final Operator BIT_AND_ASSIGN = new Assign.Operator("&=",
                                                               Binary.BIT_AND);
    public static final Operator BIT_OR_ASSIGN = new Assign.Operator("|=",
                                                              Binary.BIT_OR);
    public static final Operator BIT_XOR_ASSIGN = new Assign.Operator("^=",
                                                               Binary.BIT_XOR);
    public static final Operator SHL_ASSIGN = new Assign.Operator("<<=", Binary.SHL);
    public static final Operator SHR_ASSIGN = new Assign.Operator(">>=", Binary.SHR);
    public static final Operator USHR_ASSIGN =
            new Assign.Operator(">>>=", Binary.USHR);

    /**
     * Left child (target) of the assignment.
     * The target must be a Variable, but this is not enforced
     * statically to keep Polyglot backward compatible.
     */
    Expr left();

    /**
     * Set the left child (target) of the assignment.
     * The target must be a Variable, but this is not enforced
     * statically to keep Polyglot backward compatible.
     */
    MaybeAssign left(Expr left);

    /**
     * The assignment's operator.
     */
    Operator operator();

    /**
     * Set the assignment's operator.
     */
    MaybeAssign operator(Operator op);

    /**
     * Right child (source) of the assignment.
     */
    Expr right();

    /**
     * Set the right child (source) of the assignment.
     */
    MaybeAssign right(Expr right);

    /** Get the throwsArithmeticException of the expression. */
    boolean throwsArithmeticException();
}
