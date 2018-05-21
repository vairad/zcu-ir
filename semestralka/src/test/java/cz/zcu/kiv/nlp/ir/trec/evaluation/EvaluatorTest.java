package cz.zcu.kiv.nlp.ir.trec.evaluation;

import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.evaluation.Evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @org.junit.jupiter.api.Test
    void testEvaluate(){
        Map<String, Integer> foundedDocs = new HashMap<>();
        foundedDocs.put("klic1", 35);
        foundedDocs.put("klic2", 34);
        foundedDocs.put("klic3", 43);

        List<Result> results = Evaluator.evaluate(10, foundedDocs);

        assertEquals("klic3", results.get(0).getDocumentID(), "Spatne serazeni elementu");
        assertEquals("klic1", results.get(1).getDocumentID(), "Spatne serazeni elementu");
        assertEquals("klic2", results.get(2).getDocumentID(), "Spatne serazeni elementu");
    }

    @org.junit.jupiter.api.Test
    void testTfIdfCount(){
        float tfidf = Evaluator.countTfIdf(10,10, 100 );
        assertEquals( 2 , tfidf, "TfIdf count failed");
    }

    @org.junit.jupiter.api.Test
    void testTfIdfCount2(){
        float tfidf = Evaluator.countTfIdf(0,10, 100 );
        assertEquals( 1 , tfidf, "TfIdf count failed");
    }


}