package cz.zcu.kiv.nlp.ir.trec.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Třída symbolizující vlastnosti tokenu v invertovaném indexu
 *
 * @author Radek Vais
 */
public class TokenProperties implements Serializable {

    private String token;

    private Map<String, Integer> postings;

    /**
     * Konstruktor připraví prostor pro mapu dokumentů.
     * @param token token, jehož informace objekt uchovává
     */
    public TokenProperties(String token){
        this.token = token;
        postings = new HashMap<>();
    }

    /**
     * Zažaď výskyt v dokumentu dle ID do vlastností tokenu.
     * @param docID id dokumentu.
     */
    public void addDocument(String docID){
        Integer wordCount = postings.get(docID);
        if(wordCount == null ){
            postings.put(docID, 1);
        }else{
            postings.put(docID, ++wordCount);
        }
    }

    /**
     * Vrť seznam výskytů tokenu v dokumentech.
     * @return mapa výskytů dokument : termFreq
     */
    public Map<String, Integer> getPostings() {
        return postings;
    }

    /**
     * Vrcí název tokenu v objektu.
     * @return token
     */
    public String getToken() {
        return token;
    }
}

