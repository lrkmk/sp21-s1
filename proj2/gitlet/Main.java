package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    break;
                }
                Repository.commit(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "checkout":
                if (args.length == 2) {
                    String branch = args[1];
                    Repository.checkoutBranch(branch);
                } else if (args.length == 3) {
                    String filename = args[2];
                    Repository.checkoutFile(filename);
                } else if (args.length == 4) {
                    String commitID = args[1];
                    String filename = args[3];
                    Repository.checkoutFileWithID(commitID, filename);
                }
        }
    }
}
