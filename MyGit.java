package gitlet;
import java.io.*;
import java.util.*;

public class MyGit {
    /** current branch name*/
    private String head;

    /** map of staging area ====== key: fileName, value: hash ID*/
    private HashMap<String, String> stagingArea;

    /** map of branches ========== key: branch name, value: hash ID of the first commit*/
    private HashMap<String, String> branchMap;

    /** mark of removed file, record its name*/
    private LinkedList<String> removeList;

    public static LinkedList<String> getRemoveList(MyGit mygit) {
        return mygit.removeList;
    }

    /*public static void setRemoveList(LinkedList<String> removeList) {
        MyGit.removeList = removeList;
    }*/

    public void unserializeMygit() throws Exception {
        FileInputStream fs = new FileInputStream(".gitlet/MyGit.ser");
        ObjectInputStream os = new ObjectInputStream(fs);

        head = (String) os.readObject();
        stagingArea = (HashMap<String, String>) os.readObject();
        branchMap = (HashMap<String, String>) os.readObject();
        removeList = (LinkedList<String>) os.readObject();
        os.close();
    }

    public void serializeMygit() throws Exception {
        File file = new File(".gitlet/MyGit.ser");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        FileOutputStream fs = new FileOutputStream(".gitlet/MyGit.ser");
        ObjectOutputStream os = new ObjectOutputStream(fs);
        os.writeObject(head);
        os.writeObject(stagingArea);
        os.writeObject(branchMap);
        os.writeObject(removeList);
        os.close();
    }

    public String getHeadID() {
        return this.getBranchMap().get(this.getHeadBranch());
    }

    public Commit getHeadCommit() throws Exception {
        String headCommitID = this.getBranchMap().get(this.getHeadBranch());
        return Commit.unserializeCommit(headCommitID);
    }

    public String getHeadBranch() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public HashMap<String, String> getStagingArea() {
        return stagingArea;
    }

    public void setStagingArea(HashMap<String, String> stagingArea) {
        this.stagingArea = stagingArea;
    }

    public HashMap<String, String> getBranchMap() {
        return branchMap;
    }

    public void setBranchMap(HashMap<String, String> branchMap) {
        this.branchMap = branchMap;
    }

    public LinkedList<String> getRemoveList() {
        return removeList;
    }

    public void setRemoveList(LinkedList<String> removeList) {
        this.removeList = removeList;
    }
}
