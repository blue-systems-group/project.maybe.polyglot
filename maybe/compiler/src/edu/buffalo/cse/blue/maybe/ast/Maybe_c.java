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
    protected List<Block> alternatives;

    public Maybe_c(Position pos, Expr cond, List<Block> alternatives) {
        super(pos, null);
        assert (cond != null && alternatives != null);
        this.cond = cond;
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
    protected <N extends Maybe_c> N reconstruct(N n, Expr cond, List<Block> alternatives) {
        n = cond(n, cond);
        n = alternatives(n, alternatives);
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr cond = visitChild(this.cond, v);
        List<Block> list = new LinkedList<Block>();
        if (alternatives != null) {
            for (Block b : alternatives) {
                list.add(visitChild(b, v));
            }
        }
        return reconstruct(this, cond, list);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        // DONE: the input must be a constant String, so we can generate metadata.
        if (!ts.isImplicitCastValid(cond.type(), ts.String())) {
            throw new SemanticException("Maybe label must be String type.",
                                        cond.position());
        }
        if (!(cond instanceof StringLit_c)) {
            throw new SemanticException("Maybe label must be String Literal.",
                    cond.position());
        }
        MaybeExt ext = MaybeExt.ext(this);
        ext.checkDuplicateLabel(cond);
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
        return "maybe (" + cond + ") " + " or " + alternatives;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("switch (maybeService.get(");
        printBlock(cond, w, tr);
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
            for (Block b : alternatives) {
                if (first) {
                    first = false;
                } else {
                    w.unifiedBreak(0);
                }
                w.write("case " + i++ + ": ");
                printBlock(b, w, tr);
                w.unifiedBreak(0);
                w.write("break;");
            }
        }
        w.unifiedBreak(0);
        w.write("default: ");
        w.unifiedBreak(0);
        // TODO: handle default, choices out of range error
        w.write("break;");

        w.end();
        w.unifiedBreak(0);
        w.write("}");
    }

    @Override
    public Term firstChild() {
        return cond;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        // No sense, just suppress Unreachable statement check
        for (Block b : alternatives) {
            v.visitCFG(cond, FlowGraph.EDGE_KEY_TRUE, b, ENTRY, FlowGraph.EDGE_KEY_FALSE, b, ENTRY);
            v.visitCFG(b, this, EXIT);
        }
        return succs;
    }

    @Override
    public Node copy(NodeFactory nf) {
        return ((MaybeNodeFactory) nf).Maybe(this.position,
                     this.cond,
                     this.alternatives);
    }
}
