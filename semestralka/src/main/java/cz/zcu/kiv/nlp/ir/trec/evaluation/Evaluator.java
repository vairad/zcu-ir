package cz.zcu.kiv.nlp.ir.trec.evaluation;

import cz.zcu.kiv.nlp.ir.trec.index.TokenProperties;
import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.data.ResultImpl;

import java.util.*;

public class Evaluator {
    public static List<Result> evaluate(int documentCount, Map<String, Integer> foundedDocs) {
        PriorityQueue<ResultImpl> results = new PriorityQueue<>(documentCount, Comparator.comparingDouble(ResultImpl::getScore).reversed());

        // foudend docs obsahují termFrequency of query
        for(String key : foundedDocs.keySet()){
            ResultImpl result = new ResultImpl();
            result.setDocumentID(key);
            result.setTermFreq(foundedDocs.get(key));
            result.setScore(countTfIdf(result.getTermFreq(), foundedDocs.size(), documentCount));
            results.add(result);
        }

        List<Result> ranked = new ArrayList<>(10);
        for (int i = 1; i < 11; i++) {
            if(!results.isEmpty()) {
                ResultImpl r = results.poll();
                r.setRank(i);
                ranked.add(r);
            }
        }
        return ranked;
    }

    static float countTfIdf(int termFreq, int documentFreq, int documentCount){
        return termFreq == 0 ? (float) Math.log10((double) documentCount / (double) documentFreq)
                : (float) ((1.0 + Math.log10(termFreq)) * Math.log10((double) documentCount / (double) documentFreq));
    }

    public static Map<String, DocumentVector> evaluateDocumentSet(Map<String, TokenProperties> invertedIndex
                                                        , Set<String> docIds, int allDocumentsCount) {
        Map<String, DocumentVector> documentIndex = new HashMap<>();
        for (String docID: docIds) {
            documentIndex.put(docID, new DocumentVector(new float[invertedIndex.size()]));
        }
        
        for (Iterator<Map.Entry<String, TokenProperties>> it = invertedIndex.entrySet().iterator(); it.hasNext(); ) {
            TokenProperties token = (TokenProperties) it.next();
            Map<String, Integer> postings = token.getPostings();
            for (String docId: postings.keySet()) {
                DocumentVector dv = documentIndex.get(docId);
                dv.setAt(token.getToken(), Evaluator.countTfIdf(postings.get(docId), postings.size(), allDocumentsCount));
            }
        }
        return documentIndex;
    }

    public static DocumentVector getQueryVector(Map<String, TokenProperties> querylist, Map<String, TokenProperties> invertedIndex, int documentCount) {
        DocumentVector dv = new DocumentVector(new float[invertedIndex.size()]);
        for (String queryToken: querylist.keySet()) {
            if(invertedIndex.containsKey(queryToken)){
                dv.setAt(queryToken, Evaluator.countTfIdf(querylist.get(queryToken).getPostings().get("QUERY")
                        , invertedIndex.get(queryToken).getPostings().size()
                        ,documentCount));
            }
        }
        return dv;
    }

    public static DocumentVector getDocumentVector(Map<String, TokenProperties> invertedIndex, String docId, List<String> docTokens, int documentCount) {
        DocumentVector dv = new DocumentVector(new float[invertedIndex.size()]);
        for (String token : docTokens) {
            Map<String, Integer> postings = invertedIndex.get(token).getPostings();
            if (postings.containsKey(docId)) {
                dv.setAt(token, Evaluator.countTfIdf(postings.get(docId), postings.size(), documentCount));
            }
        }
        return dv;
    }
}
