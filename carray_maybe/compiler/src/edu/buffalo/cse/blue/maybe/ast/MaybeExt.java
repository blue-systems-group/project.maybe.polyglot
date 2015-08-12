package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;

public class MaybeExt extends Ext_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static MaybeExt ext(Node n) {
        Ext e = n.ext();
        while (e != null && !(e instanceof MaybeExt)) {
            e = e.ext();
        }
        if (e == null) {
            throw new InternalCompilerError("No Maybe extension object for node "
                    + n + " (" + n.getClass() + ")", n.position());
        }
        return (MaybeExt) e;
    }

    @Override
    public final MaybeLang lang() {
        return MaybeLang_c.instance;
    }

    // TODO:  Override operation methods for overridden AST operations.
}
