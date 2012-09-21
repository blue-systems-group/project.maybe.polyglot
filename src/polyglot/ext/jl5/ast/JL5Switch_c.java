/*******************************************************************************
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2012 Polyglot project group, Cornell University
 * Copyright (c) 2006-2012 IBM Corporation
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This program and the accompanying materials are made available under
 * the terms of the Lesser GNU Public License v2.0 which accompanies this
 * distribution.
 * 
 * The development of the Polyglot project has been supported by a
 * number of funding sources, including DARPA Contract F30602-99-1-0533,
 * monitored by USAF Rome Laboratory, ONR Grants N00014-01-1-0968 and
 * N00014-09-1-0652, NSF Grants CNS-0208642, CNS-0430161, CCF-0133302,
 * and CCF-1054172, AFRL Contract FA8650-10-C-7022, an Alfred P. Sloan 
 * Research Fellowship, and an Intel Research Ph.D. Fellowship.
 *
 * See README for contributors.
 ******************************************************************************/
package polyglot.ext.jl5.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.SwitchElement;
import polyglot.ast.Switch_c;
import polyglot.ext.jl5.types.JL5Flags;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class JL5Switch_c extends Switch_c implements JL5Switch {

    public JL5Switch_c(Position pos, Expr expr, List<SwitchElement> elements) {
        super(pos, expr, elements);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {

        if (!isAcceptableSwitchType(expr.type())) {
            throw new SemanticException("Switch index must be of type char, byte, short, int, Character, Byte, Short, Integer, or an enum type.",
                                        position());
        }

        ArrayList<SwitchElement> newels =
                new ArrayList<SwitchElement>(elements.size());
        Type switchType = expr.type();
        for (SwitchElement el : elements()) {
            if (el instanceof JL5Case) {
                el =
                        (SwitchElement) ((JL5Case) el).resolveCaseLabel(tc,
                                                                        switchType);
            }
            newels.add(el);
        }
        return elements(newels);
    }

    protected boolean isAcceptableSwitchType(Type type) {
        JL5TypeSystem ts = (JL5TypeSystem) type.typeSystem();
        if (ts.Char().equals(type) || ts.Byte().equals(type)
                || ts.Short().equals(type) || ts.Int().equals(type)) {
            return true;
        }
        if (ts.wrapperClassOfPrimitive(ts.Char()).equals(type)
                || ts.wrapperClassOfPrimitive(ts.Byte()).equals(type)
                || ts.wrapperClassOfPrimitive(ts.Short()).equals(type)
                || ts.wrapperClassOfPrimitive(ts.Int()).equals(type)) {
            return true;
        }
        if (type.isClass() && JL5Flags.isEnum(type.toClass().flags())) {
            return true;
        }
        return false;
    }

}
