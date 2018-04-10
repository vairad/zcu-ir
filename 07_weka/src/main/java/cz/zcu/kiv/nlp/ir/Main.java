package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.filters.FilterBuilder;
import cz.zcu.kiv.nlp.ir.filters.TweetPreprocessBatchFilter;
import cz.zcu.kiv.nlp.ir.utils.IOUtils;
import cz.zcu.kiv.nlp.ir.utils.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibLINEAR;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.MultiFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cz.zcu.kiv.nlp.ir.utils.IOUtils.loadInstancesFromArff;
import static cz.zcu.kiv.nlp.ir.utils.IOUtils.saveAsArffFile;

public class Main {


    //file with word embedding
    public static final String WORD_EMBEDDINGS_PATH = "./data/embeddings/w2v.twitter.edinburgh.100d.csv.gz";
//    public static final String WORD_EMBEDDINGS_PATH = "./data/embeddings/w2v.twitter.edinburgh10M.400d.csv.gz";


    //raw test and train data
    public static final String RAW_TEST_DATA = "./data/raw/testdata.manual.2009.06.14.csv";
    public static final String RAW_TRAIN_DATA = "./data/raw/training.1600000.processed.noemoticon.csv";

    //files in arff format with test and train data
    public static final String TEST_DATA = "./data/test-data.arff";
    public static final String TRAIN_DATA = "./data/train-data.arff";

    public static final String PREPROCESSED_TEST_DATA = "./data/preprocessed-test-data.arff";
    public static final String PREPROCESSED_TRAIN_DATA = "./data/preprocessed-train-data.arff";

    public static final String EXTRACTED_FEATURES_TEST = "./data/extracted-features-test.arff";
    public static final String EXTRACTED_FEATURES_TRAIN = "./data/extracted-features-train.arff";


    /**
     * size of training data for positive and negative class
     */
    static final long TRAIN_SIZE = 10000;

    public static long time;


    public static void main(String[] args) throws Exception {
        //load raw data and create arff file, can be run only once
        createArff();

        //preprocess Tweets and save
        preprocessTweets();

        System.out.println("Train data size:" + TRAIN_SIZE);
        MultiFilter usedFilters = null;

        //extract features
        FilterBuilder.FilterBuilderParams params =
                new FilterBuilder.FilterBuilderParams(false, false, false, WORD_EMBEDDINGS_PATH);
        usedFilters = extractFeatures(params);

        //train and evaluate
        Classifier classifier = trainAndEvaluate();

        //using classifier
        String tweet = "@tomas www.seznam.cz Hope you have the most amazing day and a great year ahead... Looking forward to watch #Rangasthalam Good luck :)";
        classifyTweet(classifier, usedFilters, tweet);
        tweet = "taehee taking pictures while sihyun piggybacking her :( i hate how they're so cute please :(";
        classifyTweet(classifier, usedFilters, tweet);
    }

    /**
     * Classifies given tweet with given classifier
     */
    private static void classifyTweet(Classifier classifier, MultiFilter multiFilter, String tweet) throws Exception {
        //create instance for tweet
        List<String[]> tweets = new ArrayList<>();
        tweets.add(new String[]{tweet, ""});
        //create instance from given tweet
        Instances twInst = Utils.createInstances(tweets, true);
        twInst.setClassIndex(1);


        //we need add prepreocess filter
        Filter preprocess = FilterBuilder.createPreprocessFilter(twInst);
        twInst = Filter.useFilter(twInst, preprocess);
        //saveAsArffFile("./data/classified.arff",twInst,false);

        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(multiFilter);
        fc.setClassifier(classifier);

        double pred = fc.classifyInstance(twInst.instance(0));
        System.out.println("Tweet:" + tweet);
        System.out.println("Predicted:" + twInst.classAttribute().value((int) pred));
        System.out.println("------------");
    }

    /**
     * Trains Classifier on training data and run evaluation on testing data
     *
     * @return used Classifier
     */
    private static Classifier trainAndEvaluate() throws Exception {
        //load extracted features
        Instances trainDataFeatures = loadInstancesFromArff(EXTRACTED_FEATURES_TRAIN);
        Instances testDataFeatures = loadInstancesFromArff(EXTRACTED_FEATURES_TEST);
        trainDataFeatures.setClassIndex(0);
        testDataFeatures.setClassIndex(0);
        Classifier model;

        //train naive bayes model
        model = new NaiveBayes();
        runModel(model, trainDataFeatures, testDataFeatures);

        return model;
    }

