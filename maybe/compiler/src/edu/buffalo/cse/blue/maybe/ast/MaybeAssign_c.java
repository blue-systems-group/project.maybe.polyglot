package edu.buffalo.cse.blue.maybe.ast;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.*;
import polyglot.ast.Assign.Operator;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * An {@code Assign} represents a Java assignment expression.
 */
public abstract class MaybeAssign_c extends Expr_c implements MaybeAssign {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Expr left;
    protected Operator op;
    protected Expr maybeLabel;
    protected List<Expr> alternatives;

    // @Deprecated
    public MaybeAssign_c(Position pos, Expr left, Operator op, Expr maybeLabel, List<Expr> alternatives) {
        this(pos, left, op, maybeLabel, alternatives, null);
    }

    public MaybeAssign_c(Position pos, Expr left, Operator op, Expr maybeLabel, List<Expr> alternatives, Ext ext) {
        super(pos, ext);
        assert (left != null && op != null && alternatives != null);
        this.maybeLabel = maybeLabel;
        this.left = left;
        this.op = op;
        this.alternatives = alternatives;
    }

    @Override
    public Precedence precedence() {
        // System.out.println("precedence");
        return Precedence.ASSIGN;
    }

    @Override
    public Expr left() {
        return this.left;
    }

    @Override
    public MaybeAssign left(Expr left) {
        return left(this, left);
    }

    protected <N extends MaybeAssign_c> N left(N n, Expr left) {
        // System.out.println("left");
        if (n.left == left) {
            // System.out.println("don't copy left");
            return n;
        }
        n = copyIfNeeded(n);
        n.left = left;
        // System.out.println("CCOOPPYY left");
        return n;
    }

    @Override
    public Expr label() {
        return this.maybeLabel;
    }

    @Override
    public MaybeAssign label(Expr label) {
        return label(this, label);
    }

    protected <N extends MaybeAssign_c> N label(N n, Expr label) {
        // System.out.println("label");
        if (n.maybeLabel == maybeLabel) {
            // System.out.println("don't copy label");
            return n;
        }
        n = copyIfNeeded(n);
        n.maybeLabel = label;
        // System.out.println("CCOOPPYY label");
        return n;
    }

    @Override
    public Operator operator() {
        return this.op;
    }

    @Override
    public MaybeAssign operator(Operator op) {
        return operator(this, op);
    }

    protected <N extends MaybeAssign_c> N operator(N n, Operator op) {
        if (n.op == op) return n;
        n = copyIfNeeded(n);
        n.op = op;
        // System.out.println("CCOOPPYY op");
        return n;
    }

    @Override
    public Expr right() {
        return this.alternatives.get(0);
    }

    @Override
    public MaybeAssign right(Expr right) {
        return right(this, right);
    }

    protected <N extends MaybeAssign_c> N right(N n, Expr right) {
        // if (n.right == right) {
        //     return n;
        // }
        // n = copyIfNeeded(n);
        // n.right = right;
        return n;
    }

    @Override
    public List<Expr> alternatives() {
        return this.alternatives;
    }

    @Override
    public MaybeAssign alternatives(List<Expr> alternatives) {
        return alternatives(this, alternatives);
    }

    protected <N extends MaybeAssign_c> N alternatives(N n, List<Expr> alternatives) {
        // System.out.println("alternatives");
        if (equals(n.alternatives, alternatives)) {
            // System.out.println("don't copy alternatives");
            return n;
        }
        // System.out.println("CCOOPPYY alternatives");
        n = copyIfNeeded(n);
        n.alternatives = alternatives;
        return n;
    }

    protected boolean equals(List<Expr> l1, List<Expr> l2) {
        if (l1 == null || l2 == null) {
            return false;
        }
        if (l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            if (l1.get(i) != l2.get(i)) {
                return false;
            }
        }
        return true;
    }

    /** Reconstruct the expression. */
    protected <N extends MaybeAssign_c> N reconstruct(N n, Expr left, Expr maybeLabel, List<Expr> alternatives) {
        // System.out.println("reconstruct");
        n = left(n, left);
        n = label(n, maybeLabel);
        n = alternatives(n, alternatives);
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        // System.out.println("visitChildren");
        Expr left = visitChild(this.left, v);
        Expr label = this.maybeLabel;
        // TODO: need fix infinite loop below
        // Expr label = visitChild(this.maybeLabel, v);
        List<Expr> alternatives = visitList(this.alternatives, v);
        return reconstruct(this, left, label, alternatives);
    }

