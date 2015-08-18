package edu.buffalo.cse.blue.maybe.parse;

import java.util.*;

import polyglot.parse.VarDeclarator;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.util.Position;

/**
 * Encapsulates some of the data in a variable declaration.  Used only by the parser.
 */
public class MaybeVarDeclarator extends VarDeclarator {
    // public Position pos;
    // public Id name;
    // public int dims;
    // public Expr init;
    public Expr label;
    public List<Expr> alternatives;

    public MaybeVarDeclarator(VarDeclarator varDeclarator) {
        super(varDeclarator.pos, varDeclarator.name);
        // this.pos = varDeclarator.pos;
        // this.name = varDeclarator.name;
        this.dims = varDeclarator.dims;
        this.init = null;
    }

    public Id name() {
        return name;
    }

    public int dims() {
        return dims;
    }

    public Expr init() {
        return init;
    }

    public Position position() {
        return pos;
    }

    public Expr label() {
        return label;
    }

    public List<Expr> alternatives() {
        return alternatives;
    }
}
