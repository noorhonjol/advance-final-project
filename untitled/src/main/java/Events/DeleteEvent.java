package Events;

import dataDeletion.DeleteType;

public class DeleteEvent {
    private final String userName;



    private final DeleteType deleteType;

    public DeleteEvent(String userName, DeleteType deleteType) {

        this.userName = userName;
        this.deleteType = deleteType;
    }

    public String getUserName() {
        return userName;
    }

    public DeleteType getDeleteType() {
        return deleteType;
    }
}