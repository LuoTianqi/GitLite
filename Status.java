package gitlet;

import java.util.HashMap;
import java.io.File;
import java.util.LinkedList;
//import java.util.Stack;

public class Status {
    public static void status(MyGit myGit) throws Exception {
        /* print branches */
        System.out.println("=== Branches ===");
        LinkedList<String> keys = new LinkedList<>();
        //LinkedList<String> keys = new LinkedList<>();
        for (String key : myGit.getBranchMap().keySet()) {
            if (key.equals("master")) keys.add(key);
        }

        for (String key : myGit.getBranchMap().keySet()) {
            if (!key.equals("master")) keys.add(key);
        }

        for (String key : keys) {
            if (key.equals(myGit.getHeadBranch())) {
                System.out.println("*" + key);
            } else {
                System.out.println(key);
            }
        }

        /* print staged area file */
        System.out.println("\n=== Staged Files ===");
        for (String key : myGit.getStagingArea().keySet()) {
            System.out.println(key);
        }
        
        /* print removed files */
        System.out.println("\n=== Removed Files ===");
        if (myGit.getRemoveList() != null) {
            for (String list : myGit.getRemoveList()) {
                System.out.println(list);
            }
        }

        /* print Modifications Not Staged For Commit  */
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        //printModifiedFile(myGit);

        /* print files present in the working directory
        but neither staged for addition nor tracked  */
        System.out.println("\n=== Untracked Files ===");
        //printUntrackedFile(myGit);

    }

    private static void printUntrackedFile(MyGit mygit) throws Exception {
        File f = new File("");
        File[] files = f.listFiles();
        if (files != null) {
            Commit curCommit = mygit.getHeadCommit();
            for (File file : files) {
                String fileName = file.getName();
                if (!mygit.getStagingArea().containsKey(fileName)) {
                    if (!curCommit.getContents().containsKey(fileName)) {
                        System.out.println(fileName);
                    }
                }
            }
        }
    }


    private static void printModifiedFile(MyGit mygit) throws Exception {
        Commit curCommit = mygit.getHeadCommit();
        HashMap<String, String> curContents = curCommit.getContents();
        LinkedList<String> modifiedList = new LinkedList<String>();
        //iterate the current commit
        for (String key : curContents.keySet()) {
            File curFile = new File(key);
            if (curFile.exists()) {
                String newHashID = FileSerializer.getHashIDFromFile(curFile);
                if (!newHashID.equals(curContents.get(key))) {
                    if (!newHashID.equals(mygit.getStagingArea().get(key))) {
                        //System.out.println(key);
                        modifiedList.add(key);
                    }
                }
            } else {
                if (!mygit.getRemoveList().contains(key)) {
                    modifiedList.add(key);
                }
            }
        }
        // iterate the staging area
        for (String key : mygit.getStagingArea().keySet()) {
            File curFile = new File(key);
            if (curFile.exists()) {
                String newHashID = FileSerializer.getHashIDFromFile(curFile);
                if (!newHashID.equals(mygit.getStagingArea().get(key))) {
                    if (!modifiedList.contains(key)) {
                        modifiedList.add(key);
                    }
                }
            } else {
                if (!modifiedList.contains(key)) {
                    modifiedList.add(key);
                }
            }
        }
        //print the modified list
        for (String modifiedFile : modifiedList) {
            System.out.println(modifiedFile);
        }
    }

}
