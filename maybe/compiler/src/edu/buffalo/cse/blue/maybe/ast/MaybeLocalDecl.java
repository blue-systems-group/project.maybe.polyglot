package edu.buffalo.cse.blue.maybe.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;

/**
 * A {@code LocalDecl} is an immutable representation of a local variable
 * declaration statement: a type, a name and an optional initializer.
 */
public interface MaybeLocalDecl extends LocalDecl {
    /** Get the declaration's initializer expression, or null. */
    Expr init();

    /** Set the declaration's initializer expression. */
    MaybeLocalDecl init(Expr init);

    /**
     * Set the type object for the local declaration.
     */
    MaybeLocalDecl localInstance(LocalInstance li);

    /** Set the declaration's flags. */
    MaybeLocalDecl flags(Flags flags);

    /** Set the declaration's type. */
    MaybeLocalDecl type(TypeNode type);

    /** Set the declaration's name. */
    MaybeLocalDecl id(Id name);

    /** Set the declaration's name. */
    MaybeLocalDecl name(String name);

    /** Get the declaration's maybe label expression, or null. */
    Expr label();

    /** Set the declaration's maybe label expression. */
    MaybeLocalDecl label(Expr label);

    /** Get the declaration's maybe alternatives, or null. */
    List<Expr> alternatives();

    /** Set the declaration's maybe alternatives. */
    MaybeLocalDecl alternatives(List<Expr> alternatives);
}