    private static void runModel(Classifier model, Instances trainDataFeatures, Instances testDataFeatures) throws Exception {
        time();
        System.out.println("Training model:" + model.getClass().getName());
        model.buildClassifier(trainDataFeatures);
        System.out.println("Model trained in: " + (System.currentTimeMillis() - time) + "ms" );

        Evaluation evaluation = new Evaluation(trainDataFeatures);
        evaluation.evaluateModel(model, testDataFeatures);
        String strSummary = evaluation.toSummaryString();
        System.out.println(strSummary);
        System.out.println("---------------");

        strSummary =  evaluation.toClassDetailsString();
        System.out.println(strSummary);

        System.out.println(evaluation.toMatrixString());
        //double[][] confusionMatrix = evaluation.confusionMatrix();

        System.out.println("---------------------------" + model.getClass().getName() + "--------------------------------------");
    }

    /**
     * Extracts features and return used MultiFilter
     */
    private static MultiFilter extractFeatures(FilterBuilder.FilterBuilderParams builderParams) throws Exception {
//        Instances trainData = loadInstancesFromArff(TRAIN_DATA);
//        Instances testData = loadInstancesFromArff(TEST_DATA);
        Instances trainData = loadInstancesFromArff(PREPROCESSED_TRAIN_DATA);
        Instances testData = loadInstancesFromArff(PREPROCESSED_TEST_DATA);

        //important to set class index
        trainData.setClassIndex(1);
        testData.setClassIndex(1);


        System.out.println("Extracting features");
        time();

        //create filters
        Filter[] filters = FilterBuilder.createFilters(builderParams);

        //apply filters
        MultiFilter multiFilter = new MultiFilter();
        multiFilter.setFilters(filters);
        multiFilter.setInputFormat(trainData);

        Instances filTrainData = Filter.useFilter(trainData, multiFilter);
        Instances filTestData = Filter.useFilter(testData, multiFilter);

        //save extracted features
        saveAsArffFile(EXTRACTED_FEATURES_TEST, filTestData, false);
        saveAsArffFile(EXTRACTED_FEATURES_TRAIN, filTrainData, false);

        System.out.println("Features extracted and saved in: " + (System.currentTimeMillis() - time) + "ms");

        //return used filters
        return multiFilter;
    }


    /**
     * Preprocess training and testing data
     */
    private static void preprocessTweets() throws Exception {
        //load arff files with data
        Instances trainData = loadInstancesFromArff(TRAIN_DATA);
        Instances testData = loadInstancesFromArff(TEST_DATA);

        //preprocess data
        TweetPreprocessBatchFilter tweetFilter = FilterBuilder.createPreprocessFilter(trainData);

        trainData = Filter.useFilter(trainData, tweetFilter);
        testData = Filter.useFilter(testData, tweetFilter);

        //save preprocessed data
        saveAsArffFile(PREPROCESSED_TEST_DATA, testData, false);
        saveAsArffFile(PREPROCESSED_TRAIN_DATA, trainData, false);

        System.out.println("Data preprocessed and saved");
    }

    /**
     * Create arff files from raw testing and training data
     */
    private static void createArff() throws IOException {
        //load raw data
        time();
        List<String> trainData = IOUtils.loadAllLines(RAW_TRAIN_DATA);
        List<String> testData = IOUtils.loadAllLines(RAW_TEST_DATA);
        System.out.println("Raw data loaded in: " + (System.currentTimeMillis() - time) + "ms" );

        time();
        //could be done online
        List<String[]> testTweets = Utils.extractTweet(testData,TRAIN_SIZE);
        List<String[]> trainTweets = Utils.extractTweet(trainData,TRAIN_SIZE);
        System.out.println("Tweets extracted in: " + (System.currentTimeMillis() - time) + "ms" );

        time();
        Instances testInstances = Utils.createInstances(testTweets, false);
        Instances trainInstances = Utils.createInstances(trainTweets, false);
        System.out.println("Instances created  in: " + (System.currentTimeMillis() - time) + "ms" );

        time();
        saveAsArffFile(TEST_DATA, testInstances, false);
        saveAsArffFile(TRAIN_DATA, trainInstances, false);
        System.out.println("Instances saved  in: " + (System.currentTimeMillis() - time) + "ms" );
    }

    private static void time() {
        time = System.currentTimeMillis();
    }
}