package cz.zcu.kiv.nlp.ir.trec.evaluation;

import cz.zcu.kiv.nlp.ir.trec.index.TokenProperties;

import java.util.List;
import java.util.Map;

/**
 * Třída sdružující metody pro vytváření instancé DokumentVektoru jednotlivých dokumentů a dotazů.
 * @author Radek Vais
 */
public class Evaluator {

    /**
     * Metoda vypočte metriku TF-IDF ze zadaných parametrů.
     *
     * @param termFreq      počet termů v dokumentu.
     * @param documentFreq  počet dokumentů obsahující term.
     * @param documentCount počet všech dokumentů-
     * @return hodnota metriky TF-IDF
     */
    static float countTfIdf(int termFreq, int documentFreq, int documentCount) {
        return termFreq == 0 ? (float) Math.log10((double) documentCount / (double) documentFreq)
                : (float) ((1.0 + Math.log10(termFreq)) * Math.log10((double) documentCount / (double) documentFreq));
    }

    /**
     * Metoda získá instanci DocumentVector odpovídající předanému dotazu.
     *
     * @param querylist     invertovaný index pro dotaz.
     * @param invertedIndex invertovný index vyhledávače.
     * @param documentCount počet dokumentů.
     * @return instance DocumentVector představující dotaz.
     */
    public static DocumentVector getQueryVector(Map<String, TokenProperties> querylist, Map<String, TokenProperties> invertedIndex, int documentCount) {
        DocumentVector dv = new DocumentVector(new float[invertedIndex.size()]);
        for (String queryToken : querylist.keySet()) {
            if (invertedIndex.containsKey(queryToken)) {
                dv.setAt(queryToken, Evaluator.countTfIdf(querylist.get(queryToken).getPostings().get("QUERY")
                        , invertedIndex.get(queryToken).getPostings().size()
                        , documentCount));
            }
        }
        return dv;
    }

    /**
     * Metoda získá DokumentVektor pro daný dokument.
     *
     * @param invertedIndex invertovný index vyhledávače.
     * @param docId         index dokumentu.
     * @param docTokens     tokeny obsažené v dokumentu.
     * @param documentCount počet všech dokumentů.
     * @return
     */
    public static DocumentVector getDocumentVector(Map<String, TokenProperties> invertedIndex, String docId, List<String> docTokens, int documentCount) {
        DocumentVector dv = new DocumentVector(new float[invertedIndex.size()]);
        if (docTokens == null) {
            return dv;
        }
        for (String token : docTokens) {
            Map<String, Integer> postings = invertedIndex.get(token).getPostings();
            if (postings.containsKey(docId)) {
                dv.setAt(token, Evaluator.countTfIdf(postings.get(docId), postings.size(), documentCount));
            }
        }
        return dv;
    }
}
