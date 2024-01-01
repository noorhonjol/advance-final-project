package CollectData;

import Database.IDataBase;
import Database.MongoDBSingleton;
import Events.UserDataEvent;
import com.google.common.eventbus.Subscribe;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.List;

public class DataCollect implements IDataCollect{

    private static final IDataBase dataBase= MongoDBSingleton.getInstance();
    @Override
    public void addData(Object data) {

    }

    @Override
    public void updateData(Object data) {

    }

    @Override
    public void deleteData(String userName) {

    }

    @Override
    public Document getCollectedData(String userName) {

        MongoCollection<Document>collection=dataBase.getCollection("advance-course","test");

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
        dataBase.updateUserDataInMongo(collection,document,event.getServiceData());

    }



}

