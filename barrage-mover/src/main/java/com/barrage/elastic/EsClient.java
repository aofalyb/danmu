package com.barrage.elastic;


import com.barrage.common.Log;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class EsClient {

    private EsClient() {}

    private volatile static EsClient esClient;

    private static TransportClient client;

    private static Object lock = new Object();



    public static  EsClient getInstance() {
        if(esClient == null) {
            synchronized (lock) {
                if(esClient == null) {
                    esClient = new EsClient();
                }
            }
        }
        return esClient;
    }

    public static void shutdown() {
        if(client != null) {
            client.close();
        }

    }

    final String TYPE_NAME = "barrage";

    final String INDEX_NAME = "douyu-barrage_v3";
    public void init() throws Exception{
        long _s = System.currentTimeMillis();
        if(client == null) {
            Settings settings = Settings.builder().build();
            client  = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        }



        IndicesExistsResponse existsResponse = client.admin().indices().prepareExists(INDEX_NAME).execute().actionGet();

        Log.errorLogger.error("existsResponse="+existsResponse.isExists());
        if(!existsResponse.isExists()) {
            //创建

            CreateIndexResponse indexResponse = client.admin().indices().prepareCreate(INDEX_NAME).execute().actionGet();
            Log.errorLogger.error("indexResponse"+indexResponse.isAcknowledged());

        }

        //索引字段

        TypesExistsResponse typesResponse = client.admin().indices().prepareTypesExists(INDEX_NAME).setTypes(TYPE_NAME).execute().actionGet();
        Log.errorLogger.error("TypesExistsResponse="+typesResponse.isExists());
        if(!typesResponse.isExists()) {

            XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject("properties")
                    .startObject("uid").field("type", "keyword").endObject()
                    .startObject("nn").field("type", "keyword").endObject()
                    .startObject("text").field("type", "keyword").endObject()
                    .endObject().endObject();

            PutMappingRequest mappingRequest = Requests.putMappingRequest(INDEX_NAME).type(TYPE_NAME).source(mapping);

            Log.errorLogger.error("mapping="+client.admin().indices().putMapping(mappingRequest).actionGet().isAcknowledged());

        }


        long _e = System.currentTimeMillis();
        System.out.println(_e - _s);
    }

    private volatile static int _BATCH_LEN = 50;
    private AtomicInteger CURSOR = new AtomicInteger(0);
    private volatile BulkRequestBuilder bulkRequestBuilder;

    public boolean insert(String uid,String nn,String text) throws Exception {

        long _s = System.currentTimeMillis();

        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(INDEX_NAME, TYPE_NAME)
                .setSource(XContentFactory.jsonBuilder().startObject()
                        .field("uid", uid)
                        .field("nn", nn)
                        .field("text", text)
                        .endObject());

        if(bulkRequestBuilder == null) {
            bulkRequestBuilder = client.prepareBulk();
        }

        if(CURSOR.get() < _BATCH_LEN) {
            bulkRequestBuilder.add(indexRequestBuilder.request());
            CURSOR.incrementAndGet();
            return true;
        }

        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        bulkRequestBuilder = null;
        CURSOR.set(0);


        long _e = System.currentTimeMillis();
        System.out.println("insert cost time="+(_e - _s)+"ms, result="+bulkItemResponses.hasFailures()+".");

        return bulkItemResponses.hasFailures();
    }



}



