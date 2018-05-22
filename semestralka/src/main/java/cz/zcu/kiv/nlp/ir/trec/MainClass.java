package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.gui.SearchWindow;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainClass {

    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
        System.setProperty("output.dir", TestTrecEval.OUTPUT_DIR);
        System.setProperty("log4j.configurationFile", "log-conf.xml");
    }

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(TestTrecEval.class.getName());

    public static void main(String args[]) {
        List<String> argsList = Arrays.asList(args);

        if(argsList.contains("-eval")){
            TestTrecEval.start(args);
            return;
        }

        if(argsList.contains("-gui")){
            SearchWindow.processGui(args);
        }
    }

    public static IPreprocessor preparePreprocessor(boolean agressive, boolean advanced, String[] stopFiles, String pathToIndex){
        logger.info("Preparing preprocessor");
        IPreprocessor preprocessor = new Preprocessor();

        IDictionary stopWords = new FileDictionary(Arrays.asList(stopFiles));

        IStemmer stemmer;
        if(agressive){
            stemmer = new CzechStemmerAgressive();
        }else{
            stemmer = new CzechStemmerLight();
        }

        ITokenizer tokenizer;
        if(advanced){
            tokenizer = new AdvancedTokenizer(stopWords);
        }else{
            tokenizer  = new BasicTokenizer(stopWords);
        }

        preprocessor.initialise(stemmer, tokenizer);
        return preprocessor;
    }

}