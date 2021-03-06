package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.data.Topic;
import cz.zcu.kiv.nlp.ir.trec.preprocessing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * @author tigi
 * @author Radek Vais
 */

public class TestTrecEval {

    /** instance loggeru */
    private static Logger log = LogManager.getLogger(TestTrecEval.class.getName());

    /** složka pro data a log TrecEval */
    public static final String OUTPUT_DIR = "TREC";

    public static void start(String args[]) {

        IPreprocessor preprocessor = PreprocessorFactory.preparePreprocessor(false,false, MainClass.getStopFiles());
        Index index = new Index(preprocessor, MainClass.isSmall());

        List<Topic> topics = SerializedDataHelper.loadTopic(new File(OUTPUT_DIR + "/topicData.bin"));

        File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

        List<Document> documents = new ArrayList<>();
        log.info("load");
        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.loadDocument(serializedData);
            } else {
                log.error("Cannot find " + serializedData);
            }
        } catch (Exception e) {
            log.error("Error loading data", e);
            System.exit(55);
        }
        log.info("Documents: " + documents.size());

        log.info("Indexing");
        if(MainClass.isLoad()
                && new File(MainClass.getIndexPath()+".inv").exists()
                && new File(MainClass.getIndexPath()+".idx").exists())
        {
            log.info("Load saved index.");
            index = new Index(MainClass.getIndexPath(), preprocessor, MainClass.isSmall());
        }else{
            log.info("Index documents");
            index.index(documents);
            if(MainClass.isSave()){
                log.info("Save index to disk");
                index.dumpIndex(MainClass.getIndexPath());
            }

        }

        log.info("Indexing done");

        List<String> lines = new ArrayList<>();

        for (Topic t : topics) {
            List<Result> resultHits = index.search(t.getTitle() + " " + t.getDescription());

            Comparator<Result> cmp = new Comparator<Result>() {
                public int compare(Result o1, Result o2) {
                    if (o1.getScore() > o2.getScore()) return -1;
                    if (o1.getScore() == o2.getScore()) return 0;
                    return 1;
                }
            };

            Collections.sort(resultHits, cmp);
            for (Result r : resultHits) {
                final String line = r.toString(t.getId());
                lines.add(line);
            }
            if (resultHits.size() == 0) {
                lines.add(t.getId() + " Q0 " + "abc" + " " + "99" + " " + 0.0 + " runindex1");
            }
        }
        final File outputFile = new File(OUTPUT_DIR + "/results " + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + ".txt");
        IOUtils.saveFile(outputFile, lines);
        //try to run evaluation
        try {
            runTrecEval(outputFile.toString()); // TODO odkomentovat trec eval
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String runTrecEval(String predictedFile) throws IOException {

        String commandLine = "./trec_eval.8.1/./trec_eval" +
                " ./trec_eval.8.1/czech" +
                " " + predictedFile;

        System.out.println(commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String trecEvalOutput;
        StringBuilder output = new StringBuilder("TREC EVAL output:\n");
        for (String line; (line = stdout.readLine()) != null; ) output.append(line).append("\n");
        trecEvalOutput = output.toString();
        System.out.println(trecEvalOutput);

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println(exitStatus);

        stdout.close();
        stderr.close();

        return trecEvalOutput;
    }
}
