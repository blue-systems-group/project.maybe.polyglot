package edu.buffalo.cse.blue.maybe;

import edu.buffalo.cse.blue.maybe.metadata.Metadata;
import polyglot.types.SemanticException;

/**
 * Main is the main program of the compiler extension.
 * It simply invokes Polyglot's main, passing in the extension's
 * ExtensionInfo.
 */
public class Main
{
  public static void main(String[] args) {
      polyglot.main.Main polyglotMain = new polyglot.main.Main();

      try {
          polyglotMain.start(args, new edu.buffalo.cse.blue.maybe.ExtensionInfo());
          Metadata.INSTANCE.finish();
      } catch (polyglot.main.Main.TerminationException e) {
          System.err.println(e.getMessage());
          System.exit(1);
      } catch (SemanticException e) {
          System.err.println(e.getMessage());
          System.exit(1);
      }
  }
}
