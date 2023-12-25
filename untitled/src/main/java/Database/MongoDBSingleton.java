package Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoDBSingleton implements IDataBase{

    private static MongoDBSingleton instance;
    private final MongoClient mongoClient;
    private final Map<String, MongoDatabase> databases = new HashMap<>();

    private MongoDBSingleton() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
    }

    public static synchronized MongoDBSingleton getInstance() {
        if (instance == null) {
            instance = new MongoDBSingleton();
        }
        return instance;
    }

    private MongoDatabase getDatabase(String dbName) {
        return databases.computeIfAbsent(dbName, mongoClient::getDatabase);
    }

    @Override
    public void insert(String dbName, String collectionName, Document document) {
        MongoDatabase database = getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(document);
    }

    @Override
    public List<Document> findByUsername(String dbName, String collectionName, String username) {
        MongoDatabase database = getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq("username", username)).into(new ArrayList<>());
    }
}
