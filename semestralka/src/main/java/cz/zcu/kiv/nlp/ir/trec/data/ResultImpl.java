package cz.zcu.kiv.nlp.ir.trec.data;

import java.util.Objects;

/**
 * Created by Tigi on 8.1.2015.
 */
public class ResultImpl extends AbstractResult {

    private int documentFrequency;

    public int getTermFreq(){
        return documentFrequency;
    }

    public void setTermFreq(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

}
