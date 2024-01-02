package CreationAndMetaData;

import Events.CreationCollectEvent;
import MessageQueue.MockQueue;
import iam.IUserService;
import iam.UserProfile;
import org.bson.Document;

import java.util.List;

public class Creation implements IDataCreation{
    private final IUserService userService;

    public Creation(IUserService userService) {
        this.userService = userService;
    }
    @Override
    public void requestToCollectData(String userName) {

        UserProfile userProfile = userService.getUser(userName);

        MockQueue.getInstance().produce(new CreationCollectEvent(userName,userProfile.getUserType()));
    }

    @Override
    public List<Document> getMetaData() {
        return null;
    }


}
