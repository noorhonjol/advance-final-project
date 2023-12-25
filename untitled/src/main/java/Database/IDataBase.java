package Database;

import org.bson.Document;

import java.util.List;

public interface IDataBase {

    void insert(String dbName, String collectionName, Document document);
    List<Document> findByUsername(String dbName, String collectionName, String username);


    /*
        this interface for connect with database
        use singleton design pattern
     */

}
