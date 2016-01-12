package edu.buffalo.cse.blue.maybe.visit;

import edu.buffalo.cse.blue.maybe.ast.MaybeLocalAssign;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.EnumConstantDecl;
import polyglot.ext.jl5.visit.JL5DefiniteAssignmentChecker;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.DefiniteAssignmentChecker;
import polyglot.visit.NodeVisitor;

import java.lang.reflect.Constructor;

import polyglot.visit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.CodeNode;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Initializer;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Special;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.visit.FlowGraph.Peer;

public class MaybeDefiniteAssignmentChecker extends JL5DefiniteAssignmentChecker {

    public MaybeDefiniteAssignmentChecker(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    /**
     * Perform the appropriate flow operations for the Terms. This method
     * delegates to other appropriate methods in this class, for modularity.
     *
     * To summarize:
     * - Formals: declaration of a Formal param, just insert a new
     *              DefiniteAssignment for the LocalInstance.
     * - LocalDecl: a declaration of a local variable, just insert a new
     *              DefiniteAssignment for the LocalInstance as appropriate
     *              based on whether the declaration has an initializer or not.
     * - Assign: if the LHS of the assign is a local var or a field that we
     *              are interested in, then increment the min and max counts
     *              for that local var or field.
     */
    @Override
    public Map<EdgeKey, FlowItem> flow(FlowItem trueItem, FlowItem falseItem,
                                       FlowItem otherItem, FlowGraph<FlowItem> graph, Peer<FlowItem> peer) {
        FlowItem inItem =
                safeConfluence(trueItem,
                        FlowGraph.EDGE_KEY_TRUE,
                        falseItem,
                        FlowGraph.EDGE_KEY_FALSE,
                        otherItem,
                        FlowGraph.EDGE_KEY_OTHER,
                        peer,
                        graph);

        FlowItem inDFItem = inItem;
        Node n = peer.node();
        if (n instanceof MaybeLocalAssign) {
            return flowMaybeLocalAssign(inDFItem,
                    graph,
                    (MaybeLocalAssign) n,
                    peer.succEdgeKeys());
        } else {
            return super.flow(trueItem, falseItem, otherItem, graph, peer);
        }
    }

    /**
     * Perform the appropriate flow operations for assignment to a local
     * variable
     */
    protected Map<EdgeKey, FlowItem> flowMaybeLocalAssign(FlowItem inItem, FlowGraph<FlowItem> graph,
                                                          MaybeLocalAssign a, Set<EdgeKey> succEdgeKeys) {
        Local l = a.left();
        Map<VarInstance, AssignmentStatus> m =
                new HashMap<>(inItem.assignmentStatus);
        AssignmentStatus initCount = m.get(l.localInstance().orig());

        initCount = AssignmentStatus.ASS;

        m.put(l.localInstance().orig(), initCount);

        try {
            Class<?> c = Class.forName("polyglot.visit.DefiniteAssignmentChecker$FlowItem");
            Constructor<?> constructor = c.getDeclaredConstructor(Map.class);
            constructor.setAccessible(true);
            Object o = constructor.newInstance(m);
            return DataFlow.<FlowItem>itemToMap((FlowItem) o, succEdgeKeys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
