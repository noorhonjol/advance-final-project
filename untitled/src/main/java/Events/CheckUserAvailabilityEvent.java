package Events;

import iam.UserProfile;

public class CheckUserAvailabilityEvent {
    private final UserProfile userProfile;

    public CheckUserAvailabilityEvent(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

}