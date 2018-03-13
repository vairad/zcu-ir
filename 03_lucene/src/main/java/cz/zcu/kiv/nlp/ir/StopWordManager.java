package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWordManager {

    private Set<String> stopWords;

    private static String[] stopWordFiles = {
//              "cz.txt"
//              "czechST.txt"
//              "stopwords.txt"
              "czech-stopwords.txt"
    };


    public StopWordManager(boolean removeAccents) {
        stopWords = new HashSet<String>();
        try {
            for (String stopWordsFile: stopWordFiles) {
                List<String> words = Utils.readTXTFile(new FileInputStream( new File(stopWordsFile)));
                for (String word : words ) {
                       stopWords.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getStopWords(){
        return stopWords;
    }
}
