package activity;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;

import java.util.List;

public  abstract class UserActivityServiceDecorator implements IUserActivityService {
    private final IUserActivityService userActivityService;

    public UserActivityServiceDecorator(IUserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }


    @Override
    public void addUserActivity(UserActivity userActivity) throws SystemBusyException, BadRequestException, NotFoundException {

        userActivityService.addUserActivity(userActivity);

        EventHandlerMethods.handleUserDataEvent("userActivity",getUserActivity(userActivity.getUserId()),userActivity.getUserId());

    }

    @Override
    public List<UserActivity> getUserActivity(String userId) throws SystemBusyException, BadRequestException, NotFoundException {
        return userActivityService.getUserActivity(userId);
    }

    @Override
    public void removeUserActivity(String userId, String id) throws SystemBusyException, BadRequestException, NotFoundException {

        userActivityService.removeUserActivity(userId,id);
        EventHandlerMethods.handleUserDataEvent("userActivity",getUserActivity(userId),userId);

    }

    abstract void update(String userId,String activityId,UserActivity newData) throws SystemBusyException, BadRequestException, NotFoundException;


}