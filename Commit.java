package gitlet;

//import sun.security.mscapi.KeyStore;

import java.io.*;
//import java.nio.file.Files;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Commit implements Serializable {
    /** ================== Define Field ===================== */
    private final String parentID;
    private final String message;
    private final String date;
    //private Date commitDate;
    private HashMap<String, String> contents;
    //private HashMap<String, Boolean> isRemove = new HashMap<>();//name, boolean

    /** =================== initial commit ====================== */
/*
    public Commit(Commit obj){
        this.parentID = obj.parentID;
        this.message = obj.message;
        this.commitDate = obj.commitDate;
        this.contents = obj.contents;
    }
*/
    public Commit(String message, MyGit mygit) throws Exception {
        this.message = message;
        Date commitDate = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.date = df.format(commitDate);

        //this.date = commitDate.toString();

        if (mygit.getBranchMap().get("master").equals("")) {
            //if it is the init commit
            this.parentID = null;
            this.contents = new HashMap<>();

        } else {
            //if it is not the init commit
            this.parentID = mygit.getHeadID();
            this.contents = mygit.getHeadCommit().getContents();
        }

        this.differChange(mygit.getStagingArea());
        this.removeChange(mygit.getRemoveList());
        this.addFileToBlob();
        clearStagingArea(mygit);
    }

    public void clearStagingArea(MyGit mygit) {
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


    public void setHeadAndBranch(MyGit mygit) throws Exception {
        String newHashID = this.serializeCommit();
        mygit.getBranchMap().put(mygit.getHeadBranch(), newHashID);
    }

    /** ====================== Method ====================== */

    public static Commit getCommitFromID(String hashID) throws Exception {
        return Commit.unserializeCommit(hashID);
    }

    public String serializeCommit() throws Exception {
            //get the hash ID of current commit
        String newCommitID = getIDfromCommit(this);

            //create the file with the hash ID as file name
        File newFile = new File(".gitlet/commit/" + newCommitID + ".ser");
        //newFile.getParentFile().mkdirs();
        newFile.createNewFile();

            //serialize the commit to the new file
        FileOutputStream fs = new FileOutputStream(newFile);
        ObjectOutputStream os = new ObjectOutputStream(fs);
        os.writeObject(this);
        os.close();
            //return commit hash ID
        return newCommitID;
    }

    public String getIDfromCommit(Commit curCommit) throws Exception {
        return Utils.sha1(Temp.convertToBytes(this));

    }

    public static Commit unserializeCommit(String hashID) throws Exception {
        File file = new File(".gitlet/commit/" + hashID + ".ser");
        if (file.exists()) {
            FileInputStream fs = new FileInputStream(".gitlet/commit/" + hashID + ".ser");
            ObjectInputStream os = new ObjectInputStream(fs);
            Object newCommit = os.readObject();
            os.close();
            return (Commit) newCommit;
        } else return null;
    }

    /*
    public String AllString(){
        return getParentID() + getDate() + getMessage() + getContents().toString();
    }*/

    /** Get SHA-1 identifier of my parent, or null if the initial commit. */
    public String toString() {
        if (hasParent()) {
            return parentID;
        }
        return null;
    }

    /** if has parent */
    private boolean hasParent() {
        return !(this.parentID == null);
    }

    /** get message*/
    public String getMessage() {
        return message;
    }

    /** get content*/
    public HashMap<String, String> getContents() {
        return contents;
    }

    /** change maps if they are different */
    private void differChange(HashMap<String, String> stagingArea) {
        for (Object o : stagingArea.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            contents.put((String) entry.getKey(), (String) entry.getValue());
        }
    }

    /** change maps if they are marked removed */
    private void removeChange(LinkedList<String> removedList) {
        if (removedList != null) {
            for (String removedFileName : removedList) {
                if (this.contents.keySet().contains(removedFileName)) {
                    this.contents.remove(removedFileName);
                }
            }
        }
    }

    public String getParentID() {
        return parentID;
    }

    public String getDate() {
        return date;
    }

    public void setContents(HashMap<String, String> contents) {
        this.contents = contents;
    }

    private void addFileToBlob() throws Exception {
        File f = new File(".gitlet/stagingArea");
        File[] files = f.listFiles();
        //List<String> list = new ArrayList<>();
        if (files != null) {
            for (File source : files) {
                File dest = new File(".gitlet/blob/" + source.getName());
                dest.getParentFile().mkdirs();
                //dest.createNewFile();
                //Files.copy(source.toPath(), dest.toPath())
                byte[] fileByte = Utils.readContents(source);
                Utils.writeContents(dest, fileByte);
            }
        }
    }


}
