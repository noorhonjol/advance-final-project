package Events;

import iam.UserType;

public class CreationCollectEvent {
    private final String userName;

    public UserType getUserType() {
        return userType;
    }

    private final UserType userType;


    public CreationCollectEvent(String userName, UserType userType) {
        this.userName = userName;
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }
}