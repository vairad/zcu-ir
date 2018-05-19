package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author tigi
 */

public class Index implements Indexer, Searcher {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Index.class.getName());


    @Override
    public void index(List<Document> documents) {
        //  todo implement
    }

    @Override
    public List<Result> search(String query) {
        //  todo implement
        return null;
    }
}
