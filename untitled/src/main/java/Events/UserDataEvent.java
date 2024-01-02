package Events;

import org.bson.Document;

public class UserDataEvent {
    public String getUserName() {
        return userName;
    }

    private final String userName;
    private final Document serviceData;


    public UserDataEvent(String userName, Document serviceData) {
        this.userName = userName;
        this.serviceData = serviceData;
    }

    public Document getServiceData() {
        return serviceData;
    }

}