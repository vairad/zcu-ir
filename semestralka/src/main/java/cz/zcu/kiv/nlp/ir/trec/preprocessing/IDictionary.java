package cz.zcu.kiv.nlp.ir.trec.preprocessing;

/**
 * Rozhraní pro služby slovníku.
 */
public interface IDictionary {

    /**
     * Metoda ověří, zda je slovo součástí slovníku, či nikoliv.
     * @param word slovo (sousloví) k ověření ve sovníku
     * @return true/false uspěchu
     */
    boolean isPresent(String word);
}
