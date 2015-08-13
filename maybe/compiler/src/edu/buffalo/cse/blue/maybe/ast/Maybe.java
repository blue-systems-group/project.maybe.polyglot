package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

public interface Maybe extends CompoundStmt {
    /** Get the if's condition. */
    Expr cond();

    /** Set the if's condition. */
    Maybe cond(Expr cond);

    /** Get the if's then clause. */
    Stmt consequent();

    /** Set the if's then clause. */
    Maybe consequent(Block consequent);

    /** Get the if's else clause, or null. */
    List<Block> alternatives();

    /** Set the if's else clause. */
    Maybe alternatives(List<Block> alternatives);
}
