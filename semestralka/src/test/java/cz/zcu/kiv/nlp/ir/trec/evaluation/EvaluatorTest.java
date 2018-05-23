package cz.zcu.kiv.nlp.ir.trec.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluatorTest {

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