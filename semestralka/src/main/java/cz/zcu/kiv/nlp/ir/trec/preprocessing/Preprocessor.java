package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Preprocessor implements IPreprocessor {

    /** instance loggeru */
    static Logger logger = LogManager.getLogger(Preprocessor.class.getName());

    private IStemmer stemmer;
    private ITokenizer tokenizer;

    @Override
    public void initilise(IStemmer stemmer, ITokenizer tokenizer) {

    }

    @Override
    public List<String> getProcessedForm(String sentence) {
        return null;
    }
}
