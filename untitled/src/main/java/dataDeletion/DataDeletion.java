package dataDeletion;

import Events.AddUserEvent;
import Events.CheckUserAvailabilityEvent;

import Events.DeleteEvent;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import org.bson.Document;
import java.util.logging.Logger;
import java.util.HashSet;

public class DataDeletion implements IDelete {
    private final HashSet<String> deletedUsers=new HashSet<>();
    private static final Logger logger = Logger.getLogger(DataDeletion.class.getName());

    private final IMessageQueue messageQueue= MockQueue.getInstance();

    @Override
    public void deleteData(String userName, DeleteType deleteType) {
        logger.info("Deleting data for user: " + userName);

        messageQueue.produce(new DeleteEvent(userName, deleteType));

        logger.info("Data deletion event produced for user: " + userName);
    }

    @Subscribe
    void checkDeleteEvent(CheckUserAvailabilityEvent checkUserAvailabilityEvent) {
        String userName = checkUserAvailabilityEvent.getUserProfile().getUserName();

        logger.info("Received CheckUserAvailabilityEvent for user: " + userName);

        if (deletedUsers.contains(userName)) {

            logger.warning("Attempt to add a deleted user: " + userName);

            return;
        }

        logger.info("User is available and not deleted: " + userName);

        messageQueue.produce(new AddUserEvent(checkUserAvailabilityEvent.getUserProfile()));

        logger.info("AddUserEvent produced for user: " + userName);
    }



}
