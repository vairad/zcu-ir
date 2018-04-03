package cz.zcu.kiv.nlp.vs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class specifies doctor record with evaluation posts
 * @author Radek Vais
 * @version 20.2.2018
 */
public class Record implements Serializable {
    private String url;
    private String name;
    private List<String> details;
    private List<Post> posts = new ArrayList<>();
    private String html_forum;

    public Record() {
    }

    public void addPost(Post post){
        posts.add(post);
    }

    public static void SaveRecordCollection(String folderPath, List<Record> list) {
        try {
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

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Record)) return false;
        final Record other = (Record) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$url = this.url;
        final Object other$url = other.url;
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
        final Object this$name = this.name;
        final Object other$name = other.name;
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$details = this.details;
        final Object other$details = other.details;
        if (this$details == null ? other$details != null : !this$details.equals(other$details)) return false;
        final Object this$posts = this.posts;
        final Object other$posts = other.posts;
        if (this$posts == null ? other$posts != null : !this$posts.equals(other$posts)) return false;
        final Object this$html_forum = this.html_forum;
        final Object other$html_forum = other.html_forum;
        if (this$html_forum == null ? other$html_forum != null : !this$html_forum.equals(other$html_forum))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $url = this.url;
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        final Object $name = this.name;
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $details = this.details;
        result = result * PRIME + ($details == null ? 43 : $details.hashCode());
        final Object $posts = this.posts;
        result = result * PRIME + ($posts == null ? 43 : $posts.hashCode());
        final Object $html_forum = this.html_forum;
        result = result * PRIME + ($html_forum == null ? 43 : $html_forum.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Record;
    }

    public String toString() {
        return "Record(url=" + this.url + ", name=" + this.name + ", details=" + this.details + ", posts=" + this.posts + ", html_forum=" + this.html_forum + ")";
    }

    public String getUrl() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDetails() {
        return this.details;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public String getHtml_forum() {
        return this.html_forum;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setHtml_forum(String html_forum) {
        this.html_forum = html_forum;
    }
}
