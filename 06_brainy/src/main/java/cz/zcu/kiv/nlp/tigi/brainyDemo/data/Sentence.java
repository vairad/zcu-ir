package cz.zcu.kiv.nlp.tigi.brainyDemo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by Tigi on 11.12.2015.
 */
public class Sentence {
    private String id;
    private String lang;
    private String text;
    private List<String> tokens;
    private List<String> stemms;

    public static final String defaultRegex = "(\\d+[.,](\\d+)?)|([\\p{L}\\d]+)|(<.*?>)|([\\p{Punct}])";

    public Sentence(String lang, String text) {
        this.lang = lang;
        this.text = text;
        Pattern pattern = Pattern.compile(defaultRegex);

        tokens = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            tokens.add(text.substring(start, end));
        }

        stemms = new ArrayList<String>();
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String> getStemms() {
        return stemms;
    }

    public void setStemms(List<String> stemms) {
        this.stemms = stemms;
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "text='" + text + '\'' +
                '}';
    }
}
