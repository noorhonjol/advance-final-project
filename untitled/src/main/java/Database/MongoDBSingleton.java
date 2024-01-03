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
        logger.fine("Accessing database: " + dbName);
        return databases.computeIfAbsent(dbName, k -> {
            logger.info("Creating new database connection for: " + dbName);
            return mongoClient.getDatabase(dbName);
        });
    }

    @Override
    public MongoCollection<Document> getCollection(String dbName, String collectionName) {
        logger.fine("Fetching collection: " + collectionName + " from database: " + dbName);
        MongoDatabase database = getDatabase(dbName);
        return database.getCollection(collectionName);
    }

    @Override
    public Document checkUserProfileInMongo(MongoCollection<Document> collection, String userName) throws NotFoundException, SystemBusyException {

        logger.info("Checking user profile in MongoDB for UserName: " + userName);
        try {
            Document document = collection.find(Filters.eq("userName", userName)).first();
            if (document == null) {
                logger.warning("User not found in MongoDB: " + userName);
                throw new NotFoundException("User not found: " + userName);
            }
            logger.fine("User profile found in MongoDB for UserName: " + userName);
            return document;
        } catch (MongoException e) {
            logger.severe("MongoDB operation failed while checking user profile for UserName: " + userName + ": " + e.getMessage());
            throw new SystemBusyException("Database operation failed: " + e.getMessage());
        }

    }
    @Override
    public void updateUserDataInMongo(MongoCollection<Document> collection, Document existingUserData, Document newDocs)
            throws SystemBusyException {
        logger.info("Updating user data in MongoDB");
        try {
            collection.updateOne(existingUserData, new Document("$set", newDocs));
            logger.info("User data updated successfully in MongoDB");
        } catch (MongoException e) {
            logger.severe("MongoDB operation failed during user data update: " + e.getMessage());
            throw new SystemBusyException("Database operation failed: " + e.getMessage());
        }
    }

    @Override
    public void insertNewUserDataInMongo(MongoCollection<Document> collection, Document newUserData)
            throws SystemBusyException {
        logger.info("Inserting new user data into MongoDB");
        try {
            collection.insertOne(newUserData);
            logger.info("New user data inserted successfully into MongoDB");
        } catch (MongoException e) {
            logger.severe("MongoDB operation failed during new user data insertion: " + e.getMessage());
            throw new SystemBusyException("Database operation failed: " + e.getMessage());
        }
    }
    @Override
    public void deleteUserDataInMongo(MongoCollection<Document> collection, String userName)
            throws SystemBusyException {
        logger.info("Deleting user data from MongoDB for UserName: " + userName);
        try {
            collection.deleteOne(Filters.eq("userName", userName));
            logger.info("User data deleted successfully from MongoDB for UserName: " + userName);
        } catch (MongoException e) {
            logger.severe("MongoDB operation failed during user data deletion: " + e.getMessage());
            throw new SystemBusyException("Database operation failed: " + e.getMessage());
        }
    }

}
