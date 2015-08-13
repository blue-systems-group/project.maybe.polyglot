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
    protected Block consequent;
    protected List<Block> alternatives;

    public Maybe_c(Position pos, Expr cond, Block consequent, List<Block> alternatives) {
        super(pos, null);
        assert (cond != null && consequent != null); // alternatives may be null;
        this.cond = cond;
        this.consequent = consequent;
        this.alternatives = alternatives;
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
    public Block consequent() {
        return this.consequent;
    }

    @Override
    public Maybe consequent(Block consequent) {
        return consequent(this, consequent);
    }

    protected <N extends Maybe_c> N consequent(N n, Block consequent) {
        if (n.consequent == consequent) return n;
        n = copyIfNeeded(n);
        n.consequent = consequent;
        return n;
    }

    @Override
    public List<Block> alternatives() {
        return this.alternatives;
    }

    @Override
    public Maybe alternatives(List<Block> alternatives) {
        return alternatives(this, alternatives);
    }

    protected <N extends Maybe_c> N alternatives(N n, List<Block> alternatives) {
        if (n.alternatives == alternatives) return n;
        n = copyIfNeeded(n);
        n.alternatives = alternatives;
        return n;
    }

    /** Reconstruct the statement. */
    protected <N extends Maybe_c> N reconstruct(N n, Expr cond, Block consequent,
            List<Block> alternatives) {
        n = cond(n, cond);
        n = consequent(n, consequent);
        n = alternatives(n, alternatives);
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr cond = visitChild(this.cond, v);
        Block consequent = visitChild(this.consequent, v);
        List<Block> list = new LinkedList<Block>();
        if (alternatives != null) {
            for (Block b : alternatives) {
                 list.add(visitChild(b, v));
            }
        }
        return reconstruct(this, cond, consequent, list);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        if (!ts.isImplicitCastValid(cond.type(), ts.String())) {
            throw new SemanticException("Maybe label must be String type.",
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
        return "maybe (" + cond + ") " + consequent
                + (alternatives != null ? " or " + alternatives : "");
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("if (");
        printBlock(cond, w, tr);
        w.write(" == 0)");

        printSubStmt(consequent, w, tr);

        if (alternatives != null) {
            w.allowBreak(0, 2, " ", 1);
            for (Block b : alternatives) {
                // w.newline(0);
                // printBlock(b, w, tr);
                w.write("else ");
                print(b, w, tr);
            }
        }
    }

    @Override
    public Term firstChild() {
        return cond;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        // No sense, just suppress Unreachable statement check
        for (Block b : alternatives) {
            v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, consequent, ENTRY, FlowGraph.EDGE_KEY_FALSE, b, ENTRY);
            v.visitCFG(b, this, EXIT);
        }
        v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, consequent, ENTRY, FlowGraph.EDGE_KEY_FALSE, consequent, ENTRY);
        v.visitCFG(consequent, this, EXIT);
        return succs;
    }

    // @Override
    // public Node copy(NodeFactory nf) {
    //     return ((MaybeNodeFactory) nf).Maybe(this.position,
    //                  this.cond,
    //                  this.consequent,
    //                  this.alternative);
    // }
}
