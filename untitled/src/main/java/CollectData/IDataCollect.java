package CollectData;

import org.bson.Document;

public interface IDataCollect {

    void deleteData(String userName);
    Document getCollectedData(String userName);

}