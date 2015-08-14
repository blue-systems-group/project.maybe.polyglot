package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;

/**
 * A {@code ArrayAccessAssign} represents a Java assignment expression
 * to an array element.  For instance, {@code A[3] = e}.
 *
 * The class of the {@code Expr} returned by
 * {@code ArrayAccessAssign.left()}is guaranteed to be an
 * {@code ArrayAccess}.
 */
public interface MaybeArrayAccessAssign extends MaybeAssign {
    @Override
    ArrayAccess left();
}
