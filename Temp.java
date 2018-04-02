package gitlet;

import java.io.*;
import java.util.LinkedList;

public class Temp {
    /** convert object to byte array*/
    static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    /** convert byte array to object*/
    static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    static String sha1(Object val) {
        LinkedList<Object> vals = new LinkedList<>();
        vals.add(val);
        return Utils.sha1(vals.toArray(new Object[vals.size()]));
    }

}
