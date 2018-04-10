package cz.zcu.kiv.nlp.ir.core;

import weka.core.SingleIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Class EmbeddingLoader loads word embbedings in CSV.gz format.
 * Word embeddings is available from https://affectivetweets.cms.waikato.ac.nz/ (Edinburgh corpus)
 * or https://github.com/felipebravom/AffectiveTweets/releases
 * and provides Map with loaded word embeddings
 */
public class EmbeddingLoader {

    /**
     * WordMap (dictionary) with words and its embedding, key is word (string),
     * value is List of double numbers
     */
    private Map<String, List<Double>> wordMap;

    /**
     * Path to file with word embeddings
     */
    private String filePath;

    /**
     * CSV separator, default = "\t" - tabulator
     */
    private String separator = "\t";

    /**
     * Index in line with word, default last
     */
    private SingleIndex wordNameIndex = new SingleIndex("last");

    /**
     * Dimension for word embeddings,
     */
    private int dimension;

    public EmbeddingLoader(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        this.filePath = filePath;
        this.wordMap = new HashMap<>(90000);
    }

    /**
     * Method initializes wordMap (dictionary)
     *
     * @return initialized wordMap (dictionary)
     */
    public Map<String, List<Double>> createWordMap() throws Exception {
        File embeddingFile = new File(filePath);
        GZIPInputStream gzs = new GZIPInputStream(new FileInputStream(embeddingFile));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gzs, StandardCharsets.UTF_8));

        boolean firstLine = true;
        String line;
        List<Double> wordVector;

        while ((line = bf.readLine()) != null) {
            String[] parts = line.split(this.separator);
            if (firstLine) {
                this.dimension = parts.length - 1;
                this.wordNameIndex.setUpper(this.dimension);
                firstLine = false;
            }

            wordVector = new ArrayList<>(dimension);

            //check if line has valid length
            if (parts.length - 1 == this.dimension) {
                for (int i = 0; i < parts.length - 1; i++) {
                    if (i != this.wordNameIndex.getIndex()) {
                        wordVector.add(Double.parseDouble(parts[i]));
                    }
                }

                //add word vector for word
                this.wordMap.put(parts[this.wordNameIndex.getIndex()], wordVector);
            }/*else {
                //System.out.println("Invalid line length");
               // throw new Exception("Invalid line length");
            }*/

        }

        bf.close();
        return this.wordMap;
    }

    public int getDimension() {
        return dimension;
    }

    public Map<String, List<Double>> getWordMap() {
        return wordMap;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public SingleIndex getWordNameIndex() {
        return wordNameIndex;
    }

    public void setWordNameIndex(SingleIndex wordNameIndex) {
        this.wordNameIndex = wordNameIndex;
    }
}
