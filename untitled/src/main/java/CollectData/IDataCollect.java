package CollectData;

import org.bson.Document;

import java.util.List;

public interface IDataCollect {

    void addData(Object data);
    void updateData(Object data);

    void deleteData(String userName);

    List<Document> getCollectedData(String userName);


}
