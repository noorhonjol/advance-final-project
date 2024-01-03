package dataDeletion;

import Events.DeleteEvent;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import org.bson.Document;
import java.util.logging.Logger;

public class DataDeletion implements IDelete {
    private final IMessageQueue messageQueue = MockQueue.getInstance();
    private static final Logger logger = Logger.getLogger(DataDeletion.class.getName());

    @Override
    public void deleteData(String userName, DeleteType deleteType) {
        logger.info("Deleting data for user: " + userName);
        messageQueue.produce(new DeleteEvent(userName, deleteType));
        logger.info("Data deletion event produced for user: " + userName);
    }
}
