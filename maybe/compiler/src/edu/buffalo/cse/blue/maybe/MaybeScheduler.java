package edu.buffalo.cse.blue.maybe;

import edu.buffalo.cse.blue.maybe.visit.MaybeDefiniteAssignmentChecker;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.translate.JL5ToJLRewriter;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.visit.AnnotationChecker;
import polyglot.ext.jl5.visit.AutoBoxer;
import polyglot.ext.jl5.visit.JL5DefiniteAssignmentChecker;
import polyglot.ext.jl5.visit.JL5Translator;
import polyglot.ext.jl5.visit.RemoveAnnotations;
import polyglot.ext.jl5.visit.RemoveEnums;
import polyglot.ext.jl5.visit.RemoveExtendedFors;
import polyglot.ext.jl5.visit.RemoveStaticImports;
import polyglot.ext.jl5.visit.RemoveVarArgsFlags;
import polyglot.ext.jl5.visit.RemoveVarargVisitor;
import polyglot.ext.jl5.visit.SimplifyExpressionsForBoxing;
import polyglot.ext.jl5.visit.TVCaster;
import polyglot.ext.jl5.visit.TypeErasureProcDecls;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.JLExtensionInfo;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.OutputPass;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.CodeGenerated;
import polyglot.frontend.goals.EmptyGoal;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.main.Options;
import polyglot.types.ParsedClassType;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl7.JL7Scheduler;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.DefiniteAssignmentChecker;
import polyglot.visit.NodeVisitor;

public class MaybeScheduler extends JL7Scheduler {

    public MaybeScheduler(JLExtensionInfo extInfo) {
        super(extInfo);
    }

    @Override
    public Goal InitializationsChecked(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g =
                new VisitorGoal(job, new MaybeDefiniteAssignmentChecker(job,
                        ts,
                        nf));
        try {
            g.addPrerequisiteGoal(ReachabilityChecked(job), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return this.internGoal(g);
    }
}
