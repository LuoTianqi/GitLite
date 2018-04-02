package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Find {
    public static void find(String commitMessage) throws Exception {
        List<String> gitList = addAllFile(".gitlet/commit");
        int count = 0;
        for (String list : gitList) {
            Commit myCommit = unserialization(".gitlet/commit/" + list);
            if (myCommit.getMessage().equals(commitMessage)) {
                System.out.println(list.substring(0, list.length() - 4));
                count++;
            }
        }
        if (count == 0) System.out.println("Found no commit with that message");
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

    private static Commit unserialization(String hashID) throws Exception {
        FileInputStream fs = new FileInputStream(hashID);
        ObjectInputStream os = new ObjectInputStream(fs);
        Object newCommit = os.readObject();
        os.close();
        return (Commit) newCommit;
    }
}
