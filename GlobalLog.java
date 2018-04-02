package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class GlobalLog {
    public GlobalLog() throws Exception {
        List<String> gitList = addAllFile(".gitlet/commit");
        for (String list : gitList) {
            Commit myCommit = unserialization(".gitlet/commit/" + list);
            System.out.println("===");
            System.out.println("Commit " + list.substring(0, list.length() - 4));
            System.out.println(myCommit.getDate());
            System.out.println(myCommit.getMessage());
            System.out.println();
        }
    }

    private Commit unserialization(String hashID) throws Exception {
        FileInputStream fs = new FileInputStream(hashID);
        ObjectInputStream os = new ObjectInputStream(fs);
        Object newCommit = os.readObject();
        os.close();
        return (Commit) newCommit;
    }

    private List<String> addAllFile(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles();
        List<String> list = new ArrayList<>();
        for (File file : files) {
            list.add(file.getName());
        }
        return list;
    }
    /* =========================== test ===========================*/
    /* public static void main(String[] args) {
        String filePath = "/Users/catherine/Desktop/learning-git/group61/lab13";
        new GlobalLog().addAllFile(filePath);
    } */
}
