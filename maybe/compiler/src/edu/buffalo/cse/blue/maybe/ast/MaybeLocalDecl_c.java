package edu.buffalo.cse.blue.maybe.ast;

import java.util.List;

import polyglot.ast.*;
import polyglot.frontend.MissingDependencyException;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.CollectionUtil;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ConstantChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/**
 * A {@code MaybeLocalDecl} is an immutable representation of a local variable
 * declaration statement: a type, a name and an optional initializer.
 */
public class MaybeLocalDecl_c extends Stmt_c implements MaybeLocalDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Flags flags;
    protected TypeNode type;
    protected Id name;
    protected Expr init;
    protected Expr label;
    protected List<Expr> alternatives;
    protected LocalInstance li;

    // @Deprecated
    public MaybeLocalDecl_c(Position pos, Flags flags, TypeNode type, Id name,
            Expr label, List<Expr> alternatives) {
        this(pos, flags, type, name, label, alternatives, null);
    }

    public MaybeLocalDecl_c(Position pos, Flags flags, TypeNode type, Id name,
            Expr label, List<Expr> alternatives, Ext ext) {
        super(pos, ext);
        assert (flags != null && type != null && name != null); // init may be null
        this.flags = flags;
        this.type = type;
        this.name = name;
        this.label = label;
        this.alternatives = alternatives;
    }

    @Override
    public boolean isDisambiguated() {
        return li != null && li.isCanonical() && super.isDisambiguated();
    }

    @Override
    public Type declType() {
        return type.type();
    }

    @Override
    public Flags flags() {
        return flags;
    }

    @Override
    public MaybeLocalDecl flags(Flags flags) {
        return flags(this, flags);
    }

    protected <N extends MaybeLocalDecl_c> N flags(N n, Flags flags) {
        if (n.flags.equals(flags)) return n;
        n = copyIfNeeded(n);
        n.flags = flags;
        return n;
    }

    @Override
    public TypeNode type() {
        return type;
    }

    @Override
    public MaybeLocalDecl type(TypeNode type) {
        return type(this, type);
    }

    protected <N extends MaybeLocalDecl_c> N type(N n, TypeNode type) {
        if (n.type == type) return n;
        n = copyIfNeeded(n);
        n.type = type;
        return n;
    }

    @Override
    public Id id() {
        return name;
    }

    @Override
    public MaybeLocalDecl id(Id name) {
        return id(this, name);
    }

    protected <N extends MaybeLocalDecl_c> N id(N n, Id name) {
        if (n.name == name) return n;
        n = copyIfNeeded(n);
        n.name = name;
        return n;
    }

    @Override
    public String name() {
        return name.id();
    }

    @Override
    public MaybeLocalDecl name(String name) {
        return id(this.name.id(name));
    }

    @Override
    public Expr label() {
        return label;
    }

    @Override
    public MaybeLocalDecl label(Expr label) {
        return label(this, label);
    }

    protected <N extends MaybeLocalDecl_c> N label(N n, Expr label) {
        if (n.label == label) return n;
        n = copyIfNeeded(n);
        n.label = label;
        return n;
    }

    @Override
    public Expr init() {
        return this.alternatives.get(0);
    }

    @Override
    public MaybeLocalDecl init(Expr init) {
        return init(this, init);
    }

    protected <N extends MaybeLocalDecl_c> N init(N n, Expr init) {
        return n;
    }

    @Override
    public List<Expr> alternatives() {
        return alternatives;
    }

    @Override
    public MaybeLocalDecl alternatives(List<Expr> alternatives) {
        return alternatives(this, alternatives);
    }

    protected <N extends MaybeLocalDecl_c> N alternatives(N n, List<Expr> alternatives) {
        if (CollectionUtil.equals(n.alternatives, alternatives)) return n;
        n = copyIfNeeded(n);
        n.alternatives = alternatives;
        return n;
    }

    @Override
    public VarInstance varInstance() {
        return localInstance();
    }

    @Override
    public LocalInstance localInstance() {
        return li;
    }

    @Override
    public MaybeLocalDecl localInstance(LocalInstance li) {
        return localInstance(this, li);
    }

    protected <N extends MaybeLocalDecl_c> N localInstance(N n, LocalInstance li) {
        if (n.li == li) return n;
        n = copyIfNeeded(n);
        n.li = li;
        return n;
    }

    /** Reconstruct the declaration. */
    protected <N extends MaybeLocalDecl_c> N reconstruct(N n, TypeNode type,
            Id name, Expr label, List<Expr> alternatives) {
        n = type(n, type);
        n = id(n, name);
        n = label(n, label);
        n = alternatives(n, alternatives);
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        TypeNode type = visitChild(this.type, v);
        Id name = visitChild(this.name, v);
        Expr label = visitChild(this.label, v);
        List<Expr> alternatives = visitList(this.alternatives, v);
        return reconstruct(this, type, name, label, alternatives);
    }

    /**
     * Add the declaration of the variable as we enter the scope of the
     * initializer
     */
    @Override
    public Context enterChildScope(Node child, Context c) {
        if (child == init) {
            c.addVariable(li);
        }
        return super.enterChildScope(child, c);
    }

    @Override
    public void addDecls(Context c) {
        // Add the declaration of the variable in case we haven't already done
        // so in enterScope, when visiting the initializer.
        c.addVariable(li);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        MaybeLocalDecl_c n = (MaybeLocalDecl_c) super.buildTypes(tb);

        TypeSystem ts = tb.typeSystem();

        LocalInstance li =
                ts.localInstance(position(),
                                 flags(),
                                 ts.unknownType(position()),
                                 name());
        n = localInstance(n, li);
        return n;
    }

    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (li.isCanonical()) {
            return this;
        }
        if (declType().isCanonical()) {
            li.setType(declType());
        }
        return this;
    }

    /**
     * Override superclass behavior to check if the variable is multiply
     * defined.
     */
    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        // Check if the variable is multiply defined.
        // we do it in type check enter, instead of type check since
        // we add the declaration before we enter the scope of the
        // initializer.
        Context c = tc.context();

        LocalInstance outerLocal = c.findLocalSilent(li.name());

        if (outerLocal != null && c.isLocal(li.name())) {
            throw new SemanticException("Local variable \"" + name
                    + "\" multiply defined.  " + "Previous definition at "
                    + outerLocal.position() + ".", position());
        }

        return super.typeCheckEnter(tc);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        LocalInstance li = this.li;

        try {
            ts.checkLocalFlags(flags);
        }
        catch (SemanticException e) {
            throw new SemanticException(e.getMessage(), position());
        }

        // TODO: rewrite below
        // TODO: implement typeCheck
        // if (init != null) {
        //     if (init instanceof ArrayInit) {
        //         ((ArrayInit) init).typeCheckElements(tc, type.type());
        //     }
        //     else {
        //         if (!ts.isImplicitCastValid(init.type(), type.type())
        //                 && !ts.typeEquals(init.type(), type.type())
        //                 && !ts.numericConversionValid(type.type(),
        //                                               tc.lang()
        //                                                 .constantValue(init,
        //                                                                tc.lang()))) {
        //             throw new SemanticException("The type of the variable "
        //                                                 + "initializer \""
        //                                                 + init.type()
        //                                                 + "\" does not match that of "
        //                                                 + "the declaration \""
        //                                                 + type.type() + "\".",
        //                                         init.position());
        //         }
        //     }
        // }

        return localInstance(li);
    }

    protected static class AddDependenciesVisitor extends NodeVisitor {
        protected ConstantChecker cc;
        protected LocalInstance li;

        AddDependenciesVisitor(JLang lang, ConstantChecker cc, LocalInstance li) {
            super(lang);
            this.cc = cc;
            this.li = li;
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Field) {
                Field f = (Field) n;
                if (!f.fieldInstance().orig().constantValueSet()) {
                    Scheduler scheduler = cc.job().extensionInfo().scheduler();
                    Goal g =
                            scheduler.FieldConstantsChecked(f.fieldInstance()
                                                             .orig());
                    throw new MissingDependencyException(g);
                }
            }
            if (n instanceof Local) {
                Local l = (Local) n;
                if (!l.localInstance().orig().constantValueSet()) {
                    // Undefined variable or forward reference.
                    li.setNotConstant();
                }
            }
            return n;
        }
    }

    @Override
    public Node checkConstants(ConstantChecker cc) throws SemanticException {
//        if (init != null && ! init.constantValueSet()) {
//            // HACK to add dependencies for computing the constant value.
//            init.visit(new AddDependenciesVisitor(cc, li));
//            return this;
//        }


        // TODO: rewrite below
        // if (init == null || !cc.lang().isConstant(init, cc.lang())
        //         || !li.flags().isFinal()) {
        //     li.setNotConstant();
        // }
        // else {
        //     li.setConstantValue(cc.lang().constantValue(init, cc.lang()));
        // }

        return this;
    }

    @Override
    public boolean constantValueSet() {
        return li != null && li.constantValueSet();
    }

    @Override
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        // TODO: rewrite below
        // if (child == init) {
        //     // the expected type of the initializer is the type
        //     // of the local.
        //     return type.type();
        // }

        return child.type();
    }

    @Override
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        MaybeLocalDecl_c n = (MaybeLocalDecl_c) super.extRewrite(rw);
        n = localInstance(n, null);
        return n;
    }

    @Override
    public String toString() {
        return flags.translate() + type + " " + name
                + " = maybe(" + (label != null ? label : "") + ") " + alternatives + ";";
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        boolean printSemi = tr.appendSemicolon(true);
        boolean printType = tr.printType(true);

        w.write(flags.translate());
        if (printType) {
            print(type, w, tr);
            w.write(" ");
        }
        tr.print(this, name, w);
        // TODO: allowBreak doesn't work well here
        w.write(" = ");
        print(alternatives.get(0), w, tr);
        w.write(";");

        w.unifiedBreak(0);
        w.begin(0);
        w.write("switch ((");
        printBlock(label, w, tr);
        // TODO: use maybe library to get choices
        w.write(").length() % 2) {");
        w.unifiedBreak(4);
        w.begin(0);

        int i = 0;

        boolean first = true;
        if (alternatives != null) {
            for (Expr e : alternatives) {
                if (first) {
                    first = false;
                } else {
                    w.unifiedBreak(0);
                }
                w.write("case " + i++ + ": ");
                w.unifiedBreak(4);
                tr.print(this, name, w);
                // // printSubExpr(left, true, w, tr);
                w.write(" =");
                w.allowBreak(2, " ");
                // // w.allowBreak(2, 2, " ", 1); // miser mode
                print(e, w, tr);
                // // printSubExpr(e, false, w, tr);
                w.write(";");
                w.unifiedBreak(4);
                w.write("break;");
            }
        }
        w.unifiedBreak(0);
        w.write("default: ");
        w.unifiedBreak(4);
        // TODO: handle default, choices out of range error
        w.write("break;");

        w.unifiedBreak(0);
        w.write("}");
        w.end();
        //
        // w.end();
        // w.unifiedBreak(0);
        // w.write("}");

        // if (alternatives != null) {
        //     w.write(" =");
        //     w.allowBreak(2, " ");
        //     print(alternatives.get(1), w, tr);
        // }
        // if (init != null) {
        //     w.write(" =");
        //     w.allowBreak(2, " ");
        //     print(init, w, tr);
        // }

// --------------
        // if (printSemi) {
        //     w.write(";");
        // }
        //
        // tr.printType(printType);
        // tr.appendSemicolon(printSemi);
    }

    @Override
    public void dump(CodeWriter w) {
        super.dump(w);

        if (li != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + li + ")");
            w.end();
        }
    }

    @Override
    public Term firstChild() {
        return type();
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        // List<Term> cases = new LinkedList<>();
        // List<Integer> entry = new LinkedList<>();
        // boolean hasDefault = false;
        //
        // for (SwitchElement s : elements) {
        //     if (s instanceof Case) {
        //         cases.add(s);
        //         entry.add(new Integer(ENTRY));
        //
        //         if (((Case) s).expr() == null) {
        //             hasDefault = true;
        //         }
        //     }
        // }
        //
        // // If there is no default case, add an edge to the end of the switch.
        // if (!hasDefault) {
        //     cases.add(this);
        //     entry.add(new Integer(EXIT));
        // }
        //
        // v.visitCFG(expr, FlowGraph.EDGE_KEY_OTHER, cases, entry);
        // v.push(this).visitCFGList(elements, this, EXIT);
        //
        // return succs;
        // TODO: rewrite below
        v.visitCFG(type(), this, ENTRY);
        v.visitCFG(type(), this, EXIT);
        v.visitCFG(label(), this, ENTRY);
        v.visitCFG(label(), this, EXIT);
        v.visitCFGList(alternatives(), this, ENTRY);
        v.visitCFGList(alternatives(), this, EXIT);
        // if (init() != null) {
        //     v.visitCFG(type(), init(), ENTRY);
        //     v.visitCFG(init(), this, EXIT);
        // }
        // else {
        //     v.visitCFG(type(), this, EXIT);
        // }

        return succs;
    }

    @Override
    public Node copy(NodeFactory nf) {
        return ((MaybeNodeFactory) nf).MaybeLocalDecl(this.position,
                            this.flags,
                            this.type,
                            this.name,
                            this.label,
                            this.alternatives);
    }

}
