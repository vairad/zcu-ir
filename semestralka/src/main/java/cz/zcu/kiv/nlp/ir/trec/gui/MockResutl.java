package cz.zcu.kiv.nlp.ir.trec.gui;


/**
 * Testovací třída pro vyplnění GUI
 * @author Radek Vais
 */
public class MockResutl implements IGuiResult {
    @Override
    public String getDocumentName() {
        return "Název";
    }

    @Override
    public String getPresentQuery() {
        return "Ocekáváno";
    }

    @Override
    public String getMissingQuery() {
        return "Chybí";
    }

    @Override
    public String getPrewiev() {
        return "Obsah";
    }
}
