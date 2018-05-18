package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static Logger logger = LogManager.getLogger(Utils.class.getName());

    /**
     * Metoda přečte řádky vstupního proudu. Prázdné řádky jsou ignorovány. Okrajové bílé znaky ořezány.
     *
     * @param inputStream otevřený textový soubor
     * @return seznam nalezených řádků
     */
    public static List<String> readTXTFile(InputStream inputStream) {
        logger.trace("Entry method");

        if (inputStream == null) {
            logger.warn("Parameter is null - fault");
            throw new IllegalArgumentException("Cannot locate stream");
        }
        try {
            List<String> result = new ArrayList<String>();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            logger.trace("Reading file.");
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line.trim());
                }
            }

            inputStream.close();
            logger.trace("Reading done.");
            return result;
        } catch (IOException e) {
            logger.warn("IO Exception");
            throw new IllegalStateException(e);
        }
    }

    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
