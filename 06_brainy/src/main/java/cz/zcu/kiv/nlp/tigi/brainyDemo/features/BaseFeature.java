package cz.zcu.kiv.nlp.tigi.brainyDemo.features;

import java.util.HashMap;

/**
 * Class describe common function for reature.
 *
 * @author Radek VAIS
 * @version 27.3.2018
 */
public class BaseFeature {


    protected HashMap<String, Integer>  wordFrequencyMap;
    protected int threshold;

    BaseFeature(int threshold){
        this.threshold = threshold;
    }

    protected void applyThreshold(HashMap<String, Integer>  words ){
        wordFrequencyMap = new HashMap<String, Integer>();
        int index = 0;
        for (String record : words.keySet()) {
            int count = words.get(record);
            if (count > threshold) {
                wordFrequencyMap.put(record, index);
                index++;
            }
        }
    }
}
