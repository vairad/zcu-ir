package cz.zcu.kiv.nlp.tigi.brainyDemo.model;

import cz.zcu.fav.liks.math.la.DoubleMatrix;
import cz.zcu.fav.liks.math.la.IntVector;
import cz.zcu.fav.liks.ml.classify.BasicClassificationResults;
import cz.zcu.fav.liks.ml.classify.Classifier;
import cz.zcu.fav.liks.ml.classify.maxent.MaxEntTrainer;
import cz.zcu.fav.liks.ml.features.FeatureSet;
import cz.zcu.fav.liks.ml.lists.BasicInstanceList;
import cz.zcu.fav.liks.ml.lists.BasicTrainingInstanceList;
import cz.zcu.kiv.nlp.tigi.brainyDemo.data.Language;
import cz.zcu.kiv.nlp.tigi.brainyDemo.data.Sentence;

import java.util.*;

/**
 * @author Tigi
 * @author Radek Vais
 */
public class SentimentModel {
    private Classifier classifier;
    private FeatureSet<Sentence> set;
    int numberOfLabels;

    Map<String, Integer> languageMap = new HashMap<String, Integer>();
    Map<Integer, String> inverseLanguageMap = new HashMap<Integer, String>();

    public SentimentModel(List<Language> languages, FeatureSet<Sentence> set) {
        this.set = set;

        List<Sentence> trainData = new ArrayList<Sentence>();
        List<Integer> labels = new ArrayList<Integer>();

        int index = 0;
        for (Language lang: languages) {
            if(!languageMap.containsKey(lang.getId())) {
                languageMap.put(lang.getId(), index);
                inverseLanguageMap.put(index, lang.getId());
                index++;
            }
            for (Sentence sentence: lang.getSentences()) {
                trainData.add(sentence);
                labels.add(languageMap.get(lang.getId()));
            }
        }

        numberOfLabels = languageMap.size();
        // DONE todo vytvorit labely  a trenovaci data
        // DONE todo naplnit languageMap a inverseLanguageMap
        // DONE todo zadat pocet vsech moznych lablu z trenovacich dat (numberOfLabels)

        BasicTrainingInstanceList<Sentence> instances = new BasicTrainingInstanceList<Sentence>(trainData, null, labels, numberOfLabels);

        set.train(instances);

        DoubleMatrix data = set.getData(instances);
        IntVector ls = set.getLabels(instances);

        MaxEntTrainer trainer = new MaxEntTrainer();
        classifier = trainer.train(data, ls, numberOfLabels);
    }

    public List<Sentence> markCategories(List<Sentence> sentences) {

        BasicInstanceList<Sentence> instances = new BasicInstanceList<Sentence>(sentences, null);
        DoubleMatrix data = set.getData(instances);
        BasicClassificationResults results = BasicClassificationResults.create(numberOfLabels, data.columns());
        classifier.classify(data, results);

        IntVector labels = results.getLabels();
//            results.getProbabilities() //alternatively

        for (int index = 0; index < sentences.size(); ++index){
            Sentence sen = sentences.get(index);
            sen.setLang(inverseLanguageMap.get(labels.get(index)));
        }

        //DONE todo oznacit ve vsech sentences prirazeny language label

        return sentences;
    }
}
