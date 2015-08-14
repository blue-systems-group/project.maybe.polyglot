package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;

/**
 * A {@code FieldAssign} represents a Java assignment expression to
 * a field.  For instance, {@code this.x = e}.
 *
 * The class of the {@code Expr} returned by
 * {@code FieldAssign.left()}is guaranteed to be a {@code Field}.
 */
public interface MaybeFieldAssign extends MaybeAssign {
    @Override
    Field left();
}
