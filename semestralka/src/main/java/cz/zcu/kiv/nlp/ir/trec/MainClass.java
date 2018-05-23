package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.gui.SearchWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainClass {



    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
        System.setProperty("output.dir", TestTrecEval.OUTPUT_DIR);
        System.setProperty("log4j.configurationFile", "log-conf.xml");
    }

    /** instance loggeru */
    private static Logger logger = LogManager.getLogger(TestTrecEval.class.getName());

    private static String indexPath = "index";
    private static int resultCount = 20;
    private static boolean noLoad;
    private static boolean noSave;
    private static boolean small;


    /**
     * Hlavní metoda programu. Parsuje argumenty a spouští jednotlivé moduly aplikace.
     * @param args pole argumentů
     */
    public static void main(String args[]) {
        List<String> argsList = Arrays.asList(args);

        small = argsList.contains("-small");
        noSave = argsList.contains("-noSave");

        noLoad = argsList.contains("-noLoad");

        if(argsList.contains("-index")){
           int indexPos = argsList.indexOf("-index");
           if(argsList.size() < (indexPos + 1 + 1) ){
               logger.error("No path pattern passed", (Object[]) args);
               System.out.println("No path pattern passed");
               return;
           }
           indexPath = argsList.get(indexPos + 1);
           logger.info("Path index set as: "+indexPath);
        }

        if(argsList.contains("-result")){
            int indexPos = argsList.indexOf("-result");
            if(argsList.size() < (indexPos + 1 + 1) ){
                logger.error("No number result param.",  Arrays.toString(args));
                System.out.println("No number result param.");
                return;
            }
            try{
                resultCount = Integer.parseInt(argsList.get(indexPos + 1));
                logger.info("Result count set to: "+resultCount);
            }catch (NumberFormatException e){
                logger.error("No number result param.", Arrays.toString(args));
                System.out.println("No number result param.");
                return;
            }
        }

        // run app
        if(argsList.contains("-eval")){
            TestTrecEval.start(args);
            return;
        }

        if(argsList.contains("-gui")){
            SearchWindow.processGui(args);
			return;
        }
		
		printHelp();
    }

    /**
     * Vytiskne nápovědu ke spuštění
     */
	private static void printHelp(){
		System.out.println("Use one of parameters -eval/-gui\n");
		System.out.println("-eval for run TestTrecEval");
		System.out.println("-gui for run GUI");
		System.out.println("-small for indexing only document headline.");
		System.out.println("-noLoad for disable loading index form file");
		System.out.println("-noSave for disable saving created index");
		System.out.println("-index [filePattern] path to index ending with name of index");
        System.out.println("-result [number] number of returned results");
	}


    /**
     * Flag, zda se má aplikace pokoušet načíst index.
     * @return !noLoad
     */
    public static boolean isLoad() {
        return !noLoad;
    }

    /**
     * Flag, zda se má aplikace pokoušet uložit index.
     * @return !noSave
     */
    public static boolean isSave() {
        return !noSave;
    }

    /**
     * Flag, zda se má aplikace načíst malý index.
     * @return small
     */
    public static boolean isSmall() {
        return small;
    }

    /**
     * Pseudo cesta k souboru uloženého indexu.
     * @return indexPath
     */
    public static String getIndexPath(){
        return indexPath;
    }

    /**
     * Kolik má aplikace vypisovat výsledků
     * @return resultCount
     */
    public static int getDocCount() {
        return resultCount;
    }

    public static String[] getStopFiles() {
        String [] stopFiles = {"stop-cz-dia-1.txt",
                "stop-cz-dia-2.txt",
                "stop-cz-dia-3.txt",
                "stop-spec-chars.txt"};
        return stopFiles;
    }
}