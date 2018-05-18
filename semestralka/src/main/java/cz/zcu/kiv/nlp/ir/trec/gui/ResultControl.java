package cz.zcu.kiv.nlp.ir.trec.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ResultControl extends VBox {
    @FXML
    private Label documentName;
    @FXML
    private Label presentQuery;
    @FXML
    private Label preview;

    public ResultControl(IGuiResult result) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resultComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            this.documentName.setText(result.getDocumentName());
            this.presentQuery.setText(result.getPresentQuery());
            this.preview.setText(result.getPrewiev());

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setDocumentName(String documentName) {
        this.documentName.setText(documentName);
    }

    public void setPresentQuery(String presentQuery) {
        this.presentQuery.setText(presentQuery);
    }

    public void setPreview(String preview) {
        this.preview.setText(preview);
    }
}
