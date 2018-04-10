package cz.zcu.kiv.nlp.ir.filters;

import cz.zcu.kiv.nlp.ir.core.EmbeddingLoader;
import cz.zcu.kiv.nlp.ir.core.TweetPreprocessing;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {
    private FilterBuilder() {
        //private
    }

    /**
     * Returns array of filters, depending on given params
     */
    public static Filter[] createFilters(FilterBuilderParams params) throws Exception {
        List<Filter> filters = new ArrayList<>();

        if (params.isUsePreprocessFilter()) {
            filters.add(createPreprocessFilter(null));
        }

        if (params.isUseEmbeddingFilter()) {
            filters.add(createEmbeddingFilter(null, params.wordEmbeddingPath));
        }

        //if embedding and not ngram filter we must remove string attribute, from extracted features
        if (params.isUseEmbeddingFilter() && !params.isUseNGramFilter()) {
            Reorder reorder = new Reorder(); //or Remove filter Remove reorder = new Remove();
            reorder.setAttributeIndices("2-last");
            filters.add(reorder);
        }

        if (params.isUseNGramFilter()) {
            filters.add(createNGramFilter(null));
        }




        if (filters.size() == 0) {
            throw new Exception("No features to extraction");
        }

        Filter[] filtersArr = new Filter[filters.size()];
        filters.toArray(filtersArr);

        return filtersArr;
    }


    public static TweetPreprocessBatchFilter createPreprocessFilter(Instances instances) throws Exception {
        TweetPreprocessBatchFilter tweetFilter = new TweetPreprocessBatchFilter(new TweetPreprocessing());
        if (instances != null) {
            tweetFilter.setInputFormat(instances);
        }
        return tweetFilter;
    }


    public static TweetEmbeddingBatchFilter createEmbeddingFilter(Instances instances, String wordEmbPath) throws Exception {
        EmbeddingLoader loader = new EmbeddingLoader(wordEmbPath);
        TweetEmbeddingBatchFilter filter = new TweetEmbeddingBatchFilter(loader, new TweetPreprocessing());
        if (instances != null) {
            filter.setInputFormat(instances);
        }
        return filter;
    }

    public static StringToWordVector createNGramFilter(Instances instances) throws Exception {
        //TODO DONE
        //extract unigrams and bigrams
        Tokenizer tokenizer = new NGramTokenizer();
      //  tokenizer.

        StringToWordVector filter = new StringToWordVector();
        filter.setTokenizer(tokenizer);
        filter.setLowerCaseTokens(false);
        filter.setWordsToKeep(4000);   //default 1000

        //process only first attribute - tweet text
        filter.setAttributeIndices("1");

        if (instances != null) {
            //important
            filter.setInputFormat(instances);
        }

        return filter;

//        filter.setStemmer();
//        filter.setStopwordsHandler();
//        filter.setPeriodicPruning(5);
//        filter.setOutputWordCounts(true);
//        filter.setTFTransform(true);
//        filter.setIDFTransform(true);
    }

    public static class FilterBuilderParams{
        private boolean useEmbeddingFilter;
        private boolean usePreprocessFilter;
        private boolean useNGramFilter;

        private String wordEmbeddingPath = "";

        public FilterBuilderParams(boolean useEmbeddingFilter, boolean useNGramFilter, boolean usePreprocessFilter, String wordEmbeddingPath) {
            this.useEmbeddingFilter = useEmbeddingFilter;
            this.usePreprocessFilter = usePreprocessFilter;
            this.useNGramFilter = useNGramFilter;
            this.wordEmbeddingPath = wordEmbeddingPath;
        }

        public boolean isUseEmbeddingFilter() {
            return useEmbeddingFilter;
        }

        public boolean isUsePreprocessFilter() {
            return usePreprocessFilter;
        }

        public boolean isUseNGramFilter() {
            return useNGramFilter;
        }
    }
}
