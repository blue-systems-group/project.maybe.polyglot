package edu.buffalo.cse.blue.maybe;

import edu.buffalo.cse.blue.maybe.metadata.Constants;
import edu.buffalo.cse.blue.maybe.metadata.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Main is the main program of the compiler extension.
 * It simply invokes Polyglot's main, passing in the extension's
 * ExtensionInfo.
 */
public class Main {
    private static String packageName;
    private static String url = "http://maybe.xcv58.me";

    /* modifies args */
    private String[] processArgs(String[] args) throws polyglot.main.Main.TerminationException{
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-package")) {
                if (i + 1 >= args.length) {
                    throw new polyglot.main.Main.TerminationException("missing argument, " +
                            "you need provide a valid package name after -package");
                }
                packageName = args[++i];
            } else if (args[i].equals("-url")) {
                if (i + 1 >= args.length) {
                    throw new polyglot.main.Main.TerminationException("missing argument, " +
                            "you need provide a valid url after -url");
                }
                url = args[++i];
            } else {
                list.add(args[i]);
            }
        }
        if (packageName == null) {
            throw new polyglot.main.Main.TerminationException("You need provide a valid package name by parameter -package");
        }
        return list.toArray(new String[list.size()]);
    }

    private String getPOSTUrl(String s) {
        if (s.charAt(s.length() - 1) != '/') {
            s += "/";
        }
        return s + Constants.URL_SUFFIX;
    }

    public void start(String[] args) {
        polyglot.main.Main polyglotMain = new polyglot.main.Main();
        // TODO: add arg for whether POST metadata or not
        // TODO: add arg for custom URL
        // TODO: add arg for custom package name?

        try {
            args = this.processArgs(args);
            polyglotMain.start(args, new edu.buffalo.cse.blue.maybe.ExtensionInfo());
            Metadata.INSTANCE.finish(packageName, getPOSTUrl(url));
        } catch (polyglot.main.Main.TerminationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Main().start(args);
    }
}
