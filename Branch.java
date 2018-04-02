package gitlet;

public class Branch {
    public static void branch(MyGit myGit, String branchName) throws Exception {
        if (myGit.getBranchMap().containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            myGit.getBranchMap().put(branchName, myGit.getHeadID());
        }
    }

    public static void removeBranch(MyGit myGit, String branchName) throws Exception {
        if (!myGit.getBranchMap().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (myGit.getHeadBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            myGit.getBranchMap().remove(branchName);
        }
    }
}
