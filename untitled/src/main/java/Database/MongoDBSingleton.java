package Database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MongoDBSingleton implements IDataBase {

    private static final Logger logger = Logger.getLogger(MongoDBSingleton.class.getName());
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
    public MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase database = getDatabase(dbName);
        return database.getCollection(collectionName);
    }

    @Override
    public Document checkUserProfileInMongo(MongoCollection<Document> collection, String userName)
            throws NotFoundException, SystemBusyException {
        try {
            Document document = collection.find(Filters.eq("userName", userName)).first();
            if (document == null) {
                logger.warning("User not found: " + userName);
                throw new NotFoundException("User not found: " + userName);
            }
            return document;
        } catch (MongoException e) {
            logger.severe("Database operation failed: " + e.getMessage());
            throw new SystemBusyException("Database operation failed");
        }
    }

    @Override
    public void updateUserDataInMongo(MongoCollection<Document> collection, Document existingUserData, Document newDocs)
            throws SystemBusyException {
        try {
            collection.updateOne(existingUserData, new Document("$set", newDocs));
        } catch (MongoException e) {
            logger.severe("Database operation failed: " + e.getMessage());
            throw new SystemBusyException("Database operation failed");
        }
    }

    @Override
    public void insertNewUserDataInMongo(MongoCollection<Document> collection, Document newUserData)
            throws SystemBusyException {
        try {
            collection.insertOne(newUserData);
        } catch (MongoException e) {
            logger.severe("Database operation failed: " + e.getMessage());
            throw new SystemBusyException("Database operation failed");
        }
    }

    @Override
    public void deleteUserDataInMongo(MongoCollection<Document> collection, String userName)
            throws SystemBusyException {
        try {
            collection.deleteOne(Filters.eq("userName", userName));
        } catch (MongoException e) {
            logger.severe("Database operation failed: " + e.getMessage());
            throw new SystemBusyException("Database operation failed");
        }
    }
}
