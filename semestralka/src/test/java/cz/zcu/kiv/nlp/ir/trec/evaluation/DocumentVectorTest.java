package cz.zcu.kiv.nlp.ir.trec.evaluation;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class for testing object DocumentVector
 * @author Radek Vais
 */
class DocumentVectorTest {

    float[] vectorSaS = new float[]{(float) 3.06, (float) 2.0, (float) 1.30, 0};
    float[] vectorPaP = new float[]{(float) 2.76, (float) 1.85, 0, 0};
    float[] vectorWH = new float[]{(float) 2.30, (float) 2.04, (float) 1.78, (float) 2.58};

    DocumentVector SaS = new DocumentVector(vectorSaS);
    DocumentVector PaP = new DocumentVector(vectorPaP);
    DocumentVector WH = new DocumentVector(vectorWH);

    @org.junit.jupiter.api.Test
    void testVectorSize(){
        float[] vector = new float[]{(float) 1.0, (float) 2.0, (float) 4.0};

        double result = DocumentVector.vectorSize(vector);
        assertEquals(4.5825, result, 0.0001, "Bad vector size computation.");
    }

    @org.junit.jupiter.api.Test
    void testVectorSize2(){
        float[] vector = new float[]{(float) 2.0, (float) 2.0, 2, 2};

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
            doc.setAt("A", 3.0f);
            doc.setAt("B", 4.0f);
            doc.setAt("C", 5.0f);
            doc.setAt("D", 5.0f);
            doc.setAt("E", 5.0f);
        }catch (IndexOutOfBoundsException e){
            return;
        }
        Assertions.fail("Exception was not throwed");
    }

    @org.junit.jupiter.api.Test()
    void testSetAt1(){
        DocumentVector doc = new DocumentVector(vectorWH);

        doc.setAt("A", 3.0f);
        doc.setAt("A", 4.0f);
        doc.setAt("A", 5.0f);
        doc.setAt("A", 5.0f);
        doc.setAt("A", 5.0f);

        assertEquals(5.0, doc.getTfidfValues()[0], 0.1, "Wrong Translate");
    }


}