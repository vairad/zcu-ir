package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.gui.SearchWindow;

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

    public static void main(String args[]) {
        List<String> argsList = Arrays.asList(args);

        if(argsList.contains("-eval")){
            TestTrecEval.start(args);
            return;
        }

        if(argsList.contains("-gui")){
            SearchWindow.processGui(args);
        }
    }

}