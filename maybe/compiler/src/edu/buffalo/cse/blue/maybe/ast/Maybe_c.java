package edu.buffalo.cse.blue.maybe.ast;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

public class Maybe_c extends Stmt_c implements Maybe {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Expr cond;
    protected Stmt consequent;
    protected Stmt alternative;

    public Maybe_c(Position pos, Expr cond, Stmt consequent, Stmt alternative) {
        super(pos, null);
        assert (cond != null && consequent != null); // alternative may be null;
        this.cond = cond;
        this.consequent = consequent;
        this.alternative = alternative;
    }

    @Override
    public Expr cond() {
        return this.cond;
    }

    @Override
    public Maybe cond(Expr cond) {
        return cond(this, cond);
    }

    protected <N extends Maybe_c> N cond(N n, Expr cond) {
        if (n.cond == cond) return n;
        n = copyIfNeeded(n);
        n.cond = cond;
        return n;
    }

    @Override
    public Stmt consequent() {
        return this.consequent;
    }

    @Override
    public Maybe consequent(Stmt consequent) {
        return consequent(this, consequent);
    }

    protected <N extends Maybe_c> N consequent(N n, Stmt consequent) {
        if (n.consequent == consequent) return n;
        n = copyIfNeeded(n);
        n.consequent = consequent;
        return n;
    }

    @Override
    public Stmt alternative() {
        return this.alternative;
    }

    @Override
    public Maybe alternative(Stmt alternative) {
        return alternative(this, alternative);
    }

    protected <N extends Maybe_c> N alternative(N n, Stmt alternative) {
        if (n.alternative == alternative) return n;
        n = copyIfNeeded(n);
        n.alternative = alternative;
        return n;
    }

    /** Reconstruct the statement. */
    protected <N extends Maybe_c> N reconstruct(N n, Expr cond, Stmt consequent,
            Stmt alternative) {
        n = cond(n, cond);
        n = consequent(n, consequent);
        n = alternative(n, alternative);
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr cond = visitChild(this.cond, v);
        Stmt consequent = visitChild(this.consequent, v);
        Stmt alternative = visitChild(this.alternative, v);
        return reconstruct(this, cond, consequent, alternative);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        if (!ts.isImplicitCastValid(cond.type(), ts.Boolean())) {
            throw new SemanticException("Condition of if statement must have BOOLEAN type.",
                                        cond.position());
        }
        // if (!ts.isImplicitCastValid(cond.type(), ts.Boolean())) {
        //     throw new SemanticException("Condition of if statement must have boolean type.",
        //                                 cond.position());
        // }

        return this;
    }

    @Override
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        TypeSystem ts = av.typeSystem();

        if (child == cond) {
            return ts.Boolean();
        }

        return child.type();
    }

    @Override
    public String toString() {
        return "if (" + cond + ") " + consequent
                + (alternative != null ? " else " + alternative : "");
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("if (");
        printBlock(cond, w, tr);
        w.write(")");

        printSubStmt(consequent, w, tr);

        if (alternative != null) {
            if (consequent instanceof Block) {
                // allow the "} else {" formatting except in emergencies
                w.allowBreak(0, 2, " ", 1);
            }
            else {
                w.allowBreak(0, " ");
            }

            if (alternative instanceof Block) {
                w.write("else ");
                print(alternative, w, tr);
            }
            else {
                w.write("else");
                printSubStmt(alternative, w, tr);
            }
        }
    }

    @Override
    public Term firstChild() {
        return cond;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        if (v.lang().isConstant(cond, v.lang()) && v.skipDeadIfBranches()) {
            // the condition is a constant expression.
            // That means that one branch is dead code
            boolean condConstantValue =
                    ((Boolean) v.lang().constantValue(cond, v.lang())).booleanValue();
            if (condConstantValue) {
                // the condition is constantly true.
                // the alternative won't be executed.
                v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, consequent, ENTRY);
                v.visitCFG(consequent, this, EXIT);
            }
            else {
                // the condition is constantly false.
                // the consequent won't be executed.
                if (alternative == null) {
                    // there is no alternative
                    v.visitCFG(cond, this, EXIT);
                }
                else {
                    v.visitCFG(cond,
                               FlowGraph.EDGE_KEY_FALSE,
                               alternative,
                               ENTRY);
                    v.visitCFG(alternative, this, EXIT);
                }
            }
        }
        else if (alternative == null) {
            // the alternative is null (but the condition is not constant, or we can't
            // skip dead statements.)
            v.visitCFG(cond,
                       FlowGraph.EDGE_KEY_TRUE,
                       consequent,
                       ENTRY,
                       FlowGraph.EDGE_KEY_FALSE,
                       this,
                       EXIT);
            v.visitCFG(consequent, this, EXIT);
        }
        else {
            // both consequent and alternative are present, and either the condition
            // is not constant or we can't skip dead statements.
            v.visitCFG(cond,
                       FlowGraph.EDGE_KEY_TRUE,
                       consequent,
                       ENTRY,
                       FlowGraph.EDGE_KEY_FALSE,
                       alternative,
                       ENTRY);
            v.visitCFG(consequent, this, EXIT);
            v.visitCFG(alternative, this, EXIT);
        }

        return succs;
    }

    @Override
    public Node copy(NodeFactory nf) {
        return nf.If(this.position,
                     this.cond,
                     this.consequent,
                     this.alternative);
    }
}
