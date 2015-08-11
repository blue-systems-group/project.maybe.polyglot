package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for maybe extension.
 */
public interface MaybeNodeFactory extends NodeFactory {
    // TODO: Declare any factory methods for new AST nodes.
    ConstArrayTypeNode ConstArrayTypeNode(Position pos, TypeNode base);
}
