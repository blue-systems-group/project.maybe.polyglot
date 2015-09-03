package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.ext.jl7.ast.*;

public class MaybeLang_c extends J7Lang_c implements MaybeLang {
    public static final MaybeLang_c instance = new MaybeLang_c();

    public static MaybeLang lang(NodeOps n) {
        while (n != null) {
            Lang lang = n.lang();
            if (lang instanceof MaybeLang) return (MaybeLang) lang;
            if (n instanceof Ext)
                n = ((Ext) n).pred();
            else return null;
        }
        throw new InternalCompilerError("Impossible to reach");
    }

    protected MaybeLang_c() {
    }

    protected static MaybeExt maybeExt(Node n) {
        return MaybeExt.ext(n);
    }

    @Override
    protected NodeOps NodeOps(Node n) {
        return maybeExt(n);
    }

    // TODO:  Implement dispatch methods for new AST operations.
    // TODO:  Override *Ops methods for AST nodes with new extension nodes.
}
