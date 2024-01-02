package activity;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import java.util.List;
import java.util.logging.Logger;

public abstract class UserActivityServiceDecorator implements IUserActivityService {
    private static final Logger logger = Logger.getLogger(UserActivityServiceDecorator.class.getName());
    private final IUserActivityService userActivityService;

    public UserActivityServiceDecorator(IUserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @Override
    public void addUserActivity(UserActivity userActivity) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            if (userActivity == null || userActivity.getUserId() == null || userActivity.getActivityType() == null) {
                throw new BadRequestException("user activity, user ID, and activity type must not be null");
            }
            userActivityService.addUserActivity(userActivity);
            EventHandlerMethods.handleUserDataEvent("userActivity", getUserActivity(userActivity.getUserId()), userActivity.getUserId());
        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.warning("Error during adding user activity: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<UserActivity> getUserActivity(String userId) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            return userActivityService.getUserActivity(userId);
        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.warning("Error during getting user activity: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void removeUserActivity(String userId, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            userActivityService.removeUserActivity(userId, id);
            EventHandlerMethods.handleUserDataEvent("userActivity", getUserActivity(userId), userId);
        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.warning("Error during removing user activity: " + e.getMessage());
            throw e;
        }
    }

    abstract void update(String userId, String activityId, UserActivity newData) throws SystemBusyException, BadRequestException, NotFoundException;
}
