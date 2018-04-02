package gitlet;

import java.io.File;
/*
import java.util.HashMap;
import java.util.LinkedList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
*/

public class Checkout {

    public static void checkoutFilename(String filename, MyGit myGit) throws Exception {
        if (!myGit.getHeadCommit().getContents().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
        } else {
            File dest = new File(filename);
            //if (dest.exists()) Utils.restrictedDelete(filename);
            String fileID = myGit.getHeadCommit().getContents().get(filename);
            //dest.createNewFile();
            FileSerializer.unserializeBlob(fileID, dest);
        }

    }


    public static void checkout(String commitID, String filename, MyGit myGit) throws Exception {

        if (!isCommitExist(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        } else {
            File commitsPath = new File(".gitlet/commit");
            File[] commits = commitsPath.listFiles();
            for (File com : commits) {
                if (com.getName().substring(0, 40).contains(commitID)) {
                    commitID = com.getName().substring(0, 40);
                }
            }
        }

        if (!isFileInCommit(filename, commitID)) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        Commit tarCommit = Commit.getCommitFromID(commitID);
        //Utils.restrictedDelete(filename);
        String fileID = tarCommit.getContents().get(filename);
        File dest = new File(filename);
        FileSerializer.unserializeBlob(fileID, dest);

    }

    public static void checkoutBranch(String branchName, MyGit myGit) throws Exception {
        //check if There is an untracked file in the way; delete it or add it first.
        Commit curCommit = myGit.getHeadCommit();
        String branchID = myGit.getBranchMap().get(branchName);
        Commit tarCommit = Commit.getCommitFromID(branchID);
        if (hasUntrackedFile(tarCommit, curCommit)) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }

        //**************** then do the other cases ***********

        if (!myGit.getBranchMap().containsKey(branchName)) {
            System.out.println("No such branch exists.");
        } else if (myGit.getHeadBranch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
        } else {
            //put files in the target commit into working directory
            String targetCommitID = myGit.getBranchMap().get(branchName);
            Commit targetCommit = Commit.getCommitFromID(targetCommitID);
            for (String fileName : targetCommit.getContents().keySet()) {
                checkout(targetCommitID, fileName, myGit);
            }

            for (String fileName : myGit.getHeadCommit().getContents().keySet()) {
                if (!targetCommit.getContents().containsKey(fileName)) {
                    File temp = new File(fileName);
                    if (temp.exists()) {
                        temp.delete();
                    }
                }
            }

            myGit.setHead(branchName);
            myGit.getBranchMap().put(branchName, targetCommitID);
        }
    }

    public static boolean hasUntrackedFile(Commit tarCommit, Commit curCommit) throws Exception {
        if (tarCommit != null) {
            for (String tarFile : tarCommit.getContents().keySet()) {
                if (!curCommit.getContents().containsKey(tarFile)) {
                    File workFile = new File(tarFile);
                    if (workFile.exists()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isCommitExist(String commitID) throws Exception {
        File commitsPath = new File(".gitlet/commit");
        File[] commits = commitsPath.listFiles();
        for (File com : commits) {
            if (com.getName().substring(0, 40).contains(commitID)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFileInCommit(String fileName, String commitID) throws Exception {
        Commit tarCommit = Commit.getCommitFromID(commitID);
        return tarCommit.getContents().containsKey(fileName);
    }
}
