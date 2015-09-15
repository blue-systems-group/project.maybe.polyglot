package edu.buffalo.cse.blue.maybe.metadata;

import org.json.JSONArray;
import org.json.JSONObject;
import polyglot.types.SemanticException;
import polyglot.util.Position;

import java.util.LinkedList;
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
    private List<Statement> statementList;

    /**
     * private method to really add a maybe block or variable.
     * @param position the position of whole maybe block/variable
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe block/variable
     */
    private void add(Position position, Position label, List<Position> alternatives, MaybeType type) {
        if (statementList == null) {
            statementList = new LinkedList<Statement>();
        }
//        System.out.println(position.path());
//        System.out.println(position.line());
//        System.out.println(type);
        Statement statement = new Statement(position, label, alternatives, type);
        statementList.add(statement);
    }

    /**
     * add a maybe block to metadata
     * @param position the position of whole block
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe block
     */
    public void addMaybeBlock(Position position, Position label, List<Position> alternatives) {
        this.add(position, label, alternatives, MaybeType.BLOCK);
    }

    /**
     * add a maybe variable to metadata
     * @param position the position of whole block
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe variable
     */
    public void addMaybeVariable(Position position, Position label, List<Position> alternatives) {
        this.add(position, label, alternatives, MaybeType.ASSIGNMENT);
    }

    /**
     * Called from Main.java to indicate the compiler finish and ready to generate metadata.
     * @throws SemanticException for duplicated labels.
     */
    public void finish() throws SemanticException {
        JSONObject jsonObject = new JSONObject();
        // TODO: generate real hash
        jsonObject.put(Constants.HASH, Constants.HASH);
        // TODO: get real package name
        jsonObject.put(Constants.PACKAGE, Constants.PACKAGE);
        JSONArray jsonArray = new JSONArray();
        for (Statement statement : statementList) {
            jsonArray.put(statement.toJSON());
        }
        jsonObject.put(Constants.STATEMENTS, jsonArray);
        // TODO: issue post and pretty to file
        System.out.println(jsonObject);
        this.clean();
    }

    /**
     * private method for cleanup internal data.
     * It's not used because current implementation only compile once for per compiler task.
     */
    private void clean(){

    }
}
