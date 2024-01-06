package Events;

import iam.UserProfile;

public class AddUserEvent {
    private final UserProfile userProfile;

    public AddUserEvent(UserProfile userProfile) {
        this.userProfile = userProfile;
    }


    public UserProfile getUserProfile() {
        return userProfile;
    }

}