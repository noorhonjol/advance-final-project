package Database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

public interface IDataBase {

    void insert(String dbName, String collectionName, Document document);
//    List<Document> findByUsername(String dbName, String collectionName, String username);


    /*
        this interface for connect with database
        use singleton design pattern
     */

    MongoCollection<Document> getCollection(String dbName, String collectionName);

    Document checkUserProfileInMongo(MongoCollection<Document> collection, String userName);

    void updateUserDataInMongo(MongoCollection<Document> collection, Document existingUserData, Document newDocs);

    void insertNewUserDataInMongo(MongoCollection<Document> collection, Document newUserData);
}
