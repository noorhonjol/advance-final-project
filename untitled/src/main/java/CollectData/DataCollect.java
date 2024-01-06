package CollectData;

import Database.IDataBase;
import Database.MongoDBSingleton;
import Events.ChangeStatusEvent;
import Events.UserDataEvent;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import com.mongodb.client.MongoCollection;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import org.bson.Document;
import com.mongodb.MongoException;
import java.util.logging.Logger;



public class DataCollect implements IDataCollect {
    private static final Logger logger = Logger.getLogger(DataCollect.class.getName());
    private static final IDataBase dataBase = MongoDBSingleton.getInstance();
    private static final Integer NUMBER_OF_SERVICES=4;
    private static final Integer NUMBER_OF_EXTRA_FIELDS=2;

    @Override
    public Document getCollectedData(String userName) {
        try {
            MongoCollection<Document> collection = dataBase.getCollection("advance-course", "test");
            return dataBase.checkUserProfileInMongo(collection, userName);
        } catch (MongoException | NotFoundException | SystemBusyException e) {
            logger.severe("Failed to get collected data for user: " + userName + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to get collected data for user: " + userName, e);
        }
    }
    @Subscribe
    void updateOrCreateUserProfile(UserDataEvent event) throws SystemBusyException {
        try {
            MongoCollection<Document> collection = dataBase.getCollection("advance-course", "test");
            Document document = dataBase.checkUserProfileInMongo(collection, event.getUserName());

            if (document == null) {
                createNewUserProfile(collection, event);
            } else {
                updateUserProfile(collection, document, event);
                checkAndEnqueueChangeStatusEvent(document, event.getUserName());
            }

        } catch (MongoException | NotFoundException e) {
            logAndRethrowException(event.getUserName(), e);
        }
    }

    private void createNewUserProfile(MongoCollection<Document> collection, UserDataEvent event) throws SystemBusyException {
        dataBase.insertNewUserDataInMongo(collection, event.getServiceData());
    }

    private void updateUserProfile(MongoCollection<Document> collection, Document document, UserDataEvent event) throws SystemBusyException {
        dataBase.updateUserDataInMongo(collection, document, event.getServiceData());
    }

    private void checkAndEnqueueChangeStatusEvent(Document document, String userName) {
        int numberOfFieldsRequired = NUMBER_OF_SERVICES + NUMBER_OF_EXTRA_FIELDS;

        if (document.keySet().size() == numberOfFieldsRequired || document.keySet().size() == numberOfFieldsRequired - 1) {
            MockQueue.getInstance().produce(new ChangeStatusEvent(userName));
        }
    }

    private void logAndRethrowException(String userName, Exception e) {
        logger.severe("Failed to update/create user profile for: " + userName + ". Error: " + e.getMessage());
        throw new RuntimeException("Failed to update/create user profile for: " + userName, e);
    }
    @Override
    public void deleteData(String userName) {
        try {
            MongoCollection<Document> collection = dataBase.getCollection("advance-course", "test");
            dataBase.checkUserProfileInMongo(collection, userName);
            dataBase.deleteUserDataInMongo(collection, userName);
        } catch (MongoException | NotFoundException | SystemBusyException e) {
            logger.severe("Failed to delete data for user: " + userName + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to delete data for user: " + userName, e);
        }
    }
}



