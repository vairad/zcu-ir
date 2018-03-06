package cz.zcu.kiv.nlp.vs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Class specifies one doctor's evaluation post
 * @author Radek Vais
 * @version 20.2.2018
 */
@ToString
@EqualsAndHashCode
public class Post implements Serializable {
    @Getter @Setter private String author;
    @Getter @Setter private Date created;
    @Getter @Setter private String content;

    public Post(String content, String author, Date created) {
        this.author = author;
        this.content = content;
        this.created = created;
    }
}
