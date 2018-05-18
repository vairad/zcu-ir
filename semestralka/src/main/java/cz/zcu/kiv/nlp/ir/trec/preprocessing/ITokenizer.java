package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import java.util.List;

public interface ITokenizer {
    List<String> getTokens(String sentence);
}
