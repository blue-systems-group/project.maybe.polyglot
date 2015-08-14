package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;

/**
 * A {@code LocalAssign} represents a Java assignment expression
 * to an array element.  For instance, {@code x = e}.
 *
 * The class of the {@code Expr} returned by
 * {@code LocalAssign.left()}is guaranteed to be an {@code Local}.
 */
public interface MaybeLocalAssign extends MaybeAssign {
    @Override
    Local left();
}
