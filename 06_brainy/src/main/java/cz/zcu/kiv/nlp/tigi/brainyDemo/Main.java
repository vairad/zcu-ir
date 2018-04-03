package cz.zcu.kiv.nlp.tigi.brainyDemo;

import cz.zcu.fav.liks.ml.features.FeatureSet;
import cz.zcu.kiv.nlp.tigi.brainyDemo.data.Language;
import cz.zcu.kiv.nlp.tigi.brainyDemo.data.Sentence;
import cz.zcu.kiv.nlp.tigi.brainyDemo.features.Bigram;
import cz.zcu.kiv.nlp.tigi.brainyDemo.features.CharNGram;
import cz.zcu.kiv.nlp.tigi.brainyDemo.features.Unigram;
import cz.zcu.kiv.nlp.tigi.brainyDemo.model.SentimentModel;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tigi on 11.12.2015.
 */
public class Main {

    private static final int MAX_SIZE = 10000;
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        Logger.getLogger("cz.zcu.fav.liks.math").setLevel(Level.ERROR);

        List<Language> languages = loadTrainData();
        Set<String> languageNames = new HashSet<String>();
        long start = new Date().getTime();
        for (Language l : languages) {
            languageNames.add(l.getId());
        }

        FeatureSet<Sentence> featureSet = new FeatureSet<Sentence>();
        //1000 features:
        // featureSet.add(new CharNGram(4,2));
        //result: 110552 features, Accuracy: 0.9699523809523809, end runtime 01:40

        //1000 features:
//        featureSet.add(new Unigram(10));
        //result: Trained 2767 features, Accuracy: 0.9205714285714286, end runtime 00:08

        // 1000 features
//        featureSet.add(new Unigram(10));
//        featureSet.add(new Bigram(5));
//        featureSet.add(new CharNGram(4,20));
        //result: Trained 25428 features, Accuracy: 0.965, end runtime 00:44


        //10000 features:
//         featureSet.add(new CharNGram(4,20));
        //result: Trained 118709 features, Accuracy: 0.9741428571428571, end runtime 16:32

        //10000 features:
//        featureSet.add(new Unigram(20));
        //result: Trained 17239 features, Accuracy: 0.9524285714285714, end runtime 03:52

        // 10000 features
//        featureSet.add(new Unigram(20));
//        featureSet.add(new Bigram(50));
        //result: Trained 19658 features, Accuracy: 0.9519047619047619, end runtime 04:41

        // 10000 features
        featureSet.add(new Unigram(200));
        featureSet.add(new Bigram(500));
        featureSet.add(new CharNGram(4,1000));
        //result: Trained 4434 features, Accuracy: 0.9678571428571429, end runtime 06:36



        //todo pridat featury - bigram, trigram, character - ngramy 1,2,3
        //todo nezapomente na threshold
        //todo zmerte (zaznamenejte dosazene vysledky s jednotlivymi feature sety a pokuste se dosahnout co nejlepsich vysledku Accuracy, s co nejmene featurami (trained features) za co nejkratsi cas ;)
        //todo optimalizovat featury pro vetsi rychlost a efektivitu (mene pameti) - zkusit si zvetsit MAX_SIZE lze az 100000!

        SentimentModel sentimentModel = new SentimentModel(languages, featureSet);

        List<Sentence> goldData = getTestData(languageNames, "data/europarl.test");
        List<Sentence> testData = new ArrayList<Sentence>();

        for (int i = 0; i < goldData.size(); i++) {
            Sentence s = goldData.get(i);
            testData.add(new Sentence("", s.getText()));
        }

        List<Sentence> results = sentimentModel.markCategories(testData);

        if (results.size() != goldData.size()) {
            throw new IllegalStateException("result size does not match gold data size!!!");
        }

        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
        for (int i = 0; i < goldData.size(); i++) {
            String goldLabel = goldData.get(i).getLang();
            String predictedLabel = results.get(i).getLang();
            confusionMatrix.increaseValue(goldLabel, predictedLabel);
        }
        log.info("Accuracy: " + confusionMatrix.getAccuracy());
        final java.text.DateFormat time = new SimpleDateFormat("mm:ss");
        log.info("end runtime " + time.format(new Date(new Date().getTime() - start)));
        log.info("end runtime ms: " + (new Date().getTime() - start));
    }

    private static List<Language> loadTrainData() {
        List<Language> languages = new ArrayList<Language>();
        languages.add(getLanguage("bg", "data/bul_wikipedia_2007_100K-sentences.txt"));
        languages.add(getLanguage("cs", "data/ces_newscrawl_2011_100K-sentences.txt"));
        languages.add(getLanguage("da", "data/dan_wikipedia_2007_100K-sentences.txt"));
        languages.add(getLanguage("de", "data/deu_wikipedia_2007_100K-sentences.txt"));
        languages.add(getLanguage("el", "data/ell_web_2011_100K-sentences.txt"));
        languages.add(getLanguage("en", "data/eng_wikipedia_2010_100K-sentences.txt"));
        languages.add(getLanguage("es", "data/spa_wikipedia_2011_100K-sentences.txt"));
        languages.add(getLanguage("et", "data/est_newscrawl_2011_100K-sentences.txt"));
        languages.add(getLanguage("fi", "data/fin_wikipedia_2012_100K-sentences.txt"));
        languages.add(getLanguage("fr", "data/fra_wikipedia_2010_100K-sentences.txt"));
        languages.add(getLanguage("hu", "data/hun_mixed_2012_100K-sentences.txt"));
        languages.add(getLanguage("it", "data/ita_news_2010_100K-sentences.txt"));
        languages.add(getLanguage("lt", "data/lit_wikipedia_2007_100K-sentences.txt"));
        languages.add(getLanguage("lv", "data/lav_newscrawl_2011_100K-sentences.txt"));
        languages.add(getLanguage("nl", "data/nld_mixed_2012_100K-sentences.txt"));
        languages.add(getLanguage("pl", "data/pol_wikipedia_2010_100K-sentences.txt"));
        languages.add(getLanguage("pt", "data/por-pt_newscrawl_2011_100K-sentences.txt"));
        languages.add(getLanguage("ro", "data/ron_wikipedia_2011_100K-sentences.txt"));
        languages.add(getLanguage("sk", "data/slk_wikipedia_2007_100K-sentences.txt"));
        languages.add(getLanguage("sl", "data/slv_wikipedia_2007_100K-sentences.txt"));
        languages.add(getLanguage("sv", "data/swe_web_2002_100K-sentences.txt"));

        return languages;
    }

    private static List<Sentence> getTestData(Set<String> languages, String path) {
        final ArrayList<Sentence> data = new ArrayList<Sentence>();
        try {
            final List<String> sentences = IOUtils.readLines(new FileInputStream(new File(path)));
            for (String line : sentences) {
                String lang = line.substring(0, line.indexOf("\t"));
                String sentence = line.substring(line.indexOf("\t") + 1, line.length());
                final Sentence s = new Sentence(lang, sentence);
                if (languages.contains(lang)) {
                    data.add(s);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static Language getLanguage(String lang, String path) {
        Language language = null;
        final ArrayList<Sentence> data = new ArrayList<Sentence>();
        try {
            final List<String> sentences = IOUtils.readLines(new FileInputStream(new File(path)));
            for (String line : sentences) {
                String id = line.substring(0, line.indexOf("\t"));
                String sentence = line.substring(line.indexOf("\t") + 1, line.length());
                final Sentence s = new Sentence(lang, sentence);
                s.setId(id);
                if (data.size() < MAX_SIZE) {
                    data.add(s);
                }
            }
            language = new Language(lang, data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return language;
    }
}