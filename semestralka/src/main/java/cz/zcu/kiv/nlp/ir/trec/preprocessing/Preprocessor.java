package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Instance preprocessingu, která vyžaduje ke korektnímu fungování tokenizér.
 * Preprocesing provede nejdříve tokenizaci a na tokeny aplikuje stemming.
 *
 * @author Radek Vais
 */
public class Preprocessor implements IPreprocessor {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Preprocessor.class.getName());

    private IStemmer stemmer;
    private ITokenizer tokenizer;

    @Override
    public void initialise(IStemmer stemmer, ITokenizer tokenizer) {
        logger.trace("Entry method");
        if(stemmer != null){
            logger.debug("Stemmer set up: "+stemmer);
            this.stemmer = stemmer;
        }

        if(tokenizer != null){
            logger.debug("Set up tokenizer: "+tokenizer);
            this.tokenizer = tokenizer;
        }
        logger.trace("End method");
    }

    @Override
    public List<String> getProcessedForm(String sentence) {
        logger.trace("Entry method");
        if(tokenizer == null){
            logger.warn("Tokenizer was not set");
            throw new IllegalStateException("Tokenizer was not set.");
        }

        List<String> tokens = tokenizer.getTokens(sentence);

        if(stemmer != null){
            logger.debug("Steming");
            List<String> stemmed = new ArrayList<>(tokens.size());
            for (String token: tokens) {
                stemmed.add(stemmer.stem(token));
            }
            tokens = stemmed;
        }
        logger.trace("End method");
        return tokens;
    }
}
