package gitlet;


//import java.util.HashMap;
import java.io.File;
import java.util.Arrays;

public class Merge {

    public static void merge(String branchName, MyGit myGit) throws Exception {
        Commit curCommit = myGit.getHeadCommit();
        String branchID = myGit.getBranchMap().get(branchName);
        Commit tarCommit = Commit.getCommitFromID(branchID);
        if (Checkout.hasUntrackedFile(tarCommit, curCommit)) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }

        String targetCommitID = myGit.getBranchMap().get(branchName);
        boolean noConflict = true;
        if (!caseOnALine(targetCommitID, myGit)) {
            Commit spiltPoint = getSpiltPoint(branchName, myGit);
            if (spiltPoint != null) {
                //Commit targetCommit = Commit.getCommitFromID(targetCommitID);
                noConflict = mergeFiles(spiltPoint, targetCommitID, myGit);
            }
        }

        if (!noConflict) {
            System.out.println("Encountered a merge conflict.");
        } else {
            String message = "Merged " + myGit.getHeadBranch() + " with " + branchName + ".";
            Commit newCommit = new Commit(message, myGit);
            newCommit.setHeadAndBranch(myGit);
        }

    }

    private static boolean mergeFiles(Commit spiltPoint,
                                   String tarCommitID, MyGit myGit) throws Exception {
        Commit curCommit = myGit.getHeadCommit();
        Commit tarCommit = Commit.getCommitFromID(tarCommitID);
        String curCommitID = myGit.getHeadID();
        boolean notConflict = true;

        /* TODO cases that the file exist in spilt point*/
        for (String spiltFileName : spiltPoint.getContents().keySet()) {
            //for all the file contained in spilt point
            String spiltFileID = spiltPoint.getContents().get(spiltFileName);
            if (curCommit.getContents().containsKey(spiltFileName)) {
                //if it's contained in current commit
                if (curCommit.getContents().get(spiltFileName).equals(spiltFileID)) {
                    //if it's NOT changed in current commit
                    if (tarCommit.getContents().containsKey(spiltFileName)) {
                        //if it exist in target commit
                        String tarFileID = tarCommit.getContents().get(spiltFileName);
                        if (!tarFileID.equals(spiltFileID)) {
                            //************************** TYPE 1 *************************
                            checkAndStage(tarCommitID, spiltFileName, myGit);
                        }
                    } else {
                        //if it not exist in target commit
                        //************************** TYPE 3 *************************
                        Rm.rm(myGit, spiltFileName);
                    }
                } else {
                    //if it's changed in current commit
                    if (tarCommit.getContents().containsKey(spiltFileName)) {
                        //if it exist in target
                        String tarFileID = tarCommit.getContents().get(spiltFileName);
                        if (!tarFileID.equals(spiltFileID)) {
                            //conflict!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            //************************** TYPE 7 *************************
                            conflictAction(spiltFileName, curCommitID, tarCommitID);
                            notConflict = false;
                        }
                    } else {
                        //if it NOT exist in target
                        //conflict!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //************************** TYPE 8 *************************
                        conflictAction(spiltFileName, curCommitID, tarCommitID);
                        notConflict = false;
                    }
                }
            } else {
                //if it not exist in current commit
                if (tarCommit.getContents().containsKey(spiltFileName)) {
                    //if it exist in target
                    String tarFileID = tarCommit.getContents().get(spiltFileName);
                    if (!tarFileID.equals(spiltFileID)) {
                        //if it is modified in target commit
                        //conflict!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //************************** TYPE 9 *************************
                        conflictAction(spiltFileName, curCommitID, tarCommitID);
                        notConflict = false;
                    }
                }
            }
        }

        /* TODO cases that the file NOT exist in spilt point*/
        for (String tarFileName : tarCommit.getContents().keySet()) {
            //String curFileID = curCommit.getContents().get(curFileName);
            String tarFileID = tarCommit.getContents().get(tarFileName);
            if (!spiltPoint.getContents().containsKey(tarFileName)) {
                // if it NOT exist in spilt point
                if (!curCommit.getContents().containsKey(tarFileName)) {
                    //if it NOT exist in current commit
                    //************************** TYPE 6 *************************
                    checkAndStage(tarCommitID, tarFileName, myGit);
                } else {
                    //if it exist in current commit
                    if (!tarFileID.equals(curCommit.getContents().get(tarFileName))) {
                        //conflict!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //************************** TYPE 10 *************************
                        conflictAction(tarFileName, curCommitID, tarCommitID);
                        notConflict = false;
                    }
                }
            }
        }

        return notConflict;
    }

    private static void conflictAction(String fileName,
                                       String curID, String tarID) throws Exception {
        // change
        String curFileID = Commit.getCommitFromID(curID).getContents().get(fileName);
        String tarFileID = Commit.getCommitFromID(tarID).getContents().get(fileName);
        byte[] curByte = FileSerializer.unseriaBlobToByte(curFileID);
        byte[] tarByte = FileSerializer.unseriaBlobToByte(tarFileID);
        String curFileCon;
        String tarFileCon;

        if (curByte != null) {
            curFileCon = new String(curByte, "utf-8");
        } else {
            curFileCon = "";
        }

        if (tarByte != null) {
            tarFileCon = new String(tarByte, "utf-8");
        } else {
            tarFileCon = "";
        }

        String s1 = "<<<<<<< HEAD\n";
        String s2 = "=======\n";
        String s3 = ">>>>>>>\n";

        s1 = new String(s1.getBytes("gbk"), "utf-8");
        s2 = new String(s2.getBytes("gbk"), "utf-8");
        s3 = new String(s3.getBytes("gbk"), "utf-8");

        String allString = s1 + curFileCon + s2 + tarFileCon + s3;
        //System.out.println(allString);
        //byte[] allByte = Temp.convertToBytes(allString);
        byte[] allByte = allString.getBytes("utf-8");

        File allFile = new File(fileName);
        Utils.writeContents(allFile, allByte);

    }

    public static  byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private static void checkAndStage(String tarCommitID,
                                      String fileName, MyGit myGit) throws Exception {
        Checkout.checkout(tarCommitID, fileName, myGit);
        File newFile = new File(fileName);
        if (newFile.exists()) {
            Add newAdd = new Add(fileName, newFile, myGit);
        }
    }

    private static Commit getSpiltPoint(String branchName, MyGit myGit) throws Exception {
        String tempCurID = myGit.getHeadID();
        while (tempCurID != null) {
            String tempTargetID = myGit.getBranchMap().get(branchName);
            while (tempTargetID != null) {
                if (tempTargetID.equals(tempCurID)) {
                    return Commit.getCommitFromID(tempCurID);
                } else {
                    tempTargetID = Commit.getCommitFromID(tempTargetID).getParentID();
                }
            }
            tempCurID = Commit.getCommitFromID(tempCurID).getParentID();
        }
        return null;
    }

    /** return true if spilt point, target branch, current branch are in the same line.
     * Also do the merge action of these cases*/
    private static boolean caseOnALine(String targetID, MyGit myGit) throws Exception {
        String curCommitID = myGit.getHeadID();
        // check if the given branch is an ancestor of the current branch
        String tempCurID = curCommitID; //use a temp String so we don't change the arguments
        while (tempCurID != null) {
            if (tempCurID.equals(targetID)) {
                System.out.println("Given branch is an ancestor of the current branch.");
                return true;
            } else {
                tempCurID = Commit.getCommitFromID(tempCurID).getParentID();
            }
        }

        //check if Current branch fast-forwarded.
        String tempTargetID = targetID; //use a temp String so we don't change the arguments
        while (tempTargetID != null) {
            if (tempTargetID.equals(curCommitID)) {
                System.out.println("Current branch fast-forwarded.");
                myGit.getBranchMap().put(myGit.getHeadBranch(), targetID);
                return true;
            } else {
                tempTargetID = Commit.getCommitFromID(tempTargetID).getParentID();
            }
        }
        return false;
    }

}
