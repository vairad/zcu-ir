package cz.zcu.kiv.nlp.ir.trec.gui;


import cz.zcu.kiv.nlp.ir.trec.Index;
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

    SearchControler controller;

    private IndexSettings settingsWindow;

    public static Index index;

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

    private void loadIndex() {
        IPreprocessor preprocessor = new Preprocessor();

        String [] stopFiles = {"stop-cz-dia-1.txt",
                "stop-spec-chars.txt"};
        IDictionary stopWords = new FileDictionary(Arrays.asList(stopFiles));

        IStemmer stemmer = new CzechStemmerLight();
        ITokenizer tokenizer = new BasicTokenizer(stopWords);

        preprocessor.initialise(stemmer, tokenizer);
        index = new Index(preprocessor);

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
        if( false && new File("indexFile.inv").exists() && new File("indexFile.idx").exists()) {
            logger.info("Load saved index.");
            index = new Index("indexFile", preprocessor);
        }else{
            logger.info("Index documents");
            index.index(documents);
        }

        logger.info("Indexing done");
    }

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
