package cz.zcu.kiv.nlp.vs;

import lombok.Getter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataLoader {

    String dataPath = "storage/hodnoceniLekaru/";
    String dataFile = "hodnoceniLekaru_2018-02-21_18_32_264Records.ser";

    @Getter private List<Record> documents;

    public void loadFile(){
        File data = new File(dataPath + dataFile);
        documents = Record.LoadRecordCollection(data);
    }

    public void indexDocuments(IndexWriter writer) {
        for (Record rec : documents) {
            try {
                addDoc(writer, rec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDoc(IndexWriter w, Record rec) throws IOException {
        for (Post post: rec.getPosts()) {
            Document doc = new Document();
            doc.add(new TextField("title", rec.getName().trim(), Field.Store.YES));
            // use a string field for isbn because we don't want it tokenized
            doc.add(new TextField("post", post.getContent().trim(), Field.Store.YES));
            w.addDocument(doc);
        }
    }
}
