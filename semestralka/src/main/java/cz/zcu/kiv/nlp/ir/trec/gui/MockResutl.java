package cz.zcu.kiv.nlp.ir.trec.gui;

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
