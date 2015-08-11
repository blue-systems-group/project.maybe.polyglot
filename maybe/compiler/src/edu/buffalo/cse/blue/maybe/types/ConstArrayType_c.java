package edu.buffalo.cse.blue.maybe.types;

import polyglot.types.ArrayType_c;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class ConstArrayType_c extends ArrayType_c implements ConstArrayType {
    private static final long serialVersionUID = SerialVersionUID.generate();

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

    @Override
    public boolean isImplicitCastValidImpl(Type toType) {
        if (!toType.isArray()) {
            // ?1 = ?2 const[]
            // This const array type is assignable to ?1 only if ?1 is Object.
            // Let the base language check this fact.
            return super.isImplicitCastValidImpl(toType);
        }

        // From this point, toType is an array.
        if (toType instanceof ConstArrayType) {
            // ?1 const[] = ?2 const[]
            // Let the base language check whether ?2 is assignable to ?1.
            return super.isImplicitCastValidImpl(toType);
        }

        // From this point, toType is a non-const array.
        // ?1[] = ?2 const[]
        // We cannot assign a const array to a non-const array.
        return false;
    }

    @Override
    public boolean equalsImpl(TypeObject t) {
        if (t instanceof ConstArrayType) {
            ConstArrayType ca = (ConstArrayType) t;
            return ts.equals(base(), ca.base());
        }
        return false;
    }

    @Override
    public boolean typeEqualsImpl(Type t) {
        if (t instanceof ConstArrayType) {
            ConstArrayType a = (ConstArrayType) t;
            return ts.typeEquals(base(), a.base());
        }
        return false;
    }

    @Override
    public boolean isCastValidImpl(Type toType) {
        if (!toType.isArray()) {
            return super.isCastValidImpl(toType);
        }

        if (toType instanceof ConstArrayType) {
            return super.isCastValidImpl(toType);
        }

        return false;
    }
}
