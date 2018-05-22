package cz.zcu.kiv.nlp.ir.trec.gui;

import cz.zcu.kiv.nlp.ir.trec.data.Result;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Java FX kontrolér okna vyhledávání
 * Obsahuje
 *
 * @author Radek Vais
 */
public class SearchControler implements Initializable {

    /**
     * instance loggeru tridy
     */
    public static Logger logger = LogManager.getLogger(SearchControler.class.getName());

    @FXML
    public VBox results;
    @FXML
    public Button button;
    @FXML
    private TextField field;

    @FXML
    private Label query;

    List<Result> resultHits;

    /**
     * Metoda, která spustí vyhodnocení dotzu a zobrazí výsledky
     *
     * @param event
     */
    @FXML
    private void actionSearch(ActionEvent event) {
        String text = field.getText();
        logger.debug("Passed query: "+text);
        query.setText(text);
        button.setDisable(true);
        Thread worker = new Thread(() -> {
            resultHits = SearchWindow.index.search(text);
            Platform.runLater(this::publishResults);
        });
        worker.start();
    }

    private void publishResults() {
        logger.debug("Publishing results");
        results.getChildren().clear();
        for (Result result : resultHits) {
            GuiResultImpl g = new GuiResultImpl(SearchWindow.index.getDocName(result.getDocumentID(), 50),
                    result.getScore(),
                    result.getDocumentID());
            results.getChildren().add(new ResultControl(g));
        }
        button.setDisable(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}

