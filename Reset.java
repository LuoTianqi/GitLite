package gitlet;

import java.io.File;
import java.util.*;

public class Reset {
    public static void reset(String commitID, MyGit myGit) throws Exception {
        List<String> gitList = addAllFile(".gitlet/commit");
        boolean isExist = false;

        for (String commitList : gitList) {
            String commitName = commitList.substring(0, commitList.length() - 4);
            if (commitName.equals(commitID)) {
                Commit newCommit = Commit.getCommitFromID(commitName);
                // key is the name of each commits' file
                for (String key : newCommit.getContents().keySet()) {
                    File curFile = new File(key);
                    if (!myGit.getHeadCommit().getContents().containsKey(key) && curFile.exists()) {
                        System.out.println("There is an untracked file in the way;"
                                + " delete it or add it first.");
                    }
                    Checkout.checkout(commitID, key, myGit);
                }
                // remove
                for (String key : myGit.getHeadCommit().getContents().keySet()) {
                    if (!newCommit.getContents().containsKey(key)) {
                        Rm.rm(myGit, key);
                    }
                }
                //String branch = refresh(myGit.getHeadBranch(), commitID, myGit);
                //myGit.setHead(branch);
                myGit.getBranchMap().put(myGit.getHeadBranch(), commitID);
                clearStagingArea(myGit);

                isExist = true;
                break;
            }
        }
        if (!isExist) System.out.println("No commit with that id exists.");
    }
    private static List<String> addAllFile(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles();
        List<String> list = new ArrayList<>();
        for (File file : files) {
            list.add(file.getName());
        }
        return list;
    }

    private static void clearStagingArea(MyGit mygit) {
        /* clear staging area after commit*/
        mygit.setRemoveList(new LinkedList<>());
        mygit.setStagingArea(new HashMap<>());
        //clear staging area
        File stagingFileDir = new File(".gitlet/stagingArea");
        if (stagingFileDir.exists()) {
            File[] fileList = stagingFileDir.listFiles();
            if (fileList != null) {
                for (File file : fileList) file.delete();
            }
        }
    }

    private static String refresh(String branch, String commitID, MyGit myGit) throws Exception {
        //String newBranch = branch;
        for (Object o : myGit.getBranchMap().entrySet()) {
            if (((Map.Entry) o).getKey().equals(branch)) {
                String curID = myGit.getHeadID();
                Commit curCommit = Commit.getCommitFromID(curID);
                while (curCommit != null) {
                    if (curID.equals(commitID)) {
                        return branch;
                    }
                    curID = curCommit.getParentID();
                    curCommit = Commit.getCommitFromID(curID);
                }
            }
            String curID = (String) ((Map.Entry) o).getValue();
            Commit curCommit = Commit.getCommitFromID(curID);
            while (curCommit != null) {
                if (curID.equals(commitID)) {
                    return (String) ((Map.Entry) o).getKey();
                }
                curID = curCommit.getParentID();
                curCommit = Commit.getCommitFromID(curID);
            }
        }
        return branch;
    }
}
