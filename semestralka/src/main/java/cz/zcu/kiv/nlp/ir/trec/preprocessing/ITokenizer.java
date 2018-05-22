package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import java.util.List;

/**
 * Rozhraní pro libovolný tokenizátor
 * @author Radek Vais
 */
public interface ITokenizer {
    /**
     * Metoda vrací seznam nalezených tokenů v předané větě
     * @param sentence věta obsahující tokeny v základním tvaru
     * @return seznam tokenů v (upraveném tvaru)
     */
    List<String> getTokens(String sentence);
}
