package gitlet;

import java.io.*;

/**
 * Created by 罗天琦 on 2017/7/18.
 */
public class FileSerializer {
    //serialize file into staging area
    public static void serializeBlob(String fileID, File file) throws Exception {
        byte[] fileByte = Utils.readContents(file);
        try {
            //File file = new File(filename);
            //File newFile = new File(".gitlet/stagingArea/" + fileID + ".ser");
            //newFile.getParentFile().mkdirs();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(".gitlet"
                    + "/stagingArea/" + fileID + ".ser"));
            out.writeObject(fileByte);
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
/*
        File newFile = new File(".gitlet/stagingArea/" + fileID + ".ser");
        //File newFile = new File(".gitlet/stagingArea/" + fileID + ".ser");
        newFile.getParentFile().mkdirs();
        //newFile.createNewFile();
        byte[] fileByte = Utils.readContents(file);

        FileOutputStream fs = new FileOutputStream(newFile);
        ObjectOutputStream os = new ObjectOutputStream(fs);
        os.writeObject(fileByte);
        os.close();
        */
    }

    //unserialize file into working directory (dest file)
    public static void unserializeBlob(String fileID, File dest) throws Exception {
        byte[] b;
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(".gitlet"
                    + "/blob/" + fileID + ".ser"));
            b = (byte[]) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            b = null;
        }
        //dest.createNewFile();
        Utils.writeContents(dest, b);

    }

    public static byte[] unseriaBlobToByte(String fileID) {
        byte[] b;
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(".gitlet"
                    + "/blob/" + fileID + ".ser"));
            b = (byte[]) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException e) {
            b = null;
        }

        return b;
    }

/*
    public static String getFileFromID(String fileID) {

    }
*/
    public static String getHashIDFromFile(File file) throws Exception {
        return Utils.sha1(Utils.readContents(file));
    }

}
