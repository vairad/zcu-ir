package cz.zcu.kiv.nlp.ir.trec.gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IndexSettings {

    public ChoiceBox agressiveCombo;
    public ChoiceBox advancedCombo;
    public Button stopwords;
    public Button directory;
    public Button submit;
    public ProgressBar loadingIndex;


    boolean agressiveStemming;
    boolean advancedTokenizing;
    ArrayList<String> stopwordsToLoad;
    String pathToIndex;

    public void prepareIndex(ActionEvent actionEvent) {
            String response = (String) agressiveCombo.getValue();
            if(response != null && response.compareTo("Yes") == 0){
                agressiveStemming = true;
            }else{
                agressiveStemming = false;
            }
            response = (String) advancedCombo.getValue();
            if(response != null && response.compareTo("Yes") == 0){
                advancedTokenizing = true;
            }else{
                advancedTokenizing = false;
            }
       //     SearchWindow.prepareIndex(agressiveStemming,advancedTokenizing,getStopwords(),getPathToIndex());
    }

    public void chooseStopWords(ActionEvent actionEvent) {
        stopwordsToLoad = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open StopWord files");
        Stage stage = new Stage();
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            for (File file : list) {
                stopwordsToLoad.add(file.getPath());
            }
        }
    }

    String[] getStopwords(){
        if(stopwordsToLoad == null){
            stopwordsToLoad = new ArrayList<>();
        }
        String[] stockArr = new String[stopwordsToLoad.size()];
        stockArr = stopwordsToLoad.toArray(stockArr);
        return stockArr;
    }

    String getPathToIndex(){
        if(pathToIndex == null)
        {
            return "";
        }
        return pathToIndex;
    }

    public void chooseIndexDirectory(ActionEvent actionEvent) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Open StopWord files");
        Stage stage = new Stage();
        File file = fileChooser.showDialog(stage);
        pathToIndex = file.getPath()+"/index";
    }
}
