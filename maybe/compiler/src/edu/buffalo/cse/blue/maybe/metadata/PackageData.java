package edu.buffalo.cse.blue.maybe.metadata;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by xcv58 on 9/17/15.
 */
public class PackageData {
    @SerializedName("package") private String packageName;
    private String sha224_hash;
    private HashMap<String, Statement> statements;

    public PackageData() {
        this.statements = new HashMap<String, Statement>();
    }

    public void addStatement(Statement statement) {
        this.statements.put(statement.getLabel(), statement);
    }

    public void setPackageName(String name) {
        this.packageName = name;
    }

    public void setSha224_hash() {
        assert (this.packageName != null);
        assert (this.statements != null);
        this.sha224_hash = this.getSHA224(this.packageName, this.statements.values());
    }

    /**
     * private method to generate SHA224
     * @param packageName the packageName used in this package
     * @param statementList the statements used in this package
     * @return sha224 string for the jsonObject
     */
    private String getSHA224(String packageName, Collection<Statement> statementList) {
        try {
            // refer from http://www.mkyong.com/java/java-sha-hashing-example/
//            MessageDigest instance = MessageDigest.getInstance("SHA-224");
            // There is no SHA-224 in Java 7
            // TODO: try another method to get sha224
            MessageDigest instance = MessageDigest.getInstance("SHA-256");

            instance.update(packageName.getBytes());
            instance.update(new Gson().toJson(statementList).getBytes());

            byte[] bytes = instance.digest();

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

}
