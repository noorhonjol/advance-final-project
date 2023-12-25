package CreationAndMetaData;

import org.bson.Document;

import java.util.List;

public interface IDataCreation {

    void requestToCollectData(String userName);//this produce Event

    List<Document> getMetaData();

    /*
        get data from all services and save the meta of data that want to save it
        based on user type and save the userName ( make this all on databases )
        the goal will be achieved by send data event of creation on queue and then consume it
         (your job is just send data that get it from services and save it in queue)

     */


}
