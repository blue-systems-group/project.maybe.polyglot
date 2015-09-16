package edu.buffalo.cse.blue.maybe.metadata;

import org.json.JSONArray;
import org.json.JSONObject;
import polyglot.main.Main;
import polyglot.util.Position;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * private method to generate SHA224
     * @param jsonObject the json object contains maybe metadata
     * @return sha224 string for the jsonObject
     */
    private String getSHA224(JSONObject jsonObject) {
        try {
            // refer from http://www.mkyong.com/java/java-sha-hashing-example/
            MessageDigest instance = MessageDigest.getInstance("SHA-224");
            byte[] bytes = instance.digest(jsonObject.toString().getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "no SHA-224 algorithm found!";
        }
    }

    /**
     * private method to generate JSONArray for statements
     * @param list Statement list
     * @return JSONArray contains JSONObjects for all Statement in list
     */
    private JSONArray getStatementJSONArray(List<Statement> list) {
        JSONArray jsonArray = new JSONArray();
        for (Statement statement : list) {
            jsonArray.put(statement.toJSON());
        }
        return jsonArray;
    }

    /**
     * Called from Main.java to indicate the compiler finish and ready to generate metadata.
     * @param packageName the packageName in Metadata
     * @param url the url to POST Metadata
     */
    public void finish(String packageName, String url) throws Main.TerminationException {
        JSONObject jsonObject = new JSONObject();

        // TODO: get real package name
        jsonObject.put(Constants.PACKAGE, packageName);

        jsonObject.put(Constants.STATEMENTS, this.getStatementJSONArray(statementList));

        jsonObject.put(Constants.HASH, this.getSHA224(jsonObject));

        // TODO: issue post and pretty to file
        try {
            new Post().post(url, jsonObject);
        } catch (IOException e) {
            throw new Main.TerminationException("IOException: " + e);
        }

//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("t.json"));
//            writer.write(jsonObject.toString(2));
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.clean();
    }

    /**
     * private method for cleanup internal data.
     * It's not used because current implementation only compile once for per compiler task.
     */
    private void clean(){

    }
}
