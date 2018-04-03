package cz.zcu.kiv.nlp.vs;

import cz.zcu.kiv.nlp.ir.HTMLDownloader;
import cz.zcu.kiv.nlp.ir.HTMLDownloaderInterface;
import cz.zcu.kiv.nlp.ir.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.ir.Utils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cz.zcu.kiv.nlp.vs.DownloaderType.BASIC;

/**
 * CrawlerVSCOM class acts as a controller. You should only adapt this file to serve your needs.
 * Created by Tigi on 31.10.2014.
 * Updated by Radek Vais on 20.2.2018
 */
public class CrawlerVSCOM {


    /**
     * Xpath expressions to extract and their descriptions.
     */
    private final static Map<String, String> xpathMap = new HashMap<String, String>();

    private static final int MAX_PAGES_COUNT = 500;
    private static final String DATE_TIME_FORMAT = "d.M.y";
    private static final String DATA_FILE_PATTERN = "Records.ser";
    private static DownloaderType DownloaderSwitch = BASIC;

    static {
        String forum_xpath = "//div[@id='diskuse']";
        String name_xpath = "//div[@id='content']//div[@class='vypis-polozka']//h2";
        String details_xpath = "//div[@id='content']//div[@class='vypis-polozka']//p";
        String nick_xpath = forum_xpath + "//h4[@class='nick']/a";
        String post_xpath = forum_xpath + "//div[@class='diskuse-polozka-right']";
        String date_xpath = post_xpath + "//span[@class='den']";

        xpathMap.put("html", forum_xpath + "/html()");
        xpathMap.put("date", date_xpath + "/tidyText()");
        xpathMap.put("name", name_xpath + "/tidyText()");
        xpathMap.put("detail", details_xpath + "/allText()");
        xpathMap.put("nick", nick_xpath + "/text()");
        xpathMap.put("posts", post_xpath + "/allText()");
    }

    private static String  url_xpath = "//div[@id='content']//div[@class='vypis-polozka']//a/@href";


    private static final String STORAGE_NAME = "hodnoceniLekaru";
    private static final String STORAGE = "./storage/" + STORAGE_NAME;
    private static String SITE = "http://www.hodnocenilekaru.cz/";
    private static String SITE_SUFFIX = "doktor/";


    /** ****************************************************************************
     * Be polite and don't send requests too often.
     * Waiting period between requests. (in milisec)
     */
    private static final int POLITENESS_INTERVAL = 1200;
    private static final Logger log = Logger.getLogger(CrawlerVSCOM.class);

