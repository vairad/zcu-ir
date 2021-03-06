package cz.zcu.kiv.nlp.ir.trec.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


/**
 * JavFX kontrolér a zároveň komponenta pro zobrazení výsledků v okně.
 *
 * @author Radek Vais
 */
public class ResultControl extends VBox {
    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(ResultControl.class.getName());

    @FXML
    private Label documentName;
    @FXML
    private Label presentQuery;
    @FXML
    private Label missingQuery;
    @FXML
    private Label preview;

    /**
     * Konstruktor načte rozvržení resultComponent.fxml nastyluje ho dle formatstyle.css.
     * @param result Instance dat k zobrazení v komponentě.
     */
    public ResultControl(IGuiResult result) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resultComponent.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);

            String stylesheet = getClass().getResource("formstyle.css").toExternalForm();
            this.getStylesheets().add(stylesheet);

            fxmlLoader.load();
        } catch (IOException ex) {
            logger.error("Reading exception", ex);
            throw new RuntimeException(ex);
        } catch (NullPointerException e){
            logger.error("Configuration not found", e);
            throw new RuntimeException(e);
        }

        this.documentName.setText(result.getDocumentName());
        this.presentQuery.setText(result.getPresentQuery());
        this.missingQuery.setText(result.getMissingQuery());
   //     this.preview.setText(result.getPrewiev());
    }

    /**
     * Metoda pro adHoc úpravy pole komponenty
     * @param documentName název dokumentu
     */
    public void setDocumentName(String documentName) {
        this.documentName.setText(documentName);
    }


    /**
     * Metoda pro adHoc úpravy pole komponenty
     * @param presentQuery podndpis 1
     */
    public void setPresentQuery(String presentQuery) {
        this.presentQuery.setText(presentQuery);
    }


    /**
     * Metoda pro adHoc úpravy pole komponenty
     * @param preview podnadpis přehled dokumentu
     */
    public void setPreview(String preview) {
        this.preview.setText(preview);
    }
}
