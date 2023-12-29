package CreationAndMetaData;
import Database.MongoDBSingleton;
import MessageQueue.MockQueue;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import iam.IUserService;
import iam.UserProfile;
import iam.UserType;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataCreation implements IDataCreation {
    private final Logger logger = LoggerFactory.getLogger(DataCreation.class);
    private final Queue<Document> dataCollectionQueue = new LinkedBlockingQueue<>();
    private final MockQueue messageQueue = MockQueue.getInstance();
    private final MongoDBSingleton dbSingleton = MongoDBSingleton.getInstance();
    private final IUserService userService;

    public DataCreation(IUserService userService) {
        this.userService = userService;
    }
    @Override
    public void requestToCollectData(String userName) {
        try {
            String userType = String.valueOf(getUserType(userName));
            Document userData = collectUserData(userName, userType);
            String status = userData.getString("status");
            storeMetaData(userName, userType, status);
            Document dataCollectionEvent = createDataCollectionEvent(userName, userType, userData);
            dataCollectionQueue.add(dataCollectionEvent);
            processDataCollectionQueue();
        } catch (Exception e) {
            logger.error("error during data collection for user: " + userName, e);
        }
    }
    private Document createDataCollectionEvent(String userName, String userType, Document userData) {
        return new Document("type", "dataCollection")
                .append("userName", userName)
                .append("userType", userType)
                .append("userData", userData);
    }
    @Override
    public List<Document> getMetaData() {
        MongoDatabase database = dbSingleton.getDatabase("MyBase");
        MongoCollection<Document> collection = database.getCollection("MyColection");
        return collection.find().into(new ArrayList<>());
    }
    private UserType getUserType(String userName) {
        UserProfile userProfile = userService.getUser(userName);
        if (userProfile != null) {
            return userProfile.getUserType();
        } else {
            return UserType.NEW_USER;
        }
    }
    private Document collectUserData(String userName, String userType) {
        String status = "Pennding";
        Document userData = new Document();
        userData.append("userName", userName)
                .append("userType", userType)
                .append("status", status);
        return userData;
    }
    public void processDataCollectionQueue() {
        while (!dataCollectionQueue.isEmpty()) {
            try {
                Document event = dataCollectionQueue.poll();
                messageQueue.produce("dataCollectionQueue", event);
            } catch (Exception e) {
                logger.error("Error processing data collection queue", e);
            }
        }
    }
    private void storeMetaData(String userName, String userType, String status) {
        try {
            Document metaData = new Document("userName", userName)
                    .append("userType", userType)
                    .append("status", status);
            dbSingleton.insert("MyBase", "MyColection", metaData);
        } catch (Exception e) {
            logger.error("Error storing metadata for user: " + userName, e);
        }
    }
}