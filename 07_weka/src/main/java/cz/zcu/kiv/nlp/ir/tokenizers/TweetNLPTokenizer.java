package cz.zcu.kiv.nlp.ir.tokenizers;

import cmu.arktweetnlp.Twokenize;
import java.util.Iterator;
import java.util.List;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.tokenizers.Tokenizer;

public class TweetNLPTokenizer extends Tokenizer {
    private static final long serialVersionUID = 4352757127093531518L;
    protected transient Iterator<String> m_tokenIterator;

    public TweetNLPTokenizer() {
    }

    public String globalInfo() {
        return "A Twitter-specific tokenizer based on the CMU TweetNLP library.\n" + this.getTechnicalInformation().toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result = new TechnicalInformation(Type.INPROCEEDINGS);
        result.setValue(Field.AUTHOR, "Gimpel, Kevin and Schneider, Nathan and O'Connor, Brendan and Das, Dipanjan and Mills, Daniel and Eisenstein, Jacob and Heilman, Michael and Yogatama, Dani and Flanigan, Jeffrey and Smith, Noah A");
        result.setValue(Field.TITLE, "Part-of-speech tagging for twitter: Annotation, features, and experiments");
        result.setValue(Field.YEAR, "2011");
        result.setValue(Field.URL, "http://www.cs.cmu.edu/~ark/TweetNLP/");
        result.setValue(Field.NOTE, "The Weka tokenizer works with version 0.32 of TweetNLP.");
        return result;
    }

    public boolean hasMoreElements() {
        return this.m_tokenIterator.hasNext();
    }

    public String nextElement() {
        return (String)this.m_tokenIterator.next();
    }

    public void tokenize(String s) {
        List<String> words = Twokenize.tokenizeRawTweetText(s);
        this.m_tokenIterator = words.iterator();
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1 $");
    }

    public static void main(String[] args) {
        runTokenizer(new TweetNLPTokenizer(), args);
    }
}
