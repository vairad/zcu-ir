package cz.zcu.kiv.nlp.ir.trec.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TokenProperties implements Serializable {

    private String token;

    private Map<String, Integer> postings;

    public TokenProperties(String token){
        this.token = token;
        postings = new HashMap<>();
    }

    public void addDocument(String docID){
        Integer wordCount = postings.get(docID);
        if(wordCount == null ){
            postings.put(docID, 1);
        }else{
            postings.put(docID, ++wordCount);
        }
    }

    public Map<String, Integer> getPostings() {
        return postings;
    }

    public String getToken() {
        return token;
    }
}

