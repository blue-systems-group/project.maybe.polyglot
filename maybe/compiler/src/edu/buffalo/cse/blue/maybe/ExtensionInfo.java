package edu.buffalo.cse.blue.maybe;

import polyglot.lex.Lexer;
import edu.buffalo.cse.blue.maybe.parse.Lexer_c;
import edu.buffalo.cse.blue.maybe.parse.Grm;
import edu.buffalo.cse.blue.maybe.ast.*;
import edu.buffalo.cse.blue.maybe.types.*;
import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.main.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.ext.jl5.ast.JL5ExtFactory_c;
import polyglot.ext.jl7.parse.*;
import polyglot.ext.jl7.ast.*;
import polyglot.ext.jl7.types.*;
import polyglot.ext.jl7.*;

import java.io.*;
import java.util.Set;

/**
 * Extension information for maybe extension.
 */
public class ExtensionInfo extends JL7ExtensionInfo {
    static {
        // force Topics to load
        @SuppressWarnings("unused")
        Topics t = new Topics();
    }

    @Override
    public String defaultFileExtension() {
        return "java";
    }

    @Override
    public String compilerName() {
        return "maybec";
    }

    @Override
    public Parser parser(Reader reader, Source source, ErrorQueue eq) {
        Lexer lexer = new Lexer_c(reader, source, eq);
        Grm grm = new Grm(lexer, ts, nf, eq);
        return new CupParser(grm, source, eq);
    }

    @Override
    public Set<String> keywords() {
	return new Lexer_c(null).keywords();
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new MaybeNodeFactory_c(MaybeLang_c.instance,
                                new MaybeExtFactory_c(new JL7ExtFactory_c(new JL5ExtFactory_c())));
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new MaybeTypeSystem_c();
    }

}
