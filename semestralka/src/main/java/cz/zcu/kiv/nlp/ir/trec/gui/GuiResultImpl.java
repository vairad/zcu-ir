package cz.zcu.kiv.nlp.ir.trec.gui;

public class GuiResultImpl implements IGuiResult {

    String name;
    String score;
    String docID;

    GuiResultImpl (String name, float score, String docID){
        this.name = name;
        this.score = Float.toString(score);
        this.docID = docID;
    }

    @Override
    public String getDocumentName() {
        return name;
    }

    @Override
    public String getPresentQuery() {
        return score;
    }

    @Override
    public String getMissingQuery() {
        return docID;
    }

    @Override
    public String getPrewiev() {
        return "";
    }
}
