/**
 * Copyright (c) 2014, Michal Konkol
 * All rights reserved.
 */
package cz.zcu.kiv.nlp.ir;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Konkol
 *
 * @author Radek Vais
 * @version 27.3.2018
 */
public class AdvancedTokenizer implements Tokenizer {
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
    public static final String defaultRegex = httpRegex + "|"
                                            + dateRegex + "|"
                                            + currencyRegex + "|"
                                            + phoneRegex + "|"
                                            + decimalRegex + "|"
                                            + antiFuckRegex + "|"
                                            + htmlRegex + "|"
                                            +  numberRegex + "|"
                                            + punktRegex ;

    public static String[] tokenize(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            words.add(text.substring(start, end));
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        return ws;
    }

    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public String[] tokenize(String text) {
        return tokenize(text, defaultRegex);
    }
}