    public Node typeCheck(TypeChecker tc, TypeSystem ts, Type t, Expr e) throws SemanticException {
        Type s = e.type();

        if (op == Assign.ASSIGN) {
            if (!ts.isImplicitCastValid(s, t)
                    && !ts.typeEquals(s, t)
                    && !ts.numericConversionValid(t,
                                                  tc.lang()
                                                    .constantValue(e,
                                                                   tc.lang()))) {

                throw new SemanticException("Cannot assign " + s + " to " + t
                        + ".", position());
            }

            return type(t);
        }

        if (op == Assign.ADD_ASSIGN) {
            // t += s
            if (ts.typeEquals(t, ts.String())
                    && ts.canCoerceToString(s, tc.context())) {
                return type(ts.String());
            }

            if (t.isNumeric() && s.isNumeric()) {
                return type(ts.promote(t, s));
            }

            throw new SemanticException("The " + op + " operator must have "
                    + "numeric or String operands.", position());
        }

        if (op == Assign.SUB_ASSIGN || op == Assign.MUL_ASSIGN || op == Assign.DIV_ASSIGN
                || op == Assign.MOD_ASSIGN) {
            if (t.isNumeric() && s.isNumeric()) {
                return type(ts.promote(t, s));
            }

            throw new SemanticException("The " + op + " operator must have "
                    + "numeric operands.", position());
        }

        if (op == Assign.BIT_AND_ASSIGN || op == Assign.BIT_OR_ASSIGN || op == Assign.BIT_XOR_ASSIGN) {
            if (t.isBoolean() && s.isBoolean()) {
                return type(ts.Boolean());
            }

            if (ts.isImplicitCastValid(t, ts.Long())
                    && ts.isImplicitCastValid(s, ts.Long())) {
                return type(ts.promote(t, s));
            }

            throw new SemanticException("The " + op + " operator must have "
                    + "integral or boolean operands.", position());
        }

        if (op == Assign.SHL_ASSIGN || op == Assign.SHR_ASSIGN || op == Assign.USHR_ASSIGN) {
            if (ts.isImplicitCastValid(t, ts.Long())
                    && ts.isImplicitCastValid(s, ts.Long())) {
                // Only promote the left of a shift.
                return type(ts.promote(t));
            }

            throw new SemanticException("The " + op + " operator must have "
                    + "integral operands.", position());
        }

        throw new InternalCompilerError("Unrecognized assignment operator "
                + op + ".");
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // System.out.println("typeCheck");
        TypeSystem ts = tc.typeSystem();
        Type t = left.type();

        if (!(left instanceof Variable)) {
            throw new SemanticException("Target of assignment must be a variable.",
                                        position());
        }

        // System.out.println("!!: " +op);

        // TODO: the input must be a constant String, so we can generate metadata.
        // TODO: the maybeLabel maybe:
        // class polyglot.ast.AmbExpr_c
        // class polyglot.ast.Call_c
        // class polyglot.ast.Binary_c
        // class polyglot.ast.StringLit_c
        // How to handle it?

        // System.out.println(maybeLabel);
        // System.out.println(maybeLabel.toString());
        // System.out.println(maybeLabel.getClass());
        // System.out.println(maybeLabel.type());
        // System.out.println(tc);
        // System.out.println(ts);
        // if (!ts.isImplicitCastValid(maybeLabel.type(), ts.String())) {
        //     throw new SemanticException("Maybe label must be String type.",
        //                                 maybeLabel.position());
        // }

        Node n = null;
        for (Expr e : alternatives) {
            n = this.typeCheck(tc, ts, t, e);
        }
        return n;
    }

