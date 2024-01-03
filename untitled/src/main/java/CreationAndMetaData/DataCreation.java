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
    public void requestToCollectData(UserProfile userProfile) throws SystemBusyException {

        logger.info("Received data collection request for : " + userProfile.getUserName());

        try {
            String userName = userProfile.getUserName();

            String userType = String.valueOf(userProfile.getUserType());

            logger.info("Initiating data collection request for UserName: " + userName + ", UserType: " + userType);

            storeMetaData(userName, userType, "Pending");

            messageQueue.produce(new CreationCollectEvent(userName, userProfile.getUserType()));

            logger.info("Data collection request processed successfully for UserName: " + userName);

        } catch (MongoException me) {
            logger.error("MongoDB error during data collection for UserName: " + userProfile.getUserName(), me);
            throw new SystemBusyException("Database operation failed for data collection request: " + me.getMessage());
        } catch (Exception e) {
            logger.error("General error during data collection for UserName: " + userProfile.getUserName(), e);
            throw new RuntimeException("Error during data collection request: " + e.getMessage());
        }
    }


    @Override
    public Document getMetaData(String userName) throws NotFoundException, SystemBusyException {
        try {
            logger.info("Retrieving metadata for UserName: " + userName);
            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyCollection");
            Document doc = collection.find(Filters.eq("userName", userName)).first();

            if (doc == null) {
                logger.warn("Metadata not found for UserName: " + userName);
                throw new NotFoundException("Metadata not found for user: " + userName);
            }

            logger.info("Successfully retrieved metadata for UserName: " + userName);
            return doc;
        } catch (MongoException e) {
            logger.error("MongoDB error while retrieving metadata for UserName: " + userName, e);
            throw new SystemBusyException("Database operation failed while retrieving metadata: " + e.getMessage());
        }
    }


    public boolean completePendingStatus(String userName) throws NotFoundException, SystemBusyException {
        try {
            logger.info("Updating user status to 'Complete' for UserName: " + userName);
            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyCollection");
            Document document = dbSingleton.checkUserProfileInMongo(collection, userName);

            if (document == null) {
                logger.warn("User not found for status update: " + userName);
                throw new NotFoundException("User not found: " + userName);
            }

            Document editedDocument = new Document(document);
            editedDocument.put("status", "Complete");
            dbSingleton.updateUserDataInMongo(collection, document, editedDocument);

            logger.info("User status updated to 'Complete' for UserName: " + userName);
            return true;
        } catch (MongoException e) {
            logger.error("MongoDB error while updating user status for UserName: " + userName, e);
            throw new SystemBusyException("Database operation failed while updating status: " + e.getMessage());
        }
    }

    private void storeMetaData(String userName, String userType, String status) throws SystemBusyException {
        try {
            logger.info("Storing metadata for UserName: " + userName);
            Document metaData = new Document("userName", userName)
                    .append("userType", userType)
                    .append("status", status);

            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyCollection");
            dbSingleton.insertNewUserDataInMongo(collection, metaData);

            logger.info("Metadata stored successfully for UserName: " + userName);
        } catch (MongoException e) {
            logger.error("MongoDB error while storing metadata for UserName: " + userName, e);
            throw new SystemBusyException("Database operation failed while storing metadata: " + e.getMessage());
        }
    }

}
