package cz.zcu.kiv.nlp.ir.trec.gui;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Třída obalující resource bundle aplikace.
 * @author Radek Vais
 */
public class Localization {

    private static Logger logger =	LogManager.getLogger(Localization.class.getName());

    private static ResourceBundle bundle;

    /**
     * Načte fxml definici prvků v okně a soubor s lokalizací
     * @param locale lokalizace k zobrazení
     */
    protected static void loadResource(Locale locale) {
        logger.debug("Load resources");
        logger.trace(locale.getDisplayName());
        bundle = ResourceBundle.getBundle("Texts", locale);
    }

    /**
     * Method returns resources for gui aplication.
     * @return Opened or default (cs-CZ) resources
     */
    public static ResourceBundle getBundle(){
        if(bundle == null){
            logger.warn("Loading default resource");
            loadResource(new Locale("cs", "CZ"));
        }
        return bundle;
    }
}

