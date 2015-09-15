package edu.buffalo.cse.blue.maybe.metadata;

/**
 * Created by xcv58 on 9/15/15.
 */
public class Constants {
    // first level keys
    public final static String HASH = "sha224_hash";
    public final static String PACKAGE = "package";
    // statements should be an array
    // TODO: change to an object?
    public final static String STATEMENTS = "statements";

    // keys in statements
    public final static String CONTENT = "content";
    public final static String LINE = "line";
    public final static String TYPE = "type";
    public final static String LABEL = "label";
    public final static String ALTERNATIVES = "alternatives";

    // keys in alternatives
    public final static String START = "start";
    public final static String END = "end";
    public final static String VALUE = "value";
}
