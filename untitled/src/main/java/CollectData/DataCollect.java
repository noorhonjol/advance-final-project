package CollectData;

import Database.IDataBase;
import Database.MongoDBSingleton;
import Events.ChangeStatusEvent;
import Events.UserDataEvent;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import com.mongodb.client.MongoCollection;
import org.bson.Document;



public class DataCollect implements IDataCollect{
    private static final Integer NUMBER_OF_SERVICES=4;
    private static final Integer NUMBER_OF_EXTRA_FIELDS=2;
    private static final IDataBase dataBase= MongoDBSingleton.getInstance();

   @Override
    public Document getCollectedData(String userName) {

        MongoCollection<Document> collection=dataBase.getCollection("advance-course","test");

        return dataBase.checkUserProfileInMongo(collection,userName);
    }


    @Subscribe
    void updateOrCreateUserProfile(UserDataEvent event){

        MongoCollection<Document>collection=dataBase.getCollection("advance-course","test");

        Document document=dataBase.checkUserProfileInMongo(collection,event.getUserName());

        if(document==null){
            dataBase.insertNewUserDataInMongo(collection,event.getServiceData());

            return;
        }

        Integer numberOfServicesWithExtraFields=NUMBER_OF_SERVICES+NUMBER_OF_EXTRA_FIELDS;

        if(document.keySet().size()==numberOfServicesWithExtraFields||document.keySet().size()==numberOfServicesWithExtraFields-1){

            MockQueue.getInstance().produce(new ChangeStatusEvent(event.getUserName()));

        }
        dataBase.updateUserDataInMongo(collection,document,event.getServiceData());

    }
    @Override
    public void deleteData(String userName) {

        MongoCollection<Document>collection=dataBase.getCollection("advance-course","test");

        dataBase.checkUserProfileInMongo(collection,userName);

        dataBase.deleteUserDataInMongo(collection,userName);
    }



}