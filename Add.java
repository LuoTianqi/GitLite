package gitlet;

import java.util.*;
import java.io.*;

public class Add {

    public Add(String fileName, File file, MyGit mygit) throws Exception {
        if (mygit.getRemoveList() != null) {
            if (mygit.getRemoveList().contains(fileName)) {
                mygit.getRemoveList().remove(fileName);
            }
        }

        //serialize file
        String fileID = FileSerializer.getHashIDFromFile(file);
        if (isChanged(fileName, fileID, mygit.getHeadCommit())) {
            FileSerializer.serializeBlob(fileID, file);
            mygit.getStagingArea().put(fileName, fileID);
        }
    }

    //if the file is changed from current commit
    public static boolean isChanged(String fileName, String fileID, Commit headCommit) {
        if (headCommit.getContents().isEmpty()) {
            return true;
        } else {
            if (headCommit.getContents().containsKey(fileName)) {
                return !headCommit.getContents().get(fileName).equals(fileID);
            } else {
                return true;
            }
        }
    }

}
