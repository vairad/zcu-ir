package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Továrna instancí preprocessoru
 * @author Radek Vais
 */
public class PreprocessorFactory {

    /** instance loggeru - továrna zapisuje do Preprocessor logu */
    private static Logger logger = LogManager.getLogger(Preprocessor.class.getName());

    /**
     * Tovární metoda pro instanci preprocessoru
     * @param aggressive agresivní stemmer true/false
     * @param advanced advanced tokenizet true/false
     * @param stopFiles seznam souborů se stopwords
     * @return instance preprocessoruPreprocessorFactory
     */
    public static IPreprocessor preparePreprocessor(boolean aggressive, boolean advanced, String[] stopFiles){
        logger.info("Preparing preprocessor");
        IPreprocessor preprocessor = new Preprocessor();

        IDictionary stopWords = new FileDictionary(Arrays.asList(stopFiles));

        IStemmer stemmer;
        if(aggressive){
            logger.trace("Agressive stemmer");
            stemmer = new CzechStemmerAgressive();
        }else{
            logger.trace("Light stemmer");
            stemmer = new CzechStemmerLight();
        }

        ITokenizer tokenizer;
        if(advanced){
            logger.trace("Advanced tokenizer");
            tokenizer = new AdvancedTokenizer(stopWords);
        }else{
            logger.trace("Basic tokenizer");
            tokenizer  = new BasicTokenizer(stopWords);
        }

        preprocessor.initialise(stemmer, tokenizer);
        return preprocessor;
    }
}
