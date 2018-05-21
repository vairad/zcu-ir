package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.data.ResultImpl;
import cz.zcu.kiv.nlp.ir.trec.evaluation.DocumentVector;
import cz.zcu.kiv.nlp.ir.trec.evaluation.Evaluator;
import cz.zcu.kiv.nlp.ir.trec.index.TokenProperties;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.IPreprocessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author Radek Vais
 */

public class Index implements Indexer, Searcher {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Index.class.getName());

    private IPreprocessor preprocessor;

    int documentCount;

    /** invertovaný index dokumentů
     *
     * token : TokenProperties
     */
    private Map<String, TokenProperties> invertedIndex;

    /** invertovaný index dotazu
     *
     * token : TokenProperties
     */
    private Map<String, TokenProperties> queryIndex;

    Set<String> connectedDocuments;

    private final String QUERY_DOCID = "QUERY";

    private Map<String, List<String>> documents;

    private static volatile Semaphore semaphore = new Semaphore(1);
    private static volatile Iterator<String> searchingIterator;
    private static volatile PriorityQueue<ResultImpl> resultsQueue;
    private static volatile DocumentVector queryVector;

    /**
     * Konstruktor načte invertovný index z paměti.
     *
     * Index se může ukládat do více souborů, proto se zadává tzv. filePatern. Pro cestu C:/User/xxx/index se může vytvořit
     * několik souborů např C:/User/xxx/index.inv a C:/User/xxx/index.idx.
     *
     * @param filePattern cesta k uložení indexu.
     * @param preprocessor Instance preprocessingu, která se použije pro dotazování.
     */
    Index(String filePattern, IPreprocessor preprocessor){
        this(preprocessor);
        loadIndex(filePattern);
    }

    /**
     * Konstruktor vytvoří instanci Indexu připravenou k indexaci dokumentů.
     *
     * @param preprocessor Instance preprocessingu, která se použije pro tokenizování dokumentů a dotazů.
     */
    Index(IPreprocessor preprocessor){
     //   logger.trace("Entry method");
        this.preprocessor = preprocessor;
        invertedIndex = new HashMap<>();
        queryIndex = new HashMap<>();
    }


    @Override
    public void index(List<Document> documents) {
        logger.debug("Start indexing");
        documentCount = documents.size();
        this.documents = new HashMap<>();
        int count = 0;
        for (Document document: documents) {
            List<String> tokens = preprocessor.getProcessedForm(document.getText());
            this.documents.put(document.getId(), tokens);
            addToInvertIndex(tokens, document.getId(), invertedIndex);
            count++;
            if (count % 5000 == 0){
                logger.info("Indexed "+count+" documents");
            }
        }
        logger.info("Indexed "+count+" documents");

        //add dictionary to vector class
        DocumentVector.addTerms(invertedIndex.keySet());

        logger.debug("Inverted index created.");
    }


    /**
     * Metoda zařadí token do invertovaného indexu, upraví kolekci výskytu dokumentu a zvýší počet výskytů
     *
     * @param tokens token pro zařzení.
     * @param docID id dokumentu, kde byl token nalezen.
     */
    private void addToInvertIndex(List<String> tokens, String docID, Map<String,TokenProperties> invertedIndex) {
        if (invertedIndex == null){
            invertedIndex = this.invertedIndex;
        }
        for (String token: tokens) {
            TokenProperties tokenProperties = invertedIndex.get(token);
            if(tokenProperties == null){ //when word is not in query
                tokenProperties = new TokenProperties(token);
                tokenProperties.addDocument(docID);
                invertedIndex.put(token, tokenProperties);
                continue;
            }
            tokenProperties.addDocument(docID);
        }
    }

    /**
     * Metoda zařadí token do invertovaného indexu, upraví kolekci výskytu dokumentu a zvýší počet výskytů
     *
     * @param tokens token pro zařzení.
     */
    private void addToQueryIndex(List<String> tokens) {
        addToInvertIndex(tokens, QUERY_DOCID, queryIndex);
    }


    @Override
    public List<Result> search(String query) {
        if (!query.contains("AND") && !query.contains("OR") && !query.contains("NOT")) {
            return searchOne(query);
        } else {
            //Map<String, Integer> foundedDocs = BooleanQuery.search(query, inMemoryInvIndex);
         // return Evaluator.evaluate( inMemoryIndex, foundedDocs, null);
            return null;
        }
    }


    /**
     * Metoda vyhledá v indexovaném indexu dokumenty podle jednoduchého dotazu.
     * @param query dotaz - "seznam hledaných slov"
     * @return kolekce dokumentů obsahujících alespoň jedno slovo z dotazu.
     */
    private List<Result> searchOne(String query) {
        logger.debug("Single search");
        connectedDocuments = new TreeSet<>();
        List<String> lookingFor = preprocessor.getProcessedForm(query);
        addToQueryIndex(lookingFor);
        //find apropriet doccuments
        for (String token :lookingFor) {
            connectedDocuments.addAll(connectedDocuments(token));
        }
        logger.debug("Founded "+connectedDocuments.size()+" related documents.");

       //get DocumentVector of query
        queryVector = Evaluator.getQueryVector(queryIndex,invertedIndex,documentCount);
        //rank documents against query by cosine distance
        resultsQueue = new PriorityQueue<>(1, (o1, o2) -> -Float.compare(o1.getScore(), o2.getScore()));

        searchingIterator = connectedDocuments.iterator();
        runParallelEvaluation();

        LinkedList<Result> results = new LinkedList<>();
        //prepare top 10 result
        for (int i = 1; i < 11; i++) {
            ResultImpl result = resultsQueue.poll();
            if(result == null){
                break;
            }
            result.setRank(i);
            results.addLast(result);
        }
        return results;
    }

    private void runParallelEvaluation() {

        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < 6 ; i++) {
            threads.add(new Thread(() -> {
                String docId = getDocumentId();
                while (docId != null) {
                    //logger.debug(docId);
                    DocumentVector document = Evaluator.getDocumentVector(invertedIndex,docId
                            , documents.get(docId)
                            , documentCount);
                    ResultImpl result = new ResultImpl();
                    (result).setDocumentID(docId);
                    result.setScore(document.cosineDistance(queryVector));
                    giveResult(result);
                    docId = getDocumentId();
                }
            }));
        }
        for (Thread t: threads) {
            t.start();
        }
        for (Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                logger.error("Thread problem", e);
            }
        }
    }

    synchronized String getDocumentId(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.error("Mutex problem", e);
        }

        String result = null;
        if(searchingIterator.hasNext()){
            result = searchingIterator.next();
        }
        semaphore.release();
        return result;
    }

    synchronized void giveResult(ResultImpl result) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.error("Mutex problem", e);
        }
        resultsQueue.add(result);
        semaphore.release();
    }

    private Set<String> connectedDocuments(String token){
        TokenProperties tokenProperties = invertedIndex.get(token);
        if(tokenProperties == null){
            return new TreeSet<>();
        }
        return tokenProperties.getPostings().keySet();
    }

    //================================================================================================================
    //==================================
    //=============== Saving methods
    //==================================


    /**
     * Metoda uloží struktury indexu na disk, dle zvolencý hparametrů.
     *
     * Může vzniknout více souborů odpovádajích patternu.
     * Pro pattern C:/User/xxx/index se může vytvořit
     *  několik souborů např C:/User/xxx/index.inv a C:/User/xxx/index.idx.
     *
     * @param filePattern cesta/pattern
     */
    void dumpIndex(String filePattern)
    {
        String invertedPath = filePattern+".inv";
        try {
            logger.debug("Saving "+invertedPath);
            FileOutputStream fileOut = new FileOutputStream(invertedPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(invertedIndex);
            out.close();
            fileOut.close();
            logger.info("Inverted index saved into: "+invertedPath);

        } catch (IOException exception) {
            logger.error("Problem with saving query");
            logger.debug("Index saving exception", exception);
        }
    }

    /**
     * Metoda nčítá všechny struktury indexu z disku, dle zvolencý hparametrů.
     *
     * Může požadovat existenci více souborů odpovádajích patternu.
     * Pro pattern C:/User/xxx/index se může vytvořit
     *  několik souborů např C:/User/xxx/index.inv a C:/User/xxx/index.idx.
     *
     * @param filePattern cesta/pattern
     */
    private void loadIndex(String filePattern) {
        final Object object1;
        try {
            logger.debug("Loading "+filePattern+".inv");
            File invertFile = new File(filePattern+".inv");

            final ObjectInputStream inverted = new ObjectInputStream(new FileInputStream(invertFile));
            object1 = inverted.readObject();
            inverted.close();
            invertedIndex = (Map<String, TokenProperties>) object1;

        } catch (Exception ex) {
            logger.error("Problem with loading index", ex);
            throw new RuntimeException(ex);
        }
    }
}
