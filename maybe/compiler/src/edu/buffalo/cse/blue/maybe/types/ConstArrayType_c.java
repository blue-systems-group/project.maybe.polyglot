package edu.buffalo.cse.blue.maybe.types;

import polyglot.types.ArrayType_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class ConstArrayType_c extends ArrayType_c implements ConstArrayType {
    private static final long SerialVersionUID = 123456789L;

    public ConstArrayType_c(TypeSystem ts, Position pos, Type base) {
        super(ts, pos, base);
    }

    @Override
    public String toString() {
        String result = base.toString();
        if (base instanceof ConstArrayType) {
            result += "[]";
        } else {
            result += " const[]";
        }
        return result;
    }
}
