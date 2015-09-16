package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;

import java.util.HashMap;

public class MaybeExt extends Ext_c {
    private static final long serialVersionUID = SerialVersionUID.generate();
    private static HashMap<String, Expr> labelMap = new HashMap<String, Expr>();

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

    public void checkDuplicateLabel(Expr label) throws SemanticException {
        String labelString = label.toString();
        if (labelMap.containsKey(labelString)) {
            Expr duplicateLabel = labelMap.get(labelString);
            throw new SemanticException("Maybe labels " + labelString
                    + " multiply defined. " + "Previous definition at "
                    + duplicateLabel.position() + ".", label.position());
        }
        labelMap.put(labelString, label);
    }
    // TODO:  Override operation methods for overridden AST operations.
}
