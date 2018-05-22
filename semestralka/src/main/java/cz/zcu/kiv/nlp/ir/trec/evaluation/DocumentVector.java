package cz.zcu.kiv.nlp.ir.trec.evaluation;

import cz.zcu.kiv.nlp.ir.trec.Index;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Třída představuje vektor dokumentu získaný výpočtem jiného modulu modulem evaluace.
 * Poskytuje metodu pro porovnání dvou vektorů.
 * Implementace uchovává sdílený slovnék pro překlad termů na id dokumentu.
 * @author Radek Vais
 */
public class DocumentVector implements Serializable {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(Index.class.getName());

    /** hodnoty vektoru dokumentu */
    private float[] tfidfValues;

    /** další přidělované ID -- staticky pro všechny instnce stejné */
    private static int nextID = 0;

    /** mapa termů na ID do pole -- staticky pro všehny instance stejné */
    private static Map<String, Integer> termToId = new HashMap<>();


    /**
     * Konstruktor třídy připraví instanci a nastaví pole pro jednotlivé hodnoty.
     * @param tfidfValues pole hodnot, musí mít velikost počtu termů.
     */
    DocumentVector(float[] tfidfValues) {
        this.tfidfValues = tfidfValues;
    }

    /**
     * Vypočte cosinovou podobnost s předanou instancí.
     * @param other DokumentVector k porovnání podobnosti
     * @return 0 .. 1 kde 1 je identický vektor.
     */
    public float cosineDistance(DocumentVector other) {
        float[] otherTfIdf = other.getTfidfValues();

        if(otherTfIdf.length != this.tfidfValues.length){
            logger.error("Vectors have different legth.");
            throw new IllegalArgumentException("Cant compute scalar product");
        }
        double scalarProduct = 0.0;
        for (int i = 0; i< tfidfValues.length; ++i){
            scalarProduct += tfidfValues[i] * otherTfIdf[i];
        }

        double size = vectorSize(tfidfValues) * vectorSize(otherTfIdf);
        float distance = (float)(scalarProduct/size);
        return Float.isNaN(distance)? 0 : distance ;
    }

    /**
     * Metoda nastaví do sloupce odpovdajícímu indexu term hodnotu value.
     * Pokud neexistuje záznam ve slovníku je použito první dostupné ID.
     * @param term term pro který zdáváme hodnotu.
     * @param value hodnota k zápisu.
     */
    void setAt(String term, float value){
        Integer index = termToId.get(term);
        if(index == null){
            index = nextID++;
            termToId.put(term, index);
        }
        tfidfValues[index] = value;
    }

    public static void addTerms(Set<String> terms){
        for (String term: terms) {
            Integer index = termToId.get(term);
            if(index == null){
                index = nextID++;
                termToId.put(term, index);
            }
        }
    }

    /**
     * Vrací vektor hodnot.
     * @return vektor dokumentu.
     */
    public float[] getTfidfValues() {
        return tfidfValues;
    }

    /**
     * Obecná metoda pro výpočet normy vektoru
     * sqrt(sum(v[i]^2))
     * @param vector vektor pro výpočet normy
     * @return norma vektoru
     */
    static double vectorSize(float[] vector){
        double size = 0.0;
        for (double element : vector) {
            size += element * element;
        }
        size = Math.sqrt(size);
        return size;
    }
}
