package CollectData;

import java.util.List;

public interface IDataCollect {

    //    void addData(Object data);
    //    void updateData(Object data);
    //
    //    void deleteData(String userName);

    List<Object> getCollectedData(String userName);
    void consumeEvent(String eventName);

    void produceEvent(String eventName);


}
