package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.vs.DataLoader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Arrays;

public class HelloLucene {
  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    //Analyzer analyzer = new StandardAnalyzer();
    Analyzer analyzer = new CzechAnalyzer();

    // 1. create the index
    Directory index = new RAMDirectory();

    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    DataLoader data = new DataLoader();
    data.loadFile();

    IndexWriter writer = new IndexWriter(index, config);
    data.indexDocuments(writer);
    writer.close();

    // 2. query
  //    String querystr = args.length > 0 ? args[0] : "stomatolog";
  //   String querystr = args.length > 0 ? args[0] : "stomatolog AND vynikající";
    String querystr = args.length > 0 ? args[0] : "Holubářová";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser("title", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    int pageIndex = 0;
    long hitsCount = 0;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;

    // 4. display page result
    System.out.println("Page :"+ pageIndex + "================");
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("title") + "\t" + d.get("post"));
    }

    // 5. search all other pages
    while (hits.length > 0) {
      docs = searcher.searchAfter(hits[hits.length - 1],q, hitsPerPage);
    }

    System.out.println("Found " + hitsCount + " hits.");


    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
  }
}