    @Override
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        // System.out.println("childExpectedType");
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
        if (op == Assign.ADD_ASSIGN || op == Assign.SUB_ASSIGN || op == Assign.MUL_ASSIGN
                || op == Assign.DIV_ASSIGN || op == Assign.MOD_ASSIGN || op == Assign.SHL_ASSIGN
                || op == Assign.SHR_ASSIGN || op == Assign.USHR_ASSIGN) {
            // if (left.type().isNumeric() && alternatives.type().isNumeric()) {
            if (left.type().isNumeric()) {
                try {
                    return ts.promote(left.type(), child.type());
                }
                catch (SemanticException e) {
                    throw new InternalCompilerError(e);
                }
            }
            // Assume the typechecker knew what it was doing
            return child.type();
        }
        if (op == Assign.BIT_AND_ASSIGN || op == Assign.BIT_OR_ASSIGN || op == Assign.BIT_XOR_ASSIGN) {
            if (left.type().isBoolean()) {
                return ts.Boolean();
            }
            if (left.type().isNumeric()) {
            // if (left.type().isNumeric() && alternatives.type().isNumeric()) {
                try {
                    return ts.promote(left.type(), child.type());
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

    @Override
    public boolean throwsArithmeticException() {
        // System.out.println("throwsArithmeticException");
        // conservatively assume that any division or mod may throw
        // ArithmeticException this is NOT true-- floats and doubles don't
        // throw any exceptions ever...
        return op == Assign.DIV_ASSIGN || op == Assign.MOD_ASSIGN;
    }

    @Override
    public String toString() {
        return left + " " + op + " maybe(" + maybeLabel + ") " + alternatives;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        // printSubExpr(left, true, w, tr);
        // w.write(" ");
        // w.write(op.toString());
        // w.allowBreak(2, 2, " ", 1); // miser mode
        // w.begin(0);
        // printSubExpr(alternatives, false, w, tr);
        // w.end();

        w.begin(0);
        w.write("switch (maybeService.get(");
        printBlock(maybeLabel, w, tr);
        // TODO: use maybe library to get choices
        w.write(")) {");
        w.unifiedBreak(4);
        w.begin(0);

        int i = 0;

        // w.write("case " + i++ + ": ");
        // printBlock(consequent, w, tr);
        // w.unifiedBreak(0);
        // w.write("break;");
        boolean first = true;
        if (alternatives != null) {
            for (Expr e : alternatives) {
                if (first) {
                    first = false;
                } else {
                    w.unifiedBreak(0);
                }
                w.write("case " + i++ + ": ");
                printSubExpr(left, true, w, tr);
                w.write(" ");
                w.write(op.toString());
                w.allowBreak(2, 2, " ", 1); // miser mode
                w.begin(0);
                printSubExpr(e, false, w, tr);
                w.write(";");
                w.end();
                w.unifiedBreak(0);
                w.write("break;");
            }
        }
        w.unifiedBreak(0);
        w.write("default: ");
        w.unifiedBreak(0);
        // TODO: handle default, choices out of range error
        printSubExpr(left, true, w, tr);
        w.write(" ");
        w.write(op.toString());
        w.allowBreak(2, 2, " ", 1); // miser mode
        w.begin(0);
        printSubExpr(alternatives.get(0), false, w, tr);
        w.write(";");
        w.end();
        w.unifiedBreak(0);
        w.write("break;");

        w.end();
        w.unifiedBreak(0);
        w.write("}");
    }

    @Override
    public void dump(CodeWriter w) {
        // System.out.println("dump");
        super.dump(w);
        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(operator " + op + ")");
        w.end();
    }

    @Override
    abstract public Term firstChild();

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        // System.out.println("acceptCFG");
        if (operator() == Assign.ASSIGN) {
            acceptCFGAssign(v);
        }
        else {
            acceptCFGOpAssign(v);
        }
        return succs;
    }

    /**
     * Construct a CFG for this assignment when the assignment operator
     * is ASSIGN (i.e., the normal, simple assignment =).
     */
    protected abstract void acceptCFGAssign(CFGBuilder<?> v);

    /**
     * Construct a CFG for this assignment when the assignment operator
     * is of the form op= for some operation op.
     */
    protected abstract void acceptCFGOpAssign(CFGBuilder<?> v);

    @Override
    public List<Type> throwTypes(TypeSystem ts) {
        List<Type> l = new LinkedList<>();

        if (throwsArithmeticException()) {
            l.add(ts.ArithmeticException());
        }

        return l;
    }

    @Override
    public Node copy(NodeFactory nf) {
        return ((MaybeNodeFactory) nf).MaybeAssign(this.position, this.left, this.op, this.maybeLabel, this.alternatives);
    }
}
