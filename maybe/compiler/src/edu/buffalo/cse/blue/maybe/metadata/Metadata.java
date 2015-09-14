package edu.buffalo.cse.blue.maybe.metadata;

import polyglot.util.Position;

import java.util.List;

/**
 * Created by xcv58 on 9/14/15.
 * 1. generate metadata for maybe project.
 * 2. post metadata to maybe backend
 */
public enum Metadata {
    INSTANCE;

    // TODO: read file
    // TODO: sort by position
    // TODO: get android package name
    // TODO: generate statements, alternatives, and start/end

    /**
     * private method to really add a maybe block or variable.
     * @param position the position of whole maybe block/variable
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe block/variable
     */
    private void add(Position position, Position label, List<Position> alternatives, MaybeType type) {
        System.out.println(position.path());
        System.out.println(position.line());
        System.out.println(type);
    }

    /**
     * add a maybe block to metadata
     * @param position the position of whole block
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe block
     */
    public void addMaybeBlock(Position position, Position label, List<Position> alternatives) {
        System.out.println("Maybe Block");
        this.add(position, label, alternatives, MaybeType.BLOCK);
    }

    /**
     * add a maybe variable to metadata
     * @param position the position of whole block
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe variable
     */
    public void addMaybeVariable(Position position, Position label, List<Position> alternatives) {
        System.out.println("Maybe Variable");
        this.add(position, label, alternatives, MaybeType.ASSIGNMENT);
    }

    public void finish() {
        System.out.println("finish");
        this.clean();
    }

    public void clean(){

    }
}