    /** ****************************************************************************
     * Main method
     */
    public static void main(String[] args) {
        //Initialization
        initialiseLogging();
        initialiseOutputFolder();

        HTMLDownloaderInterface downloader = getDownloader();
        Map<String, Map<String, List<String>>> results = initialiseResults();
        Collection<String> urlsSet = getUrlsToCrawl(downloader);

        //Create file for each xPath expression (will be used for dump, all informations)
        Map<String, PrintStream> printStreamMap = new HashMap<>();
        for (String key : results.keySet()) {
            File file = new File(STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + ".txt");
            PrintStream printStream = null;
            try {
                printStream = new PrintStream(new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                log.error("Write access problem",e);
            }
            printStreamMap.put(key, printStream);
        }

        // Browse through all find URLs
        int count = 0;
        for (String url : urlsSet) {
            String link = url;

            // repair relative URLs
            if (!link.contains(SITE)) {
                link = SITE + url;
            }

            //do not mine main pages
            if( !link.contains(SITE + SITE_SUFFIX)){
                continue;
            }

            //Download and extract data according to xpathMap
            Map<String, List<String>> products = downloader.processUrl(link, xpathMap);
            count++;
            if (count % 100 == 0) {
                log.info(count + " / " + urlsSet.size() + " = " + count / (0.0 + urlsSet.size()) + "% done.");
            }

            for (String key : results.keySet()) {
                Map<String, List<String>> map = results.get(key);
                List<String> list = products.get(key);
                if (list != null) {
                    map.put(url, list);
                    log.info(Arrays.toString(list.toArray()));
                    //print
                    PrintStream printStream = printStreamMap.get(key);
                    for (String result : list) {
                        printStream.println(url + "\t" + result);
                    }
                }
            }
            try {
                Thread.sleep(POLITENESS_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //close print streams
        for (String key : results.keySet()) {
            PrintStream printStream = printStreamMap.get(key);
            printStream.close();
        }

        createRecords(results);


        // Save links that failed in some way.
        // Be sure to go through these and explain why the process failed on these links.
        // Try to eliminate all failed links - they consume your time while crawling data.
        reportProblems(downloader.getFailedLinks());
        downloader.emptyFailedLinks();
        log.info("-----------------------------");


//        // Print some information.
//        for (String key : results.keySet()) {
//            Map<String, List<String>> map = results.get(key);
//            Utils.saveFile(new File(STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + "_final.txt"),
//                    map, idMap);
//            log.info(key + ": " + map.size());
//        }
        System.exit(0);
    }

    /** ****************************************************************************
     * Method go through mined data using xPath.
     * Ctreates and save collection of Records.
     * @param results Map of mined results
     */
    private static void createRecords(Map<String, Map<String, List<String>>> results) {
        LinkedList<Record> records = new LinkedList<>();
        Map<String, List<String>> name = results.get("name");
        Map<String, List<String>> details = results.get("detail");
        Map<String, List<String>> html = results.get("html");
        //about posts
        Map<String, List<String>> nick = results.get("nick");
        Map<String, List<String>> date = results.get("date");
        Map<String, List<String>> posts = results.get("posts");


        for (String key: name.keySet() ) {
            Record rec = readResults(key, name.get(key), details.get(key), html.get(key), nick.get(key), date.get(key), posts.get(key));
            records.add(rec);
        }
        Record.SaveRecordCollection(STORAGE +"/"+ STORAGE_NAME  + "_" + Utils.SDF.format(System.currentTimeMillis()) + DATA_FILE_PATTERN, records);
    }

    /** ****************************************************************************
     * Read informations about one crawled url
     * @param url
     * @param names
     * @param details
     * @param html
     * @param nicks
     * @param dates
     * @param posts
     * @return completed record
     */
    private static Record readResults(String url, List<String> names, List<String> details, List<String> html, List<String> nicks, List<String> dates, List<String> posts) {
        Record rec = new Record();

        rec.setUrl(url);

        if(names.size() == 1){
            rec.setName(names.get(0));
        }else{
            log.error("Found multiple names on site");
            return rec;
        }

        rec.setDetails(details);

        if(names.size() == 1){
            rec.setHtml_forum(html.get(0));
        }else{
            log.error("Found multiple forum blocks on site");
            return rec;
        }

        if(nicks.size() != dates.size() && dates.size() != posts.size()){
            log.error("Found inconsistent elements in posts");
            return rec;
        }

        fillPosts(rec, nicks, dates, posts );
        return rec;
    }

    /** ****************************************************************************
     * Fill posts into gived record
     * @param rec link to record to filled
     * @param nicks
     * @param dates
     * @param posts
     */
    private static void fillPosts(Record rec, List<String> nicks, List<String> dates, List<String> posts) {
        SimpleDateFormat parser = new SimpleDateFormat(DATE_TIME_FORMAT);
        for (int index = 0; index < nicks.size(); index ++){
            Date d = null;
            String date = dates.get(index);
            try {
                d = parser.parse(date);
            } catch (ParseException e) {
                log.warn("Unsupported datetime found.");
                if(date.trim().equals("Dnes")){
                    d = new Date();
                }else{
                    log.error("Incompatible Date format", e);
                }
            }
            Post p = new Post(posts.get(index), nicks.get(index), d);
            rec.addPost(p);
        }
    }

    /** ****************************************************************************
     * Initialise map for crawling results
     * @return empty initialised map of keys to xpathMap
     */
    private static Map<String, Map<String, List<String>>> initialiseResults() {
        Map<String, Map<String, List<String>>> results = new HashMap<>();
        for (String key : xpathMap.keySet()) {
            Map<String, List<String>> map = new HashMap<>();
            results.put(key, map);
        }
        return results;
    }

    /** ****************************************************************************
     * Initialise output folder
     */
    private static void initialiseOutputFolder() {
        File outputDir = new File(STORAGE);
        if (!outputDir.exists()) {
            boolean mkdirs = outputDir.mkdirs();
            if (mkdirs) {
                log.info("Output directory created: " + outputDir);
            } else {
                log.error("Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: " + outputDir);
            }
        }
    }

    /** ****************************************************************************
     * Initialise logger
     */
    private static void initialiseLogging() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    /** ****************************************************************************
     * initialise collection of URLs to crawl
     * @param downloader instance of HTMLDownloader
     * @return Collection of urls
     */
    private static Collection<String> getUrlsToCrawl(HTMLDownloaderInterface downloader) {
        Collection<String> urlsSet = new HashSet<>();

        //Try to load links
        File links = new File(STORAGE + "_urls.txt");
        if (links.exists()) {
            //Add links from conf file
            try {
                List<String> lines = Utils.readTXTFile(new FileInputStream(links));
                for (String line : lines) {
                    urlsSet.add(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //Browse SITE paged pages
            for (int pageCount = 0; pageCount < MAX_PAGES_COUNT; pageCount++) {
                String link = SITE + "?&r=40&c=&q=&p=" + pageCount;
                urlsSet.addAll(downloader.getLinks(link, url_xpath));
            }
            Utils.saveFile(new File(STORAGE + "_" + Utils.SDF.format(System.currentTimeMillis()) + "_links_size_" + urlsSet.size() + ".txt"),
                    urlsSet);
        }
        return urlsSet;
    }

    /** ****************************************************************************
     * Chose and return instance of html downloader specified in field DownloaderSwitch
     * @return HTMLDownloader instance
     */
    private static HTMLDownloaderInterface getDownloader() {
        switch (DownloaderSwitch){
            case SELENIUM:
                return new HTMLDownloaderSelenium();
            case BASIC:
            default:
                return new HTMLDownloader();
        }
    }

    /** ****************************************************************************
     * Save file with failed links for later examination.
     *
     * @param failedLinks links that couldn't be downloaded, extracted etc.
     */
    private static void reportProblems(Set<String> failedLinks) {
        if (!failedLinks.isEmpty()) {

            Utils.saveFile(new File(STORAGE + Utils.SDF.format(System.currentTimeMillis()) + "_undownloaded_links_size_" + failedLinks.size() + ".txt"),
                    failedLinks);
            log.info("Failed links: " + failedLinks.size());
        }
    }

}
