package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.vs.DataLoader;
import cz.zcu.kiv.nlp.vs.Post;
import cz.zcu.kiv.nlp.vs.Record;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * @author tigi, pauli
 */
public class JavaAPIMain {

    private static final Logger log = LoggerFactory.getLogger(JavaAPIMain.class);

    //name of index
    static final String indexName = "vaisr-index";

    //name of type
    static final String typeName = "post";

    static final String id1 = "1";
    static final String id2 = "2";


    public static void main(String[] args) throws IOException {

        List<XContentBuilder> docsXContent = new ArrayList<>();

        //if cluster name is different than "elasticsearch"
        Settings settings = Settings.builder()
                .put("cluster.name", "vaisr-cluster")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);

        //TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        printConnectionInfo(client);

        IndexResponse response;
        IndexRequestBuilder requestBuilder;

        //create JSON document with Map
        requestBuilder = client.prepareIndex(indexName, typeName);

        //load my files
        DataLoader dl =  new DataLoader();
        dl.loadFile();

        XContentBuilder xContent;
        for (Record rec : dl.getDocuments()) {
            for (Post post: rec.getPosts()) {
                //XContentBuilder - ES helper
                xContent = createJsonDocument(rec, post);
                docsXContent.add(xContent);
            }
        }

        bulkIndex(docsXContent, client);

        searchDocument(client, indexName, typeName,"content" , "doporučuji");
        log.info("===================================================================================================");
        log.info("===================================================================================================");
        log.info("===================================================================================================");

        getDocument(client, indexName, typeName, "160");

        try {
            updateDocument(client,indexName,typeName, "160", "content", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX !" );
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        getDocument(client, indexName, typeName, "160");

        log.info("===================================================================================================");
        log.info("===================================================================================================");
        log.info("===================================================================================================");

        deleteDocument(client, indexName, typeName, "160");
        getDocument(client, indexName, typeName, "160");

        //close connection
        client.close();
    }

    public static void searchDocument(Client client, String index, String type,
                                      String field, String value) {

        //SearchType.DFS_QUERY_THEN_FETCH - more
        //https://www.elastic.co/blog/understanding-query-then-fetch-vs-dfs-query-then-fetch
        log.info("Searching \"" + value + "\" in field:" + "\"" + field + "\"");
        SearchResponse response = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)     //try change to SearchType.QUERY_THEN_FETCH - see the change in score
                .setQuery(QueryBuilders.matchQuery(field, value)) //Query match - simplest query
                .setFrom(0).setSize(30)                         //can be used for pagination
                .setExplain(true)
                .get();

        //print response
        printSearchResponse(response);

    }

    public static void searchPhrase(Client client, String index, String type,
                                    String field, String value) {
        log.info("Searching phrase \"" + value + "\" in field:" + "\"" + field + "\"");
        SearchResponse response = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchPhraseQuery(field, value))
                .setFrom(0).setSize(30)
                .setExplain(true)
                .get();

