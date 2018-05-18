package cz.zcu.kiv.nlp.ir.trec.gui;

public class MockResutl implements IGuiResult {
    @Override
    public String getDocumentName() {
        return "Název";
    }

    @Override
    public String getPresentQuery() {
        return "Ano, ne, možná";
    }

    @Override
    public String getPrewiev() {
        return "Asi nějaký cool dokument";
    }
}
