package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import com.sun.xml.internal.bind.v2.model.core.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Radek Vais
 * @version 27.3.2018
 */
public class AdvancedTokenizer implements ITokenizer {

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(AdvancedTokenizer.class.getName());


    private IDictionary stopwords;

    public AdvancedTokenizer(IDictionary stopwords){
        logger.trace("Entry method");
        this.stopwords = stopwords;
    }
    //cislo |  | html | tecky a sracky

    private static final String decimalRegex = "(\\d+[.,](\\d+)?)";
    private static final String dateRegex = "(\\d{1,2}\\.\\d{1,2}\\.(\\d{2,4})?)";
    private static final String htmlRegex = "(<.*?>)";
    private static final String punktRegex = "([\\p{Punct}])";
    private static final String numberRegex = "([\\p{L}\\d]+)";
    //private static final String squareRegex = numberRegex+"x"+numberRegex;
    private static final String antiFuckRegex = "([\\p{L}\\w*]+)";
    private static final String httpRegex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final String currencyRegex = "((([\\d]{1,3})+ *)+(kc|Kc|Kč|kč|CZK))";
    private static final String phoneRegex = "(\\+{0,1}(([\\d]{3}) {0,1}){3,4})";

    /** combination of part regexes */
    private static final String defaultRegex = httpRegex + "|"
                                            + dateRegex + "|"
                                            + currencyRegex + "|"
                                            + phoneRegex + "|"
                                            + decimalRegex + "|"
                                            + antiFuckRegex + "|"
                                            + htmlRegex + "|"
                                            + numberRegex + "|"
                                            + punktRegex ;

    private static List<String> tokenize(String text, String regex) {
        logger.trace("Entry method");
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            words.add(text.substring(start, end));
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        logger.trace("Exit method");
        return Arrays.asList(ws);
    }

    @Override
    public List<String> getTokens(String text) {
        logger.trace("Entry method");
        List<String> results = tokenize(text, defaultRegex);
        if(stopwords != null){
            logger.debug("Removing stopwords");
            for (String result: results) {
                if(stopwords.isPresent(result)){
                    results.remove(result);
                }
            }
        }
        logger.trace("End method");
        return results;
    }


    @Override
    public String toString(){
        return "AdvancedTokenizer";
    }
}
