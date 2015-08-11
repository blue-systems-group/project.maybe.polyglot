package edu.buffalo.cse.blue.maybe.types;

import polyglot.types.*;
import polyglot.util.Position;

public interface CArrayTypeSystem extends TypeSystem {
    ConstArrayType constArrayOf(Position pos, Type base);
}
