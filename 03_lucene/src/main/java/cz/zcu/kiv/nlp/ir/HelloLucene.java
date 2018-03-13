package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.vs.DataLoader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class HelloLucene {

  private static final String indexPath = "storage/finding_index.idx";

  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    //Analyzer analyzer = new StandardAnalyzer();
    StopWordManager manager = new StopWordManager(false);
    Analyzer analyzer = new CzechAnalyzer(new CharArraySet(manager.getStopWords(), true));

    // 1. create the index
    Directory index = FSDirectory.open(new File(indexPath).toPath());
    if(!DirectoryReader.indexExists(index)){
      IndexWriterConfig config = new IndexWriterConfig(analyzer);

      DataLoader data = new DataLoader();
      data.loadFile();

      IndexWriter writer = new IndexWriter(index, config);
      data.indexDocuments(writer);
      writer.close();
    }

    // 2. query
      String querystr = args.length > 0 ? args[0] : "stomatolog";
//  String querystr = args.length > 0 ? args[0] : "stomatolog AND vynikající";
      // String querystr = args.length > 0 ? args[0] : "(stomatolog AND vynikající) OR pediatr";
//    String querystr = args.length > 0 ? args[0] : "Holubářová";
  //  String querystr = args.length > 0 ? args[0] : "\"prase\"~3";


    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser("post", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    int pageIndex = 0;
    long hitsCount = 0;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;
    hitsCount += hits.length;

    // 4. display page result
    System.out.println("\nPage :"+ pageIndex + "================\n");
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("title") + "\t" + d.get("post"));
    }

    // 5. search all other pages
    while (hits.length == hitsPerPage) {
      docs = searcher.searchAfter(hits[hits.length - 1],q, hitsPerPage);
      hits = docs.scoreDocs;
      hitsCount += hits.length;
      pageIndex++;
      System.out.println("\nPage :"+ pageIndex + "================\n");
      for(int i=0;i<hits.length;++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        System.out.println((i + 1) + ". " + d.get("title") + "\t" + d.get("post"));
      }
    }

    System.out.println("\n\nFound " + hitsCount + " hits.");

    reader.close();
  }
}
