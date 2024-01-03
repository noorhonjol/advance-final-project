package CreationAndMetaData;

import Database.MongoDBSingleton;
import Events.CreationCollectEvent;
import MessageQueue.MockQueue;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import iam.UserProfile;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;

public class DataCreation implements IDataCreation {
    private final Logger logger = LoggerFactory.getLogger(DataCreation.class);
    private final MockQueue messageQueue = MockQueue.getInstance();
    private final MongoDBSingleton dbSingleton = MongoDBSingleton.getInstance();

    @Override
    public void requestToCollectData(UserProfile userProfile) {
        try {
            String userName = userProfile.getUserName();
            String userType = String.valueOf(userProfile.getUserType());
            storeMetaData(userName, userType, "Pending");
            messageQueue.produce(new CreationCollectEvent(userName, userProfile.getUserType()));
        } catch (MongoException me) {
            logger.error("MongoDB error during data collection for user: " + userProfile.getUserName(), me);
        } catch (Exception e) {
            logger.error("General error during data collection for user: " + userProfile.getUserName(), e);
        }
    }


    @Override
    public Document getMetaData(String userName) throws NotFoundException, SystemBusyException {
        try {
            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyCollection");
            Document doc = collection.find(Filters.eq("userName", userName)).first();
            if (doc == null) {
                throw new NotFoundException("Metadata not found for user: " + userName);
            }
            logger.info("Retrieved metadata for user: " + userName);
            return doc;
        } catch (MongoException e) {
            logger.error("MongoDB error while retrieving metadata", e);
            throw new SystemBusyException("Database operation failed");
        }
    }

    public boolean completePendingStatus(String userName) throws NotFoundException, SystemBusyException {
        try {
            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyCollection");
            Document document = dbSingleton.checkUserProfileInMongo(collection, userName);
            if(document == null) {
                throw new NotFoundException("User not found: " + userName);
            }
            Document editedDocument = new Document(document);
            editedDocument.put("status", "Complete");
            dbSingleton.updateUserDataInMongo(collection, document, editedDocument);
            logger.info("Updated user status to 'Complete' for user: " + userName);
            return true;
        } catch (MongoException e) {
            logger.error("MongoDB error while updating user status", e);
            throw new SystemBusyException("Database operation failed");
        }
    }

    private void storeMetaData(String userName, String userType, String status) throws SystemBusyException {
        try {
            Document metaData = new Document("userName", userName)
                    .append("userType", userType)
                    .append("status", status);
            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyCollection");
            dbSingleton.insertNewUserDataInMongo(collection, metaData);
            logger.info("Stored metadata for user: " + userName);
        } catch (MongoException e) {
            logger.error("MongoDB error while storing metadata", e);
            throw new SystemBusyException("Database operation failed");
        }
    }
}
