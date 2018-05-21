package cz.zcu.kiv.nlp.ir.trec.gui;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class SearchWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static {
        System.setProperty("log4j.configurationFile", "log-conf.xml");
    }

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(SearchWindow.class.getName());

    /** java FX stage tohoto okna */
    private Stage stage;

    private SearchControler controller;

    @Override
    public void start(Stage primaryStage) {
        logger.debug("Start");
        stage = primaryStage;
        Localization.loadResource(Locale.getDefault());
        loadView();
        stage.setMinHeight(Constants.MINH_SEARCH);
        stage.setMinWidth(Constants.MINW_SEARCH);

        stage.setWidth(Constants.PREFW_SEARCH);
        stage.setHeight(Constants.PREFH_SEARCH);
        stage.show();
    }

    private void loadView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            ResourceBundle bundle = Localization.getBundle();
            fxmlLoader.setResources(bundle);

            Parent root = fxmlLoader.load(SearchWindow.class.getResource("searchWindow.fxml").openStream());

            controller = fxmlLoader.getController();

            Scene scene = new Scene(root);

            stage.setScene(scene);
        } catch (IOException ex) {
            logger.error("Set up SearchWindow UI fails", ex);
        } catch (NullPointerException e){
            logger.error("Null pointer fail", e);
        }
    }

}
