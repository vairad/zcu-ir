package cz.zcu.kiv.nlp.ir.trec.evaluation;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class for testing object DocumentVector
 * @author Radek Vais
 */
class DocumentVectorTest {

    double[] vectorSaS = new double[]{3.06, 2.0, 1.30, 0};
    double[] vectorPaP = new double[]{2.76, 1.85, 0, 0};
    double[] vectorWH = new double[]{2.30, 2.04, 1.78, 2.58};

    DocumentVector SaS = new DocumentVector(vectorSaS);
    DocumentVector PaP = new DocumentVector(vectorPaP);
    DocumentVector WH = new DocumentVector(vectorWH);

    @org.junit.jupiter.api.Test
    void testVectorSize(){
        double[] vector = new double[]{1.0, 2.0, 4.0};

        double result = DocumentVector.vectorSize(vector);
        assertEquals(4.5825, result, 0.0001, "Bad vector size computation.");
    }

    @org.junit.jupiter.api.Test
    void testVectorSize2(){
        double[] vector = new double[]{2.0, 2.0, 2, 2};

        double result = DocumentVector.vectorSize(vector);
        assertEquals(4., result, 0.0001, "Bad vector size computation.");
    }

    @org.junit.jupiter.api.Test
    void testCosineDistance(){
        float result = SaS.cosineDistance(PaP);
        assertEquals(0.94, result, 0.01, "Bad cosine distance computation.");
    }

    @org.junit.jupiter.api.Test
    void testCosineDistance2(){
        float result = PaP.cosineDistance(SaS);
        assertEquals(0.94, result, 0.01, "Bad cosine distance computation.");
    }

    @org.junit.jupiter.api.Test
    void testCosineDistance3(){
        float result = SaS.cosineDistance(WH);
        assertEquals(0.79, result, 0.01, "Bad cosine distance computation.");
    }

    @org.junit.jupiter.api.Test
    void testCosineDistance4(){
        float result = WH.cosineDistance(PaP);
        assertEquals(0.69, result, 0.01, "Bad cosine distance computation.");
    }

    @org.junit.jupiter.api.Test
    void testCosineDistanceSelf(){
        float result = PaP.cosineDistance(PaP);
        assertEquals(1, result, 0.01, "Bad cosine distance computation.");
    }

    @org.junit.jupiter.api.Test
    void testCosineDistanceSelf1(){
        float result = WH.cosineDistance(WH);
        assertEquals(1, result, 0.01, "Bad cosine distance computation.");
    }


    @org.junit.jupiter.api.Test()
    void testSetAt(){
        DocumentVector doc = new DocumentVector(vectorWH);

        try {
            doc.setAt("A", 3.0);
            doc.setAt("B", 4.0);
            doc.setAt("C", 5.0);
            doc.setAt("D", 5.0);
            doc.setAt("E", 5.0);
        }catch (IndexOutOfBoundsException e){
            return;
        }
        Assertions.fail("Exception was not throwed");
    }

    @org.junit.jupiter.api.Test()
    void testSetAt1(){
        DocumentVector doc = new DocumentVector(vectorWH);

        doc.setAt("A", 3.0);
        doc.setAt("A", 4.0);
        doc.setAt("A", 5.0);
        doc.setAt("A", 5.0);
        doc.setAt("A", 5.0);

        assertEquals(5.0, doc.getTfidfValues()[0], 0.1, "Wrong Translate");
    }


}