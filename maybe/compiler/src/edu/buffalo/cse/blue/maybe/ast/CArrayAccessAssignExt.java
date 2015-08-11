package edu.buffalo.cse.blue.maybe.ast;

import polyglot.util.SerialVersionUID;
import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.visit.TypeChecker;

import java.util.ArrayList;
import java.util.List;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

import edu.buffalo.cse.blue.maybe.types.ConstArrayType;

public class CArrayAccessAssignExt extends MaybeExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // Suppose n is a[2] = 3;
        ArrayAccessAssign n = (ArrayAccessAssign) this.node();
        // Then left is a[2].
        ArrayAccess left = n.left();
        // And array is a.
        Expr array = left.array();

        // If the type of the array is a ConstArrayType, then this assignment
        // is illegal.
        if (array.type() instanceof ConstArrayType) {
            throw new SemanticException("Cannot assign a value to an element of a const array.",
            n.position());
        }

        // Let the base language deal with the default type checking.
        return superLang().typeCheck(n, tc);
    }

      @Override
      public List<Type> throwTypes(TypeSystem ts) {
          List<Type> l = new ArrayList<>();

          // The base language checks whether to add ArrayStoreException to the
          // list.  Since CArray eliminates the possibility of
          // ArrayStoreException, this method is overridden to ignore that check.
          l.add(ts.NullPointerException());
          l.add(ts.OutOfBoundsException());

          return l;
      }

}
