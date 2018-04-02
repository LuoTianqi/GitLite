package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws Exception {
        // FILL THIS IN java gitlet.Main checkout test
        Main commands = new Main();
        MyGit mygit = new MyGit();
        File mygitFile = new File(".gitlet/MyGit.ser");
        if (!mygitFile.exists()) {
            if (args[0].equals("init")) {
                if (args.length == 1) commands.init(mygit);
                mygit.serializeMygit();
            } else {
                System.out.println("no gitlet version-control system "
                        + "exists in the current directory.");
            }
        } else {
            mygit.unserializeMygit();

            switch (args[0]) {
                case "init":
                    System.out.println("A gitlet version-control system"
                            + " already exists in the current directory.");
                    break;

                case "add":
                    if (args.length == 2) commands.add(args[1], mygit);
                    break;

                case "commit":
                    if (args.length == 2) commit(args[1], mygit);
                    else if (args.length == 1) {
                        throw new Exception("Please enter a commit message.");
                    } else {
                        printIncorrect();
                    }
                    break;

                case "log":
                    if (args.length == 1) commands.log(mygit);
                    break;

                case "global-log":
                    if (args.length == 1) commands.globalLog();
                    break;

                case "find":
                    if (args.length == 2) commands.find(args[1]);
                    break;

                case "branch":
                    if (args.length == 2) commands.branch(args[1], mygit);
                    break;

                case "rm-branch":
                    if (args.length == 2) commands.rmBranch(args[1], mygit);
                    break;

                case "status":
                    if (args.length == 1) commands.status(mygit);
                    break;

                case "rm":
                    if (args.length == 2) commands.rm(mygit, args[1]);
                    break;

                case "checkout":
                    if (args.length == 2) Checkout.checkoutBranch(args[1], mygit);
                    else if (args.length == 3) {
                        Checkout.checkoutFilename(args[2], mygit);
                    } else if (args.length == 4) {
                        if (args[2].equals("--")) {
                            Checkout.checkout(args[1], args[3], mygit);
                        } else {
                            printIncorrect();
                        }
                    } else {
                        printIncorrect();
                    }
                    break;

                case "merge":
                    if (args.length == 2) commands.merge(args[1], mygit);
                    else printIncorrect();
                    break;

                case "reset":
                    if (args.length == 2) Reset.reset(args[1], mygit);
                    else printIncorrect();
                    break;


                default:
                    break;
            }
            mygit.serializeMygit();
        }

    }

    public static void printIncorrect() {
        System.out.println("Incorrect operands.");
    }

    public void merge(String branchName, MyGit myGit) throws Exception {
        boolean ifNoChanges = myGit.getStagingArea().isEmpty() && myGit.getRemoveList().isEmpty();
        if (!ifNoChanges) {
            System.out.println("You have uncommitted changes.");
        } else if (!myGit.getBranchMap().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (myGit.getHeadBranch().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
        } else {
            Merge.merge(branchName, myGit);
        }
    }

    public void rm(MyGit mygit, String fileName) throws Exception {
        Rm.rm(mygit, fileName);
    }

    public void status(MyGit mygit) throws Exception {
        Status.status(mygit);
    }

    public void rmBranch(String branchName, MyGit mygit) throws Exception {
        Branch.removeBranch(mygit, branchName);
    }

    public void branch(String branchName, MyGit mygit) throws Exception {
        Branch.branch(mygit, branchName);
    }

    public void find(String message) throws Exception {
        Find.find(message);
    }

    public void globalLog() throws Exception {
        GlobalLog newlog = new GlobalLog();
    }

    public void init(MyGit mygit) throws Exception {
        File file = new File(".gitlet");
        if (!file.exists()) {
            file.mkdir();
            file = new File(".gitlet/blob");
            file.mkdir();
            file = new File(".gitlet/commit");
            file.mkdir();
            file = new File(".gitlet/stagingArea");
            file.mkdir();
        }
        mygit.setHead("master");
        mygit.setStagingArea(new HashMap<>());
        mygit.setBranchMap(new HashMap<>());
        mygit.getBranchMap().put("master", "");
        mygit.setRemoveList(new LinkedList<>());
        commit("initial commit", mygit);
    }

    public void log(MyGit mygit) throws Exception {
        Log newlog = new Log(mygit);
    }

    public void add(String fileName, MyGit mygit) throws Exception {
        File curFile = new File(fileName);
        if (curFile.exists()) {
            Add newAdd = new Add(fileName, curFile, mygit);
        } else {
            System.out.println("File does not exist.");
        }
    }

    public static void commit(String message, MyGit mygit) throws Exception {
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
        } else if (!mygit.getBranchMap().get("master").equals("")
                && mygit.getStagingArea().size() == 0
                && mygit.getRemoveList().size() == 0) {
            System.out.println("No changes added to the commit.");
        } else {
            Commit newCommit = new Commit(message, mygit);
            newCommit.setHeadAndBranch(mygit);
        }
    }
}
