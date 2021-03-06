package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.Assign.Operator;
import polyglot.ast.*;
import polyglot.lex.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import polyglot.ext.jl7.parse.*;
import polyglot.ext.jl7.ast.*;
import polyglot.ext.jl7.types.*;
import java.util.*;

/**
 * NodeFactory for maybe extension.
 */
public interface MaybeNodeFactory extends JL7NodeFactory {
    // TODO: Declare any factory methods for new AST nodes.
    Maybe Maybe(Position pos, Expr cond, List<Block> alternatives);
    MaybeLocalDecl MaybeLocalDecl(Position pos, Flags flags, TypeNode type, Id name, Expr label, List<Expr> alternatives);

    MaybeAssign MaybeAssign(Position pos, Expr left, Assign.Operator op, Expr maybeLabel, List<Expr> right);

    MaybeAssign MaybeLocalAssign(Position pos, Local left, Assign.Operator op, Expr maybeLabel, List<Expr> right);
    MaybeAssign MaybeFieldAssign(Position pos, Field left, Assign.Operator op, Expr maybeLabel, List<Expr> right);
    MaybeAssign MaybeArrayAccessAssign(Position pos, ArrayAccess left, Assign.Operator op, Expr maybeLabel, List<Expr> right);
    MaybeAssign MaybeAmbAssign(Position pos, Expr left, Assign.Operator op, Expr maybeLabel, List<Expr> right);

    // LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type, Id name, Expr init);
}
