package cz.zcu.kiv.nlp.vs;

import lombok.Getter;

import java.io.File;
import java.util.List;

public class DataLoader {

    String dataPath = "storage/hodnoceniLekaru/";
    String dataFile = "hodnoceniLekaru_2018-03-21_08_46_286Records.ser";

    @Getter private List<Record> documents;

    public void loadFile(){
        File data = new File(dataPath + dataFile);
        documents = Record.LoadRecordCollection(data);
    }
}