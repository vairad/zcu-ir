package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Radek Vais
 */
public class FileDictionary implements IDictionary {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Utils.class.getName());

    /** Množina slov slovníku. */
    Set<String> dictionary;

    /**
     * Konstruktor otevře postupně celý seznam souborů předaných jako parametr a uloži je do množiny.
     * @param filesToLoad seznam souborů k načtení.
     */
    FileDictionary(List<String> filesToLoad){
        logger.trace("Start method.");
        dictionary = new HashSet<String>();

        for (String fileName: filesToLoad) {
            try {
                List<String> words = Utils.readTXTFile(new FileInputStream( new File(fileName)));
                dictionary.addAll(words);
            } catch (FileNotFoundException e) {
                logger.warn("File "+fileName+" was not found.");
            }
        }
        if(dictionary.isEmpty()){
            logger.warn("Created empty dictionary.");
        }
        logger.trace("Dictionary created.");
    }

    @Override
    public boolean isPresent(String word) {
        logger.trace("Entry method");
        return dictionary.contains(word);
    }
}
