package edu.buffalo.cse.blue.maybe.metadata;

import com.google.gson.Gson;
import polyglot.main.Main;
import polyglot.util.Position;

import java.io.IOException;
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
    private PackageData packageData;

    /**
     * private method to really add a maybe block or variable.
     * @param position the position of whole maybe block/variable
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe block/variable
     */
    private void add(Position position, Position label, List<Position> alternatives, MaybeType type) {
        if (packageData == null) {
            packageData = new PackageData();
        }
        packageData.addStatement(new Statement(position, label, alternatives, type));
    }

    /**
     * add a maybe block to metadata
     * @param position the position of whole block
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe block
     */
    public void addMaybeBlock(Position position, Position label, List<Position> alternatives) {
        this.add(position, label, alternatives, MaybeType.block);
    }

    /**
     * add a maybe variable to metadata
     * @param position the position of whole block
     * @param label the maybe label
     * @param alternatives the Position list for alternatives of this maybe variable
     */
    public void addMaybeVariable(Position position, Position label, List<Position> alternatives) {
        this.add(position, label, alternatives, MaybeType.assignment);
    }

    /**
     * Called from Main.java to indicate the compiler finish and ready to generate metadata.
     * @param packageName the packageName in Metadata
     * @param url the url to POST Metadata
     */
    public void finish(String packageName, String url) throws Main.TerminationException {
        this.packageData.setPackageName(packageName);
        this.packageData.setSha224_hash();

//        String jsonString = new Gson().toJson(this.packageData);

        // DONE: issue post and pretty to file
        try {
            new Post().post(url, this.packageData);
        } catch (IOException e) {
            throw new Main.TerminationException("IOException: " + e);
        }

        // TODO: pretty to file
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
    private void clean() {
        this.packageData = null;
    }
}
