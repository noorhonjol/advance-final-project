package Database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

public interface IDataBase {

    MongoCollection<Document> getCollection(String dbName, String collectionName);

    Document checkUserProfileInMongo(MongoCollection<Document> collection, String userName);

    void updateUserDataInMongo(MongoCollection<Document> collection, Document existingUserData, Document newDocs);

    void insertNewUserDataInMongo(MongoCollection<Document> collection, Document newUserData);

    void deleteUserDataInMongo(MongoCollection<Document> collection, String userName);
    
}