package cz.zcu.kiv.nlp.ir.trec.gui;

/**
 * Rozhraní pro objekty zobrazitelné v GUI
 */
public interface IGuiResult {

    /**
     * Hodnota názvu dokumentu
     * @return string název
     */
    String getDocumentName();

    /**
     * Hodnota prvního podnadpisu. Ideální, pro slova z dotazu obsažená v dokumentu.
     * @return string
     */
    String getPresentQuery();

    /**
     * Hodnota druhého podnadpisu. Ideální, pro slova z dotazu NEobsažená v dokumentu.
     * @return string
     */
    String getMissingQuery();

    /**
     * Hodnota prvního preview.
     * @return string
     */
    String getPrewiev();

}
