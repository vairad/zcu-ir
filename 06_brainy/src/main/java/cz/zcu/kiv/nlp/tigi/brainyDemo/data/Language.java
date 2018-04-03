package cz.zcu.kiv.nlp.tigi.brainyDemo.data;

import java.util.*;
/**
 * Created by Tigi on 11.12.2015.
 */
public class Language {
    private String id;
    private List<Sentence> sentences= new ArrayList<Sentence>();

    public Language(String id, List<Sentence> sentences) {
        this.id = id;
        this.sentences = sentences;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
