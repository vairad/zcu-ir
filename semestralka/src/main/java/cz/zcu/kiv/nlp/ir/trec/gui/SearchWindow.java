package cz.zcu.kiv.nlp.ir.trec.gui;


import cz.zcu.kiv.nlp.ir.trec.Index;
import cz.zcu.kiv.nlp.ir.trec.MainClass;
import cz.zcu.kiv.nlp.ir.trec.SerializedDataHelper;
import cz.zcu.kiv.nlp.ir.trec.TestTrecEval;
import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Topic;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearchWindow extends Application {

    public static void processGui(String[] args) {
        launch(args);
    }

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(SearchWindow.class.getName());

    /** java FX stage tohoto okna */
    private Stage stage;

    private SearchControler controller;

    private IndexSettings settingsWindow;

    public static Index index;

    /**
     * Halvní metoda spouštějící JavaFX aplikaci.
     * Připraví a zobrazí okno vyhledávání.
     * @param primaryStage stage
     */
    @Override
    public void start(Stage primaryStage) {
        logger.debug("Start");
        loadIndex();
        stage = primaryStage;
        Localization.loadResource(Locale.getDefault());
        loadView();
        stage.setTitle("Search window");
        stage.setMinHeight(Constants.MINH_SEARCH);
        stage.setMinWidth(Constants.MINW_SEARCH);

        stage.setWidth(Constants.PREFW_SEARCH);
        stage.setHeight(Constants.PREFH_SEARCH);
        stage.show();
        //
        // showSetting();

    }

    /**
     * Metoda spustí náhvání indexu.
     * A odkaz uloží do proměnné.
     */
    private void loadIndex() {
        IPreprocessor preprocessor = PreprocessorFactory.preparePreprocessor(false,false, MainClass.getStopFiles());
        index = new Index(preprocessor, MainClass.isSmall());

        List<Topic> topics = SerializedDataHelper.loadTopic(new File(TestTrecEval.OUTPUT_DIR + "/topicData.bin"));

        File serializedData = new File(TestTrecEval.OUTPUT_DIR + "/czechData.bin");

        List<Document> documents = new ArrayList<>();
        logger.info("load");
        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.loadDocument(serializedData);
            } else {
                logger.error("Cannot find " + serializedData);
            }
        } catch (Exception e) {
            logger.error("Error loading data", e);
            System.exit(55);
        }
        logger.info("Documents: " + documents.size());

        logger.info("Indexing");
        if(MainClass.isLoad()
                && new File(MainClass.getIndexPath()+".inv").exists()
                && new File(MainClass.getIndexPath()+".idx").exists())
        {
            logger.info("Load saved index.");
            index = new Index(MainClass.getIndexPath(), preprocessor, MainClass.isSmall());
        }else{
            logger.info("Index documents");
            index.index(documents);
            if(MainClass.isSave()){
                logger.info("Save index to disk");
                index.dumpIndex(MainClass.getIndexPath());
            }

        }

        logger.info("Indexing done");
    }

    /**
     * Metoda vyvolá okno pro nastavení indexování.
     */
    private void showSetting() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            ResourceBundle bundle = Localization.getBundle();
            fxmlLoader.setResources(bundle);

            root = fxmlLoader.load(SearchWindow.class.getResource("settingsWindow.fxml").openStream());

            settingsWindow = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Index settings");
            stage.setScene(new Scene(root, 300, 200));
            stage.show();
        }
        catch (IOException | NullPointerException e) {
            logger.error("Resource problem", e);
        }
    }

    /**
     * Meotda načte resource searchWindow.fxml pro okno.
     */
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
