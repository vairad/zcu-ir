import cz.zcu.kiv.nlp.vs.Post;
import cz.zcu.kiv.nlp.vs.Record;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestLoadingSerialisedFile {

    List<Record> records;

    @Before
    public void prepareData(){
        records = new LinkedList<>();
        for(int i = 0; i <100 ; i++) {
            Record rec = new Record();
            rec.setName("MuDr. Jmeno " + i);
            rec.setHtml_forum("<html>kyho html</html>");
            LinkedList<String> details = new LinkedList<>();
            details.add("det 1");
            details.add("det 3");
            details.add("" + i);
            rec.setDetails(details);
            rec.setUrl("http://mrkev.cz/" + i);
            for (int j = 0; j < 10; j++) {
                Post p = new Post("Kyhobsah s textem", "Ja ja ja jen", new Date());
                rec.addPost(p);
            }
            records.add(rec);
        }
    }

    @Before
    public void initialiseOutputFolder() {
        File outputDir = new File("tests");
        if (!outputDir.exists()) {
            boolean mkdirs = outputDir.mkdirs();
            if (mkdirs) {
                System.out.println("Test folder created.");
            } else {
                System.out.println("Test folder was NOT created.");
            }
        }
    }

    @org.junit.Test
    public void loadSerializedFileTest(){
        Record.SaveRecordCollection("tests/records.ser", records);

        List<Record> loaded = Record.LoadRecordCollection(new File("tests/records.ser"));

        Assert.assertEquals("Loaded collections differs",records, loaded);
    }

    @After
    public void cleanFiles(){
        File index = new File("tests");
        String[]entries = index.list();
        if(entries == null){
            return;
        }
        for(String s: entries){
            File currentFile = new File(index.getPath(),s);
            currentFile.delete();
        }
        index.delete();
        System.out.println("Test folder was cleaned.");
    }

}
