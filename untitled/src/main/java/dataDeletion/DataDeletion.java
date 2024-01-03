package dataDeletion;
import Events.AddUserEvent;
import Events.CheckUserAvailabilityEvent;
import Events.DeleteEvent;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import org.bson.Document;

import java.util.HashSet;

public class DataDeletion implements IDelete{

    private final HashSet<String> deletedUsers=new HashSet<>();

    private final IMessageQueue messageQueue= MockQueue.getInstance();
    @Override
    public void deleteData(String userName, DeleteType deleteType) {

        deletedUsers.add(userName);

        messageQueue.produce(new DeleteEvent(userName,deleteType));

    }
    @Subscribe
    void checkDeleteEvent(CheckUserAvailabilityEvent checkUserAvailabilityEvent){
        if(deletedUsers.contains(checkUserAvailabilityEvent.getUserProfile().getUserName())){

            System.out.println("user is deleted");

            return ;
        }

        messageQueue.produce(new AddUserEvent(checkUserAvailabilityEvent.getUserProfile()));

    }


}