package CreationAndMetaData;

import org.bson.Document;

import java.util.List;

public interface IDataCreation {

    void requestToCollectData(String userName);//this produce Event

    List<Document> getMetaData();



}
