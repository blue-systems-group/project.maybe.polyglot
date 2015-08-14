package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;
import java.util.*;

public interface Maybe extends CompoundStmt {
    /** Get the maybe's condition. */
    Expr cond();

    /** Set the maybe's condition. */
    Maybe cond(Expr cond);

    /** Get the maybe's else clause, or null. */
    List<Block> alternatives();

    /** Set the maybe's else clause. */
    Maybe alternatives(List<Block> alternatives);
}
