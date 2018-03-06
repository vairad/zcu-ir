package cz.zcu.kiv.nlp.ir;


import java.util.*;

/**
 * Created by Tigi on 29.2.2016.
 */
public class BasicPreprocessing implements Preprocessing {

    Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
    Stemmer stemmer;
    Tokenizer tokenizer;
    Set<String> stopwords;
    boolean removeAccentsBeforeStemming;
    boolean removeAccentsAfterStemming;
    boolean toLowercase;

    public BasicPreprocessing(Stemmer stemmer, Tokenizer tokenizer, Set<String> stopwords, boolean removeAccentsBeforeStemming, boolean removeAccentsAfterStemming, boolean toLowercase) {
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.stopwords = stopwords;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
    }

    @Override
    public void index(String document) {
        if (toLowercase) {
            document = document.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            document = AdvancedTokenizer.removeAccents(document);
        }
        for (String token : tokenizer.tokenize(document)) {
            if (stopwords != null){
                if(stopwords.contains(token)){
                   continue;
                }
            }
            if (stemmer != null) {
                token = stemmer.stem(token);
            }
            if (removeAccentsAfterStemming) {
                token = AdvancedTokenizer.removeAccents(token);
            }
            if (!wordFrequencies.containsKey(token)) {
                wordFrequencies.put(token, 0);
            }

            wordFrequencies.put(token, wordFrequencies.get(token) + 1);
        }
    }

    @Override
    public String getProcessedForm(String text) {
        if (toLowercase) {
            text = text.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            text = AdvancedTokenizer.removeAccents(text);
        }
        if (stemmer != null) {
            text = stemmer.stem(text);
        }
        if (removeAccentsAfterStemming) {
            text = AdvancedTokenizer.removeAccents(text);
        }
        return text;
    }

    final String withDiacritics = "áÁčČďĎéÉěĚíÍňŇóÓřŘšŠťŤúÚůŮýÝžŽ";
    final String withoutDiacritics = "aAcCdDeEeEiInNoOrRsStTuUuUyYzZ";

    private String removeAccents(String text) {
        for (int i = 0; i < withDiacritics.length(); i++) {
            text = text.replaceAll("" + withDiacritics.charAt(i), "" + withoutDiacritics.charAt(i));
        }
        return text;
    }

    public Map<String, Integer> getWordFrequencies() {
        return wordFrequencies;
    }
}
