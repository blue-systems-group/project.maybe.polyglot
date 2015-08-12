package edu.buffalo.cse.blue.maybe.types;

import polyglot.types.*;
import polyglot.util.Position;
import polyglot.types.Type;

public class MaybeTypeSystem_c extends TypeSystem_c implements MaybeTypeSystem {
    @Override
    public ConstArrayType constArrayOf(Position pos, Type base) {
        return new ConstArrayType_c(this, pos, base);
    }
}
