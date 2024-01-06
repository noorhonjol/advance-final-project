package Events;

import dataDeletion.DeleteType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DeleteEvent {
    private final String userName;
    private final DeleteType deleteType;
    private final Logger logger = LoggerFactory.getLogger(DeleteEvent.class);
    public DeleteEvent(String userName, DeleteType deleteType) {
        this.userName = userName;
        this.deleteType = deleteType;
        logger.info("DeleteEvent created for user: {}, deleteType: {}", userName, deleteType);
    }

    public String getUserName() {
        return userName;
    }

    public DeleteType getDeleteType() {
        return deleteType;
    }
}

