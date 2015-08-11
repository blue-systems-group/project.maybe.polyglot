package edu.buffalo.cse.blue.maybe.types;

import polyglot.types.*;
import polyglot.util.Position;
import polyglot.types.Type;

public class CArrayTypeSystem_c extends TypeSystem_c implements CArrayTypeSystem {
    @Override
    public ConstArrayType constArrayOf(Position pos, Type base) {
        return new ConstArrayType_c(this, pos, base);
    }
}
