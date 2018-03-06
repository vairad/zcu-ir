package cz.zcu.kiv.nlp.vs;

import cz.zcu.kiv.nlp.ir.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class specifies doctor record with evaluation posts
 * @author Radek Vais
 * @version 20.2.2018
 */
@ToString
@EqualsAndHashCode
public class Record implements Serializable {
    @Getter @Setter private String url;
    @Getter @Setter private String name;
    @Getter @Setter private List<String> details;
    @Getter @Setter private List<Post> posts = new ArrayList<>();
    @Getter @Setter private String html_forum;

    public Record() {
    }

    public void addPost(Post post){
        posts.add(post);
    }

    public static void SaveRecordCollection(String folderPath, List<Record> list) {
        try {
           // String filePath = folderPath + Utils.SDF.format(System.currentTimeMillis()) +"_"+ Record.class.getSimpleName() + ".ser";
            String filePath = folderPath ;
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(list);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in: " + filePath);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static List<Record> LoadRecordCollection(File serializedFile) {
        final Object object;
        try {
            final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile));
            object = objectInputStream.readObject();
            objectInputStream.close();
            return (List<Record>) object;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