        printSearchResponse(response);
    }

    private static void printSearchResponse(SearchResponse response) {
        SearchHit[] results = response.getHits().getHits();
        log.info("Search complete");
        log.info("Search took: " + response.getTook().getMillis() + " ms");
        log.info("Found documents: " + response.getHits().totalHits);

        for (SearchHit hit : results) {
            log.info("--------");
            log.info("Doc id: " + hit.getId());
            log.info("Score: " + hit.getScore());
            String result = hit.getSourceAsString();
            log.info(result);
            //hit.getSourceAsMap();
        }
        log.info("------------------------------");
        System.out.println("");
    }

    private static void bulkIndex(List<XContentBuilder> docsXcontent, Client client) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        int index = 0;
        for (XContentBuilder document: docsXcontent) {
            bulkRequest.add(client.prepareIndex(indexName,typeName,Integer.toString(index)).setSource(document));
            index++;
        }

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            log.info("Error - Bulk Indexing");
        }else {
            log.info("Bulk index request complete");
        }
    }

    private static void printResponse(IndexResponse response) {
        // Index name
        String _index = response.getIndex();
        // Type name
        String _type = response.getType();
        // Document ID (generated or not)
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will get: 1)
        long _version = response.getVersion();
        // isCreated() is true if the document is a new one, false if it has been updated
        boolean created = response.status() == RestStatus.CREATED;
        log.info("Doc indexed to index: " + _index + " type: " + _type + " id: " + _id + " version: " + _version + " created: " + created);
    }

    public static Map<String, Object> putJsonDocument(Record rec, Post post) {

        Map<String, Object> jsonDocument = new HashMap<String, Object>();

        jsonDocument.put("name", rec.getName());
        jsonDocument.put("url", rec.getUrl());
        jsonDocument.put("details", rec.getDetails());
        jsonDocument.put("date", post.getCreated());
        jsonDocument.put("content", post.getContent());
        return jsonDocument;
    }

    public static XContentBuilder createJsonDocument(Record rec, Post post) throws IOException {
        XContentBuilder builder = jsonBuilder();
        builder.startObject()
                .field("name", rec.getName())
                .field("url", rec.getUrl())
                .field("details", rec.getDetails())
                .field("date", post.getCreated())
                .field("content", post.getContent())
                .endObject();
        return builder;
    }

    public static void getDocument(Client client, String index, String type, String id) {

        GetResponse getResponse = client.prepareGet(index, type, id).get();

        if (!getResponse.isExists()) {
            log.info("Document with id:" + id + " not found");
            return;
        }

        Map<String, Object> source = getResponse.getSource();

        log.info("------------------------------");
        log.info("Retrieved document");
        log.info("Index: " + getResponse.getIndex());
        log.info("Type: " + getResponse.getType());
        log.info("Id: " + getResponse.getId());
        log.info("Version: " + getResponse.getVersion());
        log.info("Document title: " + source.get("title"));
        log.info(source.toString());
        log.info("------------------------------");

        //parsing - mannualy, deserialize JSON to object...
        String title = (String)source.get("title");
        String content = (String)source.get("content");


    }

    //allows partial updates  - not whole doc
    public static void updateDocument(Client client, String index, String type,
                                      String id, String field, Object newValue) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index);
        updateRequest.type(type);
        updateRequest.id(id);
        updateRequest.doc(jsonBuilder()
                .startObject()
                .field(field, newValue)
                .endObject());

        client.update(updateRequest).get();
    }

    //alternative update
    public static void prepareUpdateDocument(Client client, String index, String type,
                                             String id, String field, Object newValue) throws IOException {
        client.prepareUpdate(index, type, id)
                .setDoc(jsonBuilder()
                        .startObject()
                        .field(field, newValue)
                        .endObject())
                .get();
    }


    public static void deleteDocument(Client client, String index, String type, String id) {

        DeleteResponse response = client.prepareDelete(index, type, id).get();
        log.info("Information on the deleted document:");
        log.info("Index: " + response.getIndex());
        log.info("Type: " + response.getType());
        log.info("Id: " + response.getId());
        log.info("Version: " + response.getVersion());
    }

    private static final void printConnectionInfo(Client client) {
        ClusterHealthResponse health = client.admin().cluster().prepareHealth().get();
        String clusterName = health.getClusterName();

        log.info("Connected to Cluster: " + clusterName);
        log.info("Indices in cluster: ");
        for (ClusterIndexHealth heal : health.getIndices().values()) {
            String index = heal.getIndex();
            int numberOfShards = heal.getNumberOfShards();
            int numberOfReplicas = heal.getNumberOfReplicas();
            ClusterHealthStatus status = heal.getStatus();

            log.info("Index: " + index);
            log.info("Status: " + status.toString());
            log.info("Number of Shards: " + numberOfShards);
            log.info("Number of Replicas: " + numberOfReplicas);
            log.info("---------");

        }

    }
}
