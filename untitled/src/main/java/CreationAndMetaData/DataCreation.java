package CreationAndMetaData;
import Database.MongoDBSingleton;
import Events.ChangeStatusEvent;
import Events.CreationCollectEvent;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import iam.UserProfile;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataCreation implements IDataCreation {
    private final Logger logger = LoggerFactory.getLogger(DataCreation.class);
    private final MockQueue messageQueue = MockQueue.getInstance();
    private final MongoDBSingleton dbSingleton = MongoDBSingleton.getInstance();

    @Override
    public void requestToCollectData(UserProfile userProfile) {
        try {
            String userName = userProfile.getUserName();

            String userType = String.valueOf(userProfile.getUserType());

            storeMetaData(userName, userType, "Pending");

            messageQueue.produce(new CreationCollectEvent(userName,userProfile.getUserType()));

        } catch (Exception e) {

            logger.error("error during data collection for user: " + userProfile.getUserName(), e);

        }
    }
    @Override
    public Document getMetaData(String userName) {

        MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyColection");

        try {
            return collection.find(Filters.eq("userName", userName)).first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void completePendingStatus(String userName) {
        try {

            MongoCollection<Document> collection = dbSingleton.getCollection("MyBase", "MyColection");

            Document document=dbSingleton.checkUserProfileInMongo(collection,userName);
            Document editedDocument=new Document(document);
            if(document==null){
                return;
            }
            editedDocument.put("status","Complete");
            dbSingleton.updateUserDataInMongo(collection,document,editedDocument);

        } catch (Exception e) {
            logger.error("error updating status to complete for user: " + userName, e);
        }
    }
    private void storeMetaData(String userName, String userType, String status) {
        try {
            Document metaData = new Document("userName", userName)
                    .append("userType", userType)
                    .append("status", status);

            MongoCollection<Document> collection =dbSingleton.getCollection("MyBase", "MyColection");
            if(userMetaIsExist(userName,collection)){
                return;
            }
            dbSingleton.insertNewUserDataInMongo(collection, metaData);

        } catch (Exception e) {
            logger.error("Error storing metadata for user: " + userName, e);
        }
    }
    @Subscribe
    void handleCompleteEvent(ChangeStatusEvent changeStatusEvent) {
        completePendingStatus(changeStatusEvent.getUserName());
    }
    private boolean userMetaIsExist(String userName,MongoCollection<Document> collection) {
        return dbSingleton.checkUserProfileInMongo(collection,userName)!=null;
    }
}