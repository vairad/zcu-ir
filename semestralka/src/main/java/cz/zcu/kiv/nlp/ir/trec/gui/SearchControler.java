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

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class SearchControler implements Initializable {

        @FXML
        public VBox results;
        @FXML
        public Button button;
        @FXML
        private TextField field;

        @FXML
        private Label query;

        List<Result> resultHits;

        @FXML
        private void handleAction(ActionEvent event) {
            String text = field.getText();
            query.setText(text);
            button.setDisable(true);
            Thread worker = new Thread(()->{
                resultHits = SearchWindow.index.search(text);
                Platform.runLater(this::publishResults);
            });
            worker.start();
        }

    private void publishResults() {
        results.getChildren().clear();
        for(Result result: resultHits){
            GuiResultImpl g = new GuiResultImpl(SearchWindow.index.getDocName(result.getDocumentID(), 20),
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

