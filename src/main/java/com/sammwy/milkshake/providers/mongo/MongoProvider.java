package com.sammwy.milkshake.providers.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.sammwy.milkshake.Provider;
import com.sammwy.milkshake.ProviderInfo;
import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.RepositoryCache;
import com.sammwy.milkshake.Schema;
import com.sammwy.milkshake.query.Filter;

public class MongoProvider implements Provider {
    private MongoClient client;
    private MongoDatabase database;

    @Override
    public void connect(ProviderInfo info) {
        this.client = MongoClients.create(info.toURI("mongodb"));
        this.database = client.getDatabase(info.getDatabase());
    }

    @Override
    public boolean insert(String collection, Map<String, Object> data) {
        database.getCollection(collection).insertOne(new Document(data));
        return true;
    }

    @Override
    public int insertMany(String collection, List<Map<String, Object>> dataList) {
        List<Document> docs = dataList.stream().map(Document::new).toList();
        database.getCollection(collection).insertMany(docs);
        return docs.size();
    }

    @Override
    public boolean upsert(String collection, Map<String, Object> data) {
        String id = (String) data.get("_id");
        Document filter = new Document("_id", id);
        Document update = new Document("$set", new Document(data));
        UpdateOptions options = new UpdateOptions().upsert(true);
        database.getCollection(collection).updateOne(filter, update, options);
        return true;
    }

    @Override
    public List<Map<String, Object>> find(String collection, Filter.Find criteria) {
        Bson criteriaDoc = MongoUtils.toBson(criteria);
        FindIterable<Document> docs = database.getCollection(collection).find(criteriaDoc);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Document doc : docs) {
            results.add(doc);
        }
        return results;
    }

    @Override
    public Map<String, Object> findOne(String collection, Filter.Find criteria) {
        Bson criteriaDoc = MongoUtils.toBson(criteria);
        Document doc = database.getCollection(collection).find(criteriaDoc).first();
        return doc;
    }

    @Override
    public Map<String, Object> findById(String collection, String id) {
        Document doc = database.getCollection(collection).find(new Document("_id", id)).first();
        return doc;
    }

    @Override
    public int update(String collection, Filter.Find criteria, Filter.Update update) {
        Bson criteriaDoc = MongoUtils.toBson(criteria);
        Bson updateDoc = MongoUtils.toBson(update);

        UpdateResult result = database.getCollection(collection).updateMany(criteriaDoc, updateDoc);
        return (int) result.getModifiedCount();
    }

    @Override
    public boolean updateByID(String collection, String id, Filter.Update update) {
        Bson updateDoc = MongoUtils.toBson(update);
        Document filter = new Document("_id", id);
        UpdateResult result = database.getCollection(collection).updateOne(filter, updateDoc);
        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean updateOne(String collection, Filter.Find criteria, Filter.Update update) {
        Bson criteriaDoc = MongoUtils.toBson(criteria);
        Bson updateDoc = MongoUtils.toBson(update);
        UpdateResult result = database.getCollection(collection).updateOne(criteriaDoc, updateDoc);
        return result.getModifiedCount() > 0;
    }

    @Override
    public int delete(String collection, Filter.Find criteria) {
        Bson criteriaDoc = MongoUtils.toBson(criteria);
        DeleteResult result = database.getCollection(collection).deleteMany(criteriaDoc);
        return (int) result.getDeletedCount();
    }

    @Override
    public boolean deleteByID(String collection, String id) {
        DeleteResult result = database.getCollection(collection).deleteOne(new Document("_id", id));
        return result.getDeletedCount() > 0;
    }

    @Override
    public boolean deleteOne(String collection, Filter.Find criteria) {
        Bson criteriaDoc = MongoUtils.toBson(criteria);
        DeleteResult result = database.getCollection(collection).deleteOne(criteriaDoc);
        return result.getDeletedCount() > 0;
    }

    @Override
    public <T extends Schema> Repository<T> addRepository(Class<T> schemaClass) {
        Repository<T> repo = new Repository<>(this, schemaClass);
        RepositoryCache.cache(schemaClass, repo);
        return repo;
    }

    @Override
    public <T extends Schema> boolean initialize(Class<T> schemaClass) {
        return true;
    }
}
