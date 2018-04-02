package gitlet;

import java.util.LinkedList;
import java.io.File;

public class Rm {
    /** Remove the file from the working directory if it was tracked in the current commit.
     * if the file had been staged, then unstage it, but don't remove it from the working directory.
     * f the file is neither staged nor tracked by the head commit */

    public static void rm(MyGit myGit, String filename) throws Exception {
        if (myGit.getHeadCommit().getContents().containsKey(filename)) {
            // delete from commit content
            //myGit.getHeadCommit().getContents().remove(filename);
            if (myGit.getStagingArea().containsKey(filename)) {
                // if exits at staging area, remove it.
                deleteStagingArea(filename, myGit);
            }
            // add to remove list
            if (myGit.getRemoveList() == null) {
                myGit.setRemoveList(new LinkedList<>());
                myGit.getRemoveList().addFirst(filename);
            } else {
                myGit.getRemoveList().addFirst(filename);
            }
            // delete local files
            Utils.restrictedDelete(filename);
        } else if (myGit.getStagingArea().containsKey(filename)) {
            // if exits at staging area, remove it.
            deleteStagingArea(filename, myGit);
        } else {
            System.out.println("No reason to remove the file");
        }
    }

    private static void deleteStagingArea(String filename, MyGit mygit) {
        String fileID = mygit.getStagingArea().get(filename);
        File newFile = new File(".gitlet/stagingArea/" + fileID + ".ser");
        if (newFile.exists()) newFile.delete();
        mygit.getStagingArea().remove(filename);
    }
}
