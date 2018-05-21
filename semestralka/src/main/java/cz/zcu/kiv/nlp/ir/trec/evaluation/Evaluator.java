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

    public static Map<String, DocumentVector> evaluateIndex(Map<String, TokenProperties> invertedIndex, List<Document> documents) {
        Map<String, DocumentVector> documentIndex = new HashMap<>();
        for (Document document: documents) {
            documentIndex.put(document.getId(), new DocumentVector(new float[invertedIndex.size()]));
        }
        
        for (Iterator<Map.Entry<String, TokenProperties>> it = invertedIndex.entrySet().iterator(); it.hasNext(); ) {
            TokenProperties token = (TokenProperties) it.next();
            Map<String, Integer> postings = token.getPostings();
            for (String docId: postings.keySet()) {
                DocumentVector dv = documentIndex.get(docId);
                dv.setAt(token.getToken(), Evaluator.countTfIdf(postings.get(docId), postings.size(), documents.size()));
            }
        }
        return documentIndex;
    }
}
