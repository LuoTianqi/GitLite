package gitlet;


public class Log {
    public Log(MyGit mygit) throws Exception {
        String curID = mygit.getHeadID();
        Commit curCommit = Commit.getCommitFromID(curID);

        while (curCommit != null) {
            System.out.println("===");
            System.out.println("Commit " + curID);
            System.out.println(curCommit.getDate());
            System.out.println(curCommit.getMessage());
            System.out.println();
            curID = curCommit.getParentID();
            curCommit = Commit.getCommitFromID(curID);
        }
    }

}
