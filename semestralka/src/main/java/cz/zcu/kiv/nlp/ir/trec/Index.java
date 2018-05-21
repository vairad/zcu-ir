package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.index.TokenProperties;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.IPreprocessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
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

    int documentCount;

    /** invertovaný index dokumentů
     *
     * token : TokenProperties
     */
    private Map<String, TokenProperties> invertedIndex;

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
    }


    @Override
    public void index(List<Document> documents) {
        logger.debug("Start indexing");
        documentCount = documents.size();
        int count = 0;
        for (Document document: documents) {
            List<String> tokens = preprocessor.getProcessedForm(document.getText());
            addToInvertIndex(tokens, document.getId());
            count++;
            if (count % 5000 == 0){
                logger.info("Indexed "+count+" documents");
            }
        }
        logger.info("Indexed "+count+" documents");
        logger.debug("Inverted index created.");
    }


    /**
     * Metoda zařadí token do invertovaného indexu, upraví kolekci výskytu dokumentu a zvýší počet výskytů
     *
     * @param tokens token pro zařzení.
     * @param docID id dokumentu, kde byl token nalezen.
     */
    private void addToInvertIndex(List<String> tokens, String docID) {
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


    @Override
    public List<Result> search(String query) {
        if (!query.contains("AND") && !query.contains("OR") && !query.contains("NOT")) {
            return searchOne(query);
        } else {
       //     Map<String, Integer> foundedDocs = BooleanQuery.search(query, inMemoryInvIndex);
      //     return Evaluator.evaluate( inMemoryIndex, foundedDocs, null);
            return null;
        }
    }


    /**
     * Metoda vyhledá v indexovaném indexu dokumenty podle jednoduchého dotazu.
     * @param query dotaz - "seznam hledaných slov"
     * @return kolekce dokumentů obsahujících alespoň jedno slovo z dotazu.
     */
    private List<Result> searchOne(String query) {
     //   logger.debug("Entry method");
        List<Map<Integer ,Integer>> results = new ArrayList<>();
        List<String> lookingFor = preprocessor.getProcessedForm(query);
        for (String part : lookingFor) {
          //  if(inMemoryInvIndex.containsKey(part))
         //   results.add(inMemoryInvIndex.get(part));
        }
        if(results.size() == 0)
        {
            logger.debug("No documents found");
            return null;
        }
      //  results.sort(Comparator.comparingInt(Map::size));
        Map<Integer, Integer> foundedDocs = new HashMap<>(10*results.size()); //predpokladame 10 záznamů pro hledné slovo
        for(Map<Integer, Integer> result:results){
            for(int key: result.keySet()){
                if(foundedDocs.containsKey(key)){
                    Integer docFreq = foundedDocs.get(key);
                    docFreq += result.get(key);
                    foundedDocs.replace(key, docFreq);
                }else {
                    foundedDocs.put(key, result.get(key));
                }
            }
        }
        //TODO REPAIR
      //  return Evaluator.evaluate(8140, foundedDocs);
        return null;
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
