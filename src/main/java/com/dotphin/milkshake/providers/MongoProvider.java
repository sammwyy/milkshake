package com.dotphin.milkshake.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dotphin.milkshake.DataQuery;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

import org.bson.Document;

public class MongoProvider implements IProvider {

    private MongoClient client;
    private MongoDatabase database;

    @Override
    public IProvider connect(final String connectionURI) {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.

        this.client = MongoClients.create(connectionURI);
        this.database = this.client.getDatabase("test");
        return this;
    }

    @Override
    public IProvider disconnect() {
        this.client.close();
        return this;
    }

    @Override
    public String create(final String entity, final Map<String, Object> props) {
        final MongoCollection<Document> collection = this.database.getCollection(entity);
        InsertOneResult result = collection.insertOne(new Document(props));
        return result.getInsertedId().toString();
    }

    @Override
    public Map<String, Object> findByID(final String entity, String ID) {
        final MongoCollection<Document> collection = this.database.getCollection(entity);
        final Document filter = new Document("_id", ID);
        final Document doc = collection.find(filter).first();
        return doc;
    }

    @Override
    public List<Map<String, Object>> findMany(final String entity, DataQuery query) {
        final MongoCollection<Document> collection = this.database.getCollection(entity);
        final Document filter = new Document(query.getQueries());
        final FindIterable<Document> docs = collection.find(filter).limit(query.getLimit()).skip(query.getSkip());
        final List<Map<String, Object>> list = new ArrayList<>();

        docs.forEach((Document doc) -> {
            list.add(doc);
        });

        return list;
    }

    @Override
    public Map<String, Object> findOne(final String entity, DataQuery query) {
        final MongoCollection<Document> collection = this.database.getCollection(entity);
        final Document filter = new Document(query.getQueries());
        final Document doc = collection.find(filter).first();
        return doc;
    }

    @Override
    public DataQuery findByIDAndUpdate(final String entity, String ID, DataQuery update) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataQuery[] findManyAndUpdate(final String entity, DataQuery query, DataQuery update) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataQuery findOneAndUpdate(final String entity, String query, DataQuery update) {
        // TODO Auto-generated method stub
        return null;
    }

}
