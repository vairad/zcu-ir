package cz.zcu.kiv.nlp.ir.trec.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchControler implements Initializable {

        @FXML
        public VBox results;
        @FXML
        public Button button;
        @FXML
        private TextField field;

        @FXML
        private Label query;

        @FXML
        private void handleAction(ActionEvent event) {
            String text = field.getText();
            query.setText(text);
            for(int i = 0; i < 10; i++){
                    results.getChildren().add(new ResultControl(new MockResutl()));
            }
        }

        @Override
        public void initialize(URL url, ResourceBundle rb) {
        }
}

