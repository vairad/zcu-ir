package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.data.ResultImpl;
import cz.zcu.kiv.nlp.ir.trec.evaluation.DocumentVector;
import cz.zcu.kiv.nlp.ir.trec.evaluation.Evaluator;
import cz.zcu.kiv.nlp.ir.trec.index.TokenProperties;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.IPreprocessor;
import cz.zcu.kiv.nlp.ir.trec.query.BooleanQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Implementace fyhledávácího indexu.
 * @author Radek Vais
 */

public class Index implements Indexer, Searcher {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Index.class.getName());

    /**Vlajka, zda má být použit malý index.     */
    private final boolean small;

    private IPreprocessor preprocessor;

    private int documentCount;

    /**
     * invertovaný index dokumentů
     * token : TokenProperties
     */
    private Map<String, TokenProperties> invertedIndex;

    /**
     * invertovaný index dotazu
     * token : TokenProperties
     */
    private Map<String, TokenProperties> queryIndex;


    /**
     * Seznam dotazů obsahující slovo z pozitivní části dotazu
     */
    private Set<String> connectedDocuments;

    /**
     * ID dokumentu dotazu
     */
    private final String QUERY_DOCID = "QUERY";

    /**
     * In order index výskytu slov v dokumentu
     *  dokument : slovo - slovo - slovo
     */
    private Map<String, List<String>> documents;

    /**
     * Raw podoba načených dokumentů.
     */
    private List<Document> sources;

    //synchronizace vláken ==============================================================
    /**
     * Ošetření kritické sekce přidělování dokumentů a ukládání výsledků
     */
    private volatile Semaphore semaphore = new Semaphore(1);

    /**
     * Seznam dokumentů pro přidělení vyhledávači.
     */
    private volatile Iterator<String> searchingIterator;
    /**
     * Fronta pro ukládání výsledků
     */
    private volatile PriorityQueue<ResultImpl> resultsQueue;

    /**
     * Vypočtený vektor tf-idf pro dokumenty v selectu
     */
    private volatile DocumentVector queryVector;

    /**
     * Konstruktor načte invertovný index z paměti.
     *
     * Index se může ukládat do více souborů, proto se zadává tzv. filePatern. Pro cestu C:/User/xxx/index se může vytvořit
     * několik souborů např C:/User/xxx/index.inv a C:/User/xxx/index.idx.
     *
     * @param filePattern cesta k uložení indexu.
     * @param preprocessor Instance preprocessingu, která se použije pro dotazování.
     * @param small vljka, zda má být použit kompletní či malý index (small = true - malý index z názvů)
     */
    public Index(String filePattern, IPreprocessor preprocessor, boolean small){
        this(preprocessor, small);
        loadIndex(filePattern);
    }

    /**
     * Konstruktor vytvoří instanci Indexu připravenou k indexaci dokumentů.
     *
     * @param preprocessor Instance preprocessingu, která se použije pro tokenizování dokumentů a dotazů.
     * @param small vlajka použití malého indexu.
     */
    public Index(IPreprocessor preprocessor, boolean small){
     //   logger.trace("Entry method");
        this.preprocessor = preprocessor;
        invertedIndex = new HashMap<>();
        queryIndex = new HashMap<>();
        this.small = small;
    }


    @Override
    public void index(List<Document> documents) {
        logger.debug("Start indexing");
        documentCount = documents.size();
        this.documents = new HashMap<>();
        this.sources = documents;
        int count = 0;
        for (Document document: documents) {
            List<String> tokens = preprocessor.getProcessedForm(document.getTitle());
             if(!small){
                 tokens.addAll(preprocessor.getProcessedForm(document.getText()));
             }
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
     * @param invertedIndex invertovaný index vyhledávače.
     */
    private void addToInvertIndex(List<String> tokens, String docID, Map<String,TokenProperties> invertedIndex) {
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
        prepareSearch();
        if (!query.contains("AND") && !query.contains("OR") && !query.contains("NOT")) {
            return searchOne(query);
        } else {
            return searchBoolean(query);
        }
    }

    /**
     *  medtoda připraví objekty pro ukládání výsledků dotazů
     *
     */
    private void prepareSearch() {
        queryIndex = new HashMap<>();
        connectedDocuments = new HashSet<>();
        resultsQueue = new PriorityQueue<>(1, (o1, o2) -> -Float.compare(o1.getScore(), o2.getScore()));
    }

    /** Metoda slouží k vyhledání dokumentů dle boolean query a následnému vyhodnocení, který je nejvhodnější
     *
     * @param query boolean query
     * @return top 10 nalezených dokumentů
     */
    private List<Result> searchBoolean(String query) {
        connectedDocuments = BooleanQuery.searchBoolean(query,this);
        logger.debug("Founded "+connectedDocuments.size()+" related documents.");
        //get DocumentVector of query
        queryVector = Evaluator.getQueryVector(queryIndex,invertedIndex,documentCount);
        searchingIterator = connectedDocuments.iterator();

        runParallelEvaluation();

        return topXDocs(10);
    }

    /**
     * Metoda vrací top X nalezených dokumentů v seřazeném seznamu
     * @param count počet dokumentů ke vrácení
     * @return TOP x výsledků
     */
    private List<Result> topXDocs(int count) {
        LinkedList<Result> results = new LinkedList<>();
        //prepare top count result
        for (int i = 1; i < count+1; i++) {
            ResultImpl result = resultsQueue.poll();
            if(result == null){
                break;
            }
            result.setRank(i);
            results.addLast(result);
        }
        return results;
    }

    /**
     * Metoda vyhledá v indexovaném indexu dokumenty podle jednoduchého dotazu.
     * @param query dotaz - "seznam hledaných slov"
     * @return kolekce dokumentů obsahujících alespoň jedno slovo z dotazu.
     */
    private List<Result> searchOne(String query) {
        logger.debug("Single search");

        connectedDocuments = allConnectedDocuments(preprocessor.getProcessedForm(query));
        logger.debug("Founded "+connectedDocuments.size()+" related documents.");

       //get DocumentVector of query
        queryVector = Evaluator.getQueryVector(queryIndex,invertedIndex,documentCount);
        //rank documents against query by cosine distance

        searchingIterator = connectedDocuments.iterator();

        runParallelEvaluation();

        return topXDocs(MainClass.getDocCount());
    }

    /**
     * Metoda slouží ke spuštění parlelního výpočtu cosinové podobnosti nalezených dokumentů s query.
     */
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

    /**
     * Meotoda přidělující seznam dokumentů k pralelnímu propočítání.
     * @return docId k výpočtu podobnosti.
     */
    synchronized private String getDocumentId(){
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

    /**
     * Meotda sloužící k paralelnímu uložení výsledků.
     * @param result Výsledek k uložení.
     */
    synchronized private void giveResult(ResultImpl result) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.error("Mutex problem", e);
        }
        if(result != null) {
            resultsQueue.add(result);
        }
        semaphore.release();
    }

    /**
     * Metoda slouží k nalezení všech dokumentů obsahující některé ze slov z dotazu
     * @param tokens seznam všech tokenů dotazu
     * @return množina docID
     */
    public Set<String> allConnectedDocuments(List<String> tokens){
        Set<String> connectedDocuments = new TreeSet<>();
        addToQueryIndex(tokens);
        //find apropriet doccuments
        for (String token : tokens) {
            connectedDocuments.addAll(connectedDocuments(token));
        }
        return connectedDocuments;
    }

    /**
     * Metoda vracející seznm docID výskytů pro vybraný token.
     * @param token token z dotazu
     * @return seznam docId, kde se token vyskytuje.
     */
    private Set<String> connectedDocuments(String token){
        TokenProperties tokenProperties = invertedIndex.get(token);
        if(tokenProperties == null){
            return new TreeSet<>();
        }
        return tokenProperties.getPostings().keySet();
    }

    /**
     * Metoda vracející seznsm docID bez vybraného tokenu.
     * @param token token z dotazu
     * @return seznam docId, kde se token NEvyskytuje.
     */
    private Set<String> notConnectedDocuments(String token){
        TokenProperties tokenProperties = invertedIndex.get(token);
        if(tokenProperties == null){
            return new TreeSet<>();
        }
        Set<String> allKeys = new TreeSet<>(documents.keySet());
        allKeys.removeAll(tokenProperties.getPostings().keySet());
        return allKeys;
    }

    /**
     * Method asplit Single query to single tokens
     * @param singleQuery query string
     * @return list of preprocessed tokens
     */
    public List<String> tokenQuery(String singleQuery) {
        return preprocessor.getProcessedForm(singleQuery);
    }


    /**
     *  Metoda slouží k nalezení všech dokumentů NEobsahující některé ze slov z dotazu
     * @param tokens seznam tokenů
     * @return množina dokumentů, neobsahující slova
     */
    public Set<String> allNotConnectedDocuments(List<String> tokens) {
        Set<String> notConnectedDocuments = new TreeSet<>();

        //find apropriet NOTdoccuments
        for (String token : tokens) {
            notConnectedDocuments.addAll(notConnectedDocuments(token));
        }
        return notConnectedDocuments;
    }


    /**
     * Metoda vrací data originálních dokumentů
     * @param docId index dokumentu
     * @param len délka úravku (0 = celý obsah)
     * @return Titulek
     */
    public String getDocName(String docId, int len){
        for (Document doc: sources) {
            if(doc.getId().compareTo(docId)==0){
                String text = doc.getTitle();
                len = len > 0 && len < text.length() ? len : text.length() ;
                return text.substring(0,len);
            }
        }
        return  "**Undefined document**";
    }

    /**
     * Metoda vraci data originách dokumentů
     * @param docId index dokumentu
     * @param len délka úryvku (0 = celý obsah)
     * @return prvních len znaků
     */
    public String getDocPre(String docId, int len){
        for (Document doc: sources) {
            if(doc.getId().compareTo(docId)==0){
                String text = doc.getText();
                len = len > 0 && len < text.length() ? len : text.length() ;
                return text.substring(0,len);
            }
        }
        return  "**Undefine document**";
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
    public void dumpIndex(String filePattern)
    {
        String invertedPath = filePattern+".inv";
        String indexPath = filePattern+".idx";
        try {
            logger.debug("Saving "+invertedPath);
            FileOutputStream fileOut = new FileOutputStream(invertedPath);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream((fileOut)));
            out.writeObject(invertedIndex);
            out.close();
            fileOut.close();
            logger.info("Inverted index saved into: "+invertedPath);

            logger.debug("Saving "+indexPath);
            fileOut = new FileOutputStream(indexPath);
            out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
            out.writeObject(documents);
            out.close();
            fileOut.close();
            logger.info("Document index saved into: "+indexPath);

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
        final Object object1, object2;
        try {
            logger.debug("Loading "+filePattern+".inv");
            File invertFile = new File(filePattern+".inv");

            final ObjectInputStream inverted = new ObjectInputStream(new BufferedInputStream(new FileInputStream(invertFile)));
            object1 = inverted.readObject();
            inverted.close();
            invertedIndex = (Map<String, TokenProperties>) object1;

            logger.debug("Loading "+filePattern+".idx");
            File indexFile = new File(filePattern+".idx");
            final ObjectInputStream index = new ObjectInputStream(new BufferedInputStream(new FileInputStream(indexFile)));
            object2 = index.readObject();
            index.close();
            documents = (Map<String, List<String>>) object2;

        } catch (Exception ex) {
            logger.error("Problem with loading index", ex);
            throw new RuntimeException(ex);
        }
    }
}
