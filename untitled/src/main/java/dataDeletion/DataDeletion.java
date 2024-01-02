package dataDeletion;
import Events.DeleteEvent;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import org.bson.Document;

public class DataDeletion implements IDelete{
    private final IMessageQueue messageQueue= MockQueue.getInstance();
    @Override
    public void deleteData(String userName, DeleteType deleteType) {

        messageQueue.produce(new DeleteEvent(userName,deleteType));
    }

}