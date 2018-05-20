package cz.zcu.kiv.nlp.ir.trec;

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
}
