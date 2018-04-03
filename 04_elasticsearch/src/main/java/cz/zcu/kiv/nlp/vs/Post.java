package cz.zcu.kiv.nlp.vs;

import java.io.Serializable;
import java.util.Date;

/**
 * Class specifies one doctor's evaluation post
 * @author Radek Vais
 * @version 20.2.2018
 */
public class Post implements Serializable {
    private String author;
    private Date created;
    private String content;

    public Post(String content, String author, Date created) {
        this.author = author;
        this.content = content;
        this.created = created;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Post)) return false;
        final Post other = (Post) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$author = this.author;
        final Object other$author = other.author;
        if (this$author == null ? other$author != null : !this$author.equals(other$author)) return false;
        final Object this$created = this.created;
        final Object other$created = other.created;
        if (this$created == null ? other$created != null : !this$created.equals(other$created)) return false;
        final Object this$content = this.content;
        final Object other$content = other.content;
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $author = this.author;
        result = result * PRIME + ($author == null ? 43 : $author.hashCode());
        final Object $created = this.created;
        result = result * PRIME + ($created == null ? 43 : $created.hashCode());
        final Object $content = this.content;
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Post;
    }

    public String toString() {
        return "Post(author=" + this.author + ", created=" + this.created + ", content=" + this.content + ")";
    }

    public String getAuthor() {
        return this.author;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getContent() {
        return this.content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
