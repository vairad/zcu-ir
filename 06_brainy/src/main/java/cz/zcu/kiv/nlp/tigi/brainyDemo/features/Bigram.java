package cz.zcu.kiv.nlp.tigi.brainyDemo.features;

import cz.zcu.fav.liks.ml.features.Feature;
import cz.zcu.fav.liks.ml.features.FeatureVectorGenerator;
import cz.zcu.fav.liks.ml.lists.InstanceList;
import cz.zcu.fav.liks.ml.lists.ListIterator;
import cz.zcu.kiv.nlp.tigi.brainyDemo.data.Sentence;

import java.util.HashMap;
import java.util.List;

/**
 * @author Radek VAIS
 *
 * @version 27.3.2018
 */
public class Bigram extends BaseFeature implements Feature<Sentence> {

    private static final long serialVersionUID = 84002172983443223L;

    public Bigram(int threshold) {
        super(threshold);
    }

    @Override
    public void extractFeature(ListIterator<Sentence> instances, FeatureVectorGenerator generator) {
        Sentence sentence = instances.getCurrent();
        List<String> tokens = sentence.getTokens();
        for (int i = 0; i < tokens.size() - 1; i++) {
            String word = tokens.get(i)+ "_" + tokens.get(i+1);

            Integer index = wordFrequencyMap.get(word);
            if (index == null) {
                generator.setFeature(wordFrequencyMap.size(), 1.0);
            } else {
                generator.setFeature(index, 1.0);
            }
        }
    }

    @Override
    public int getNumberOfFeatures() {
        return wordFrequencyMap.size() + 1;
    }

    @Override
    public void train(InstanceList<Sentence> instances) {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        ListIterator<Sentence> iterator = instances.iterator();
        while (iterator.hasNext()) {
            Sentence sentence = iterator.next();
            List<String> tokens = sentence.getTokens();

            if (tokens == null) {
                throw new IllegalStateException("SentimentDocument contains no tokens");
            }

            for (int i = 0; i < tokens.size() - 1 ; i++) {
                String word = tokens.get(i)+ "_" + tokens.get(i+1);

                Integer count = counts.get(word);
                if (count == null) {
                    counts.put(word, 1);
                } else {
                    counts.put(word, count + 1);
                }
            }
        }

        applyThreshold(counts);

    }

}

