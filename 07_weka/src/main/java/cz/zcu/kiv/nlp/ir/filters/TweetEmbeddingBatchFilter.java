package cz.zcu.kiv.nlp.ir.filters;

import cz.zcu.kiv.nlp.ir.core.EmbeddingLoader;
import cz.zcu.kiv.nlp.ir.core.TweetPreprocessing;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.filters.SimpleBatchFilter;

import java.util.*;

public class TweetEmbeddingBatchFilter extends SimpleBatchFilter{

    /**
     * Index denoting tweet text, indexed from 0
     * default 0
     */
    private int textIndex = 0;

    /**
     * Dictionary with word embeddings
     */
    private Map<String, List<Double>> wordMap;

    /**
     * Embedding loader
     */
    private EmbeddingLoader embeddingLoader;

    /**
     * Class for preprocessing tweets
     */
    private TweetPreprocessing tweetPreprocessing;

    public TweetEmbeddingBatchFilter(EmbeddingLoader embeddingLoader, TweetPreprocessing tweetPreprocessing) {
        this.embeddingLoader = embeddingLoader;
        this.tweetPreprocessing = tweetPreprocessing;
    }

    @Override
    public String globalInfo() {
        return "Batch atribute filter that computes word embeddings for given tweets";
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
        //new attributes
        ArrayList<Attribute> att = new ArrayList<>();

        //Add all attributes of the input
        for(int i = 0; i < inputFormat.numAttributes(); i++) {
            att.add(inputFormat.attribute(i));
        }

        //create dictionary only for first time
        if (!this.isFirstBatchDone()) {
            embeddingLoader.createWordMap();
            //TODO DONE load and init word vector map
        }
        
        //Add new attributes
        int dimension = embeddingLoader.getDimension();
        for (int i = 0; i < dimension; i++) {
            att.add(embeddingLoader.)
            //TODO
        }

        Instances result = new Instances(inputFormat.relationName(), att, 0);
        result.setClassIndex(inputFormat.classIndex());

        return result;
    }

    @Override
    protected Instances process(Instances instances) throws Exception {
        //new header instances for preprocessed text
        Instances result = getOutputFormat();

        //attribute with text
        Attribute attrCont = instances.attribute(this.textIndex);

        Map<String, List<Double>> wordMap = embeddingLoader.getWordMap();
        //editWordMap(wordMap);

        long outOfVoc = 0;
        Set<String> outOfVocWords = new HashSet<>();

        //through instances
        for (int i = 0; i < instances.numInstances(); i++) {

            //saved values in instance
            double[] values = new double[result.numAttributes()];
            for (int j = 0; j < instances.numAttributes(); j++) {
                values[j] = instances.instance(i).value(j);
            }

            //TODO DONE
            //get tweet text from actual instance
            String content = instances.instance(i).toString(1);

            //tokenize tweet text
            List<String> tokens = tweetPreprocessing.tokenize(content);
            double embValue;
            int tokensSize = tokens.size();

            //add new values
            //go trough tokens and average word embeddings
            for (String token : tokens) {

                //if word embeddings contain our word (token)
                if (wordMap.containsKey(token)) {
                  //TODO


                }else {
                    outOfVoc++;
                    outOfVocWords.add(token);
                }
            }

            Instance inst = new SparseInstance(1, values);
            inst.setDataset(result);
            copyValues(inst, false, instances, result);
            result.add(inst);
        }

        //System.out.println("Out of vocabulary words:" + outOfVoc);
        /*System.out.println("Out of vocab words:");
        outOfVocWords.forEach(System.out::println);*/

        return result;
    }

    /**
     * Because we replaced all URLs with "WEBSITE_TOKEN" and usenames with "USER_TOKEN"
     * but in edinburg word embeddings are replaces as "http://www.url.com" and "@use" we have to add our
     * replaced tokens
     */
    private void editWordMap(Map<String, List<Double>> wordMap) {
        if (!wordMap.containsKey("WEBSITE_TOKEN")) {
            List<Double> vector = wordMap.get("http://www.url.com");
            wordMap.put("WEBSITE_TOKEN", new ArrayList<>(vector));
        }

        if (!wordMap.containsKey("USER_TOKEN")) {
            List<Double> vector = wordMap.get("@user");
            wordMap.put("USER_TOKEN", new ArrayList<>(vector));
        }

    }

    public int getTextIndex() {
        return textIndex;
    }

    public void setTextIndex(int textIndex) {
        this.textIndex = textIndex;
    }
}
