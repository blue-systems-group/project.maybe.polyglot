package edu.buffalo.cse.blue.maybe.ast;

import polyglot.ast.ArrayTypeNode_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeBuilder;
import edu.buffalo.cse.blue.maybe.types.CArrayTypeSystem;

public class ConstArrayTypeNode_c extends ArrayTypeNode_c implements ConstArrayTypeNode {
    public ConstArrayTypeNode_c(Position pos, TypeNode base) {
        super(pos, base);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        CArrayTypeSystem ts = (CArrayTypeSystem) tb.typeSystem();
        return type(ts.constArrayOf(position(), base().type()));
    }

    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        Type baseType = base.type();
        if (!baseType.isCanonical()) {
            return this;
        }
        CArrayTypeSystem ts = (CArrayTypeSystem) ar.typeSystem();
        NodeFactory nf = ar.nodeFactory();
        return nf.CanonicalTypeNode(position(), ts.constArrayOf(position(), baseType));
    }

    @Override
    public String toString() {
        String result = base.toString();
        if (base instanceof ConstArrayTypeNode) {
            result += "[]";
        } else {
            result += " const[]";
        }
        return result;
    }
}
