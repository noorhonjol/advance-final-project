package Database;

import com.mongodb.client.MongoCollection;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import org.bson.Document;

import java.util.List;

public interface IDataBase {

    MongoCollection<Document> getCollection(String dbName, String collectionName);

    Document checkUserProfileInMongo(MongoCollection<Document> collection, String userName) throws NotFoundException, SystemBusyException;

    void updateUserDataInMongo(MongoCollection<Document> collection, Document existingUserData, Document newDocs) throws SystemBusyException;

    void insertNewUserDataInMongo(MongoCollection<Document> collection, Document newUserData) throws SystemBusyException;

    void deleteUserDataInMongo(MongoCollection<Document> collection, String userName) throws SystemBusyException;
    
}