package cz.zcu.kiv.nlp.vs;

import cz.zcu.kiv.nlp.ir.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataLoader {

    private static String dataPath = "storage/hodnoceniLekaru/";
    private static String dataFile = "hodnoceniLekaru_2018-02-21_18_32_264Records.ser";

    private static String outputDir = "my_data_prep_stemm.txt";

    private static List<Record> documents;

    private static void loadFile(){
        File data = new File(dataPath + dataFile);
        documents = Record.LoadRecordCollection(data);
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = null;

        writer = new BufferedWriter(new FileWriter(new File(outputDir), false));

        loadFile();

        long counter = 0;

        StopWordManager stop = new StopWordManager(true);

        //Preprocessing preprocess = new BasicPreprocessing(null, null, stop.getStopWords(), true, false, true);

       // Preprocessing preprocess = new BasicPreprocessing(null, null, null, false, false, false);
        Preprocessing preprocess = new BasicPreprocessing(new CzechStemmerLight(), null, stop.getStopWords(), true, false, true);


        Tokenizer tokenizer = new AdvancedTokenizer();

        for (Record r : documents) {
            String name = preprocess.getProcessedForm(r.getName());
            writer.write(++counter + "\t" +"X"+ "\t");
            for (String n: tokenizer.tokenize(name)) {
                writer.write(n+" ");
            }
            writer.newLine();

            writer.write(++counter + "\t" +"X"+ "\t");
            for (String detail: r.getDetails())
            {
                String deta = preprocess.getProcessedForm(detail);
                for (String n: tokenizer.tokenize(deta)) {
                    writer.write(n+" ");
                }
            }
            writer.newLine();

            for (Post post: r.getPosts()) {
                writer.write(++counter + "\t" + "X" + "\t");
                String p = preprocess.getProcessedForm(post.getContent());
                for (String n : tokenizer.tokenize(p)) {
                    writer.write(n + " ");
                }
                writer.newLine();
            }
        }
        writer.close();
    }
}
