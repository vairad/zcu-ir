package cz.zcu.kiv.nlp.ir.utils;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class IOUtils {


    private IOUtils() {
        //private
    }

    /**
     * Read all lines from file, and returns list of readed lines
     */
    public static List<String> loadAllLines(String file) throws IOException {
        return Files.readAllLines(Paths.get(file), Charset.forName("Latin1"));
        //or
//        return Files.readAllLines(Paths.get(file), Charset.forName("UTF-8")); // but delete line number 240432 - "0","1980874671","Sun May 31 07:53:32 PDT 2009","NO_QUERY","Bibs_xO","@lee_collins HaHa yeahh ur right... but i cants "
    }

    /**
     * Read instances from given arff file
     */
    public static Instances loadInstancesFromArff(String fileName) throws Exception {
        ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(fileName);
        return dataSource.getDataSet();
    }

    /**
     * Save given instances as arff file, if compress flag is true the file is compressed with gzip (.gz)
     */
    public static void saveAsArffFile(String fileName, Instances instances, boolean compress) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        saver.setCompressOutput(compress);
        String fileExt = compress ? ".gz" : "";
        saver.setFile(new File(fileName + fileExt));
        saver.writeBatch();
    }

}
