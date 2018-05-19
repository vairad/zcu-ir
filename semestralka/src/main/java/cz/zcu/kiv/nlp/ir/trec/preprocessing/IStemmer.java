package cz.zcu.kiv.nlp.ir.trec.preprocessing;

/**
 * Rozhraní pro službu stemmingu.
 * @Author Radek Vais
 */
public interface IStemmer {

    /**
     * Metoda provede stemming stringu a výsledek vrátí jako návratovou hodnotu.
     * Zdrojové slovo není upraveno.
     * @param input vstpu pro stemming
     * @return stemmovné slovo
     */
    String stem(String input);
}
