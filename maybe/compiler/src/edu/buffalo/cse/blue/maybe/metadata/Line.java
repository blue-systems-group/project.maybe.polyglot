package edu.buffalo.cse.blue.maybe.metadata;

/**
 * Created by xcv58 on 9/15/15.
 */
public class Line {
    protected String content;
    protected int length;

    public Line(String s) {
        content = s;
        length = s.length();
    }
}
