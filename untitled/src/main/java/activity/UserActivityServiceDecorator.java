package activity;

import Events.EventHandlerMethods;

import java.util.List;

public  abstract class UserActivityServiceDecorator implements IUserActivityService {
    private final IUserActivityService userActivityService;

    public UserActivityServiceDecorator(IUserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }


    @Override
    public void addUserActivity(UserActivity userActivity) {

        userActivityService.addUserActivity(userActivity);

        EventHandlerMethods.handleUserDataEvent("userActivity",getUserActivity(userActivity.getUserId()),userActivity.getUserId());

    }

    @Override
    public List<UserActivity> getUserActivity(String userId) {
        return userActivityService.getUserActivity(userId);
    }

    @Override
    public void removeUserActivity(String userId, String id) {

        userActivityService.removeUserActivity(userId,id);
        EventHandlerMethods.handleUserDataEvent("userActivity",getUserActivity(userId),userId);

    }

    abstract void update(String userId,String activityId,UserActivity newData);


}