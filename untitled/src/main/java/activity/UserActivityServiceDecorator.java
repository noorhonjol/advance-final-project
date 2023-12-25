package activity;

import java.util.List;

public  abstract class UserActivityServiceDecorator implements IUserActivityService {
    private IUserActivityService userActivityService;


    @Override
    public void addUserActivity(UserActivity userActivity) {
        userActivityService.addUserActivity(userActivity);
    }

    @Override
    public List<UserActivity> getUserActivity(String userId) {
        return userActivityService.getUserActivity(userId);
    }

    @Override
    public void removeUserActivity(String userId, String id) {
        userActivityService.removeUserActivity(userId,id);
    }

    abstract void update(String userId,UserActivity newData);


}