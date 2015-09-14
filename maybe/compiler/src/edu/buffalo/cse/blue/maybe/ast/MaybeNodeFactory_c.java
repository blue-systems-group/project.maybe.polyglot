package edu.buffalo.cse.blue.maybe.ast;

import edu.buffalo.cse.blue.maybe.metadata.Metadata;
import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.ext.jl7.ast.*;
import polyglot.ext.jl5.ast.*;

import java.util.*;


/**
 * NodeFactory for maybe extension.
 */
public class MaybeNodeFactory_c extends JL7NodeFactory_c implements MaybeNodeFactory {
    public MaybeNodeFactory_c(MaybeLang lang, MaybeExtFactory extFactory) {
        super(lang, extFactory);
    }

    private List<Position> getPositionList(List list) {
        List<Position> positionList = new LinkedList<Position>();
        for (Object object : list) {
            if (object instanceof Node) {
                Node node = (Node) object;
                positionList.add(node.position());
            } else {
                System.err.println(object + " is not a Node!");
            }
        }
        return positionList;
    }


    @Override
    public MaybeExtFactory extFactory() {
        return (MaybeExtFactory) super.extFactory();
    }

    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overridden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
    @Override
    public Maybe Maybe(Position pos, Expr cond, List<Block> alternatives) {
        Metadata.INSTANCE.addMaybeBlock(pos, cond.position(), getPositionList(alternatives));
        Maybe n = new Maybe_c(pos, cond, alternatives);
        n = ext(n, extFactory().extIf());
        n = del(n, delFactory().delIf());
        return n;
    }

    @Override
    public MaybeAssign MaybeAssign(Position pos, Expr left, Assign.Operator op, Expr maybeLabel, List<Expr> right) {
        Metadata.INSTANCE.addMaybeVariable(pos, maybeLabel.position(), getPositionList(right));
        if (left instanceof Local) {
            return MaybeLocalAssign(pos, (Local) left, op, maybeLabel, right);
        }
        else if (left instanceof Field) {
            return MaybeFieldAssign(pos, (Field) left, op, maybeLabel, right);
        }
        else if (left instanceof ArrayAccess) {
            return MaybeArrayAccessAssign(pos, (ArrayAccess) left, op, maybeLabel, right);
        }
        return MaybeAmbAssign(pos, left, op, maybeLabel, right);

        // MaybeAssign n = new MaybeAssign_c(pos, left, op, right);
        // n = ext(n, extFactory().extIf());
        // n = del(n, delFactory().delIf());
        // return n;
    }

    @Override
    public MaybeLocalAssign MaybeLocalAssign(Position pos, Local left,
            Assign.Operator op, Expr maybeLabel, List<Expr> right) {
        MaybeLocalAssign n = new MaybeLocalAssign_c(pos, left, op, maybeLabel, right);
        n = ext(n, extFactory().extLocalAssign());
        n = del(n, delFactory().delLocalAssign());
        return n;
    }

    @Override
    public MaybeFieldAssign MaybeFieldAssign(Position pos, Field left,
            Assign.Operator op, Expr maybeLabel, List<Expr> right) {
        MaybeFieldAssign n = new MaybeFieldAssign_c(pos, left, op, maybeLabel, right);
        n = ext(n, extFactory().extFieldAssign());
        n = del(n, delFactory().delFieldAssign());
        return n;
    }

    @Override
    public MaybeArrayAccessAssign MaybeArrayAccessAssign(Position pos, ArrayAccess left,
            Assign.Operator op, Expr maybeLabel, List<Expr> right) {
        MaybeArrayAccessAssign n = new MaybeArrayAccessAssign_c(pos, left, op, maybeLabel, right);
        n = ext(n, extFactory().extArrayAccessAssign());
        n = del(n, delFactory().delArrayAccessAssign());
        return n;
    }

    @Override
    public MaybeAmbAssign MaybeAmbAssign(Position pos, Expr left, Assign.Operator op,
            Expr maybeLabel, List<Expr> right) {
        MaybeAmbAssign n = new MaybeAmbAssign_c(pos, left, op, maybeLabel, right);
        n = ext(n, extFactory().extAmbAssign());
        n = del(n, delFactory().delAmbAssign());
        return n;
    }

    // @Override
    // public LocalDecl LocalDecl(Position pos, Flags flags, TypeNode type,
    //         Id name, Expr init) {
    //     LocalDecl n = new LocalDecl_c(pos, flags, type, name, init);
    //     n = ext(n, extFactory().extLocalDecl());
    //     n = del(n, delFactory().delLocalDecl());
    //     return n;
    // }

    @Override
    public MaybeLocalDecl MaybeLocalDecl(Position pos, Flags flags, TypeNode type, Id name, Expr label, List<Expr> alternatives) {
        Metadata.INSTANCE.addMaybeVariable(pos, label.position(), getPositionList(alternatives));
        MaybeLocalDecl n = new MaybeLocalDecl_c(pos, flags, type, name, label, alternatives);
        // TODO: whould use correct ext
        // n = ext(n, extFactory().extLocalAssign());
        // n = del(n, delFactory().delLocalAssign());
        n = ext(n, extFactory().extLocalDecl());
        n = del(n, delFactory().delLocalDecl());
        JL5LocalDeclExt ext = (JL5LocalDeclExt) JL5Ext.ext(n);
        return (MaybeLocalDecl) ext.annotationElems(new LinkedList<AnnotationElem>());
    }
}
