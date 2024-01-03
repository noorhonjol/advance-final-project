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
        logger.info("Adding user activity for UserID: " + userActivity.getUserId());
        try {
            userActivityService.addUserActivity(userActivity);
            logger.fine("User activity added. Now handling user data event.");

            EventHandlerMethods.handleUserDataEvent("userActivities", getUserActivity(userActivity.getUserId()), userActivity.getUserId());
            logger.info("User data event handled successfully for UserID: " + userActivity.getUserId());

        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.warning("Error during adding user activity for UserID: " + userActivity.getUserId() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<UserActivity> getUserActivity(String userId) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Fetching user activities for UserID: " + userId);
        try {
            List<UserActivity> activities = userActivityService.getUserActivity(userId);
            logger.info("Successfully fetched user activities for UserID: " + userId);
            return activities;

        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.warning("Error during getting user activity for UserID: " + userId + ": " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void removeUserActivity(String userId, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Removing user activity for UserID: " + userId + ", ActivityID: " + id);
        try {
            userActivityService.removeUserActivity(userId, id);

            logger.fine("User activity removed. Now handling user data event.");

            EventHandlerMethods.handleUserDataEvent("userActivities", getUserActivity(userId), userId);
            logger.info("User data event handled successfully after removing activity for UserID: " + userId);

        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.warning("Error during removing user activity for UserID: " + userId + ", ActivityID: " + id + ": " + e.getMessage());
            throw e;
        }
    }


    abstract void update(String userId, String activityId, UserActivity newData) throws SystemBusyException, BadRequestException, NotFoundException;
}
