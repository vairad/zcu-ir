package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.IPreprocessor;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Radek Vais
 */

public class Index implements Indexer, Searcher {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Index.class.getName());

    private IPreprocessor preprocessor;

    private Map<String, Map<String, Integer>> inMemoryIndex;


    Index(String filePath, IPreprocessor preprocessor){
        this(preprocessor);
        File indexFile = new File(filePath);
        loadIndex(indexFile);
    }

    Index(IPreprocessor preprocessor){
        logger.trace("Entry method");
        this.preprocessor = preprocessor;
        inMemoryIndex = new HashMap<>();
    }


    private void addTokensToIndex(List<String> tokens, String docId){
        for (String token: tokens) {
            Map<String, Integer> wordRecord = inMemoryIndex.get(token);
            if(wordRecord == null){ //when word is not in index
                wordRecord = new HashMap<>();
                wordRecord.put(docId, 1);
                inMemoryIndex.put(token, wordRecord);
                continue;
            }

            Integer wordFrequency = wordRecord.get(docId);
            if (wordFrequency == null){
                wordRecord.put(docId, 1);
            }else {
                wordRecord.replace(docId, ++wordFrequency);
            }

        }
    }

    @Override
    public void index(List<Document> documents) {
        logger.trace("Entry method");
        for (Document document: documents) {
            List<String> tokens = preprocessor.getProcessedForm(document.getTitle());
            addTokensToIndex(tokens, document.getId());
        }
    }

    @Override
    public List<Result> search(String query) {
        //  todo implement
        return null;
    }


    void dumpIndex(String filePath)
    {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(inMemoryIndex);
            out.close();
            fileOut.close();
            logger.info("Index saved into: "+filePath);
        } catch (IOException exception) {
            logger.error("Problem with saving index");
            logger.debug("Index saving exception", exception);
        }
    }

    private void loadIndex(File serializedFile) {
        final Object object;
        try {
            final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile));
            object = objectInputStream.readObject();
            objectInputStream.close();
            inMemoryIndex = (Map<String, Map<String, Integer>>) object;
        } catch (Exception ex) {
            logger.error("Problem with loading index", ex);
            throw new RuntimeException(ex);
        }
    }
}
