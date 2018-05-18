package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import java.util.List;

public class Preprocessor implements IPreprocessor {

    private IStemmer stemmer;
    private ITokenizer tokenizer;

    @Override
    public void initilise(IStemmer stemmer, ITokenizer tokenizer) {

    }

    @Override
    public List<String> getProcessedForm(String sentence) {
        return null;
    }
}
