package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Radek Vais
 * @version 27.3.2018
 */
public class BasicTokenizer implements ITokenizer {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(BasicTokenizer.class.getName());

    private String defaultRegex = "\\s+";

    private IDictionary stopwords;

    public BasicTokenizer(IDictionary stopwords){
       // logger.trace("Entry method");
        this.stopwords = stopwords;
    }

    private static List<String> tokenize(String text, String regex) {
        return Arrays.asList(text.split(regex));
    }

    @Override
    public List<String> getTokens(String text) {
      //  logger.trace("Entry method");
        List<String> results = tokenize(text, defaultRegex);
        List<String> filtered = new LinkedList<>();
        if(stopwords != null){
            //logger.trace("Removing stopwords");
            for (String result: results) {
                if(stopwords.isPresent(result)){
                    continue;
                }
                filtered.add(result);
            }
            return filtered;
        } else {
            return results;
        }
    }


    @Override
    public String toString(){
        return this.getClass().getName();
    }
}
