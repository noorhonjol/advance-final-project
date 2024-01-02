package CollectData;

import Database.IDataBase;
import Database.MongoDBSingleton;
import Events.UserDataEvent;
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
                dataBase.insertNewUserDataInMongo(collection, event.getServiceData());
            } else {
                dataBase.updateUserDataInMongo(collection, document, event.getServiceData());
            }
        } catch (MongoException | NotFoundException e) {
            logger.severe("Failed to update/create user profile for: " + event.getUserName() + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to update/create user profile for: " + event.getUserName(), e);
        }
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
