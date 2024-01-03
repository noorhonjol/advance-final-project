package activity;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.logging.Logger;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import iam.UserType;

public class EventDrivenUserActivityService extends UserActivityServiceDecorator {
    private static final Logger logger = Logger.getLogger(EventDrivenUserActivityService.class.getName());

    public EventDrivenUserActivityService(IUserActivityService userActivityService) {
        super(userActivityService);
    }

    @Override
    void update(String userId, String activityId, UserActivity newData) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Updating activity for UserID: " + userId + ", ActivityID: " + activityId);

        try {
            List<UserActivity> userActivities = getUserActivity(userId);

            logger.fine("Fetched user activities for UserID: " + userId);

            UserActivity existingActivity = getUserActivity(userActivities, activityId);

            if (existingActivity == null) {

                logger.warning("Activity with ID: " + activityId + " not found for UserID: " + userId);
                throw new NotFoundException("Activity not found with ID: " + activityId);
            }

            userActivities.remove(existingActivity);
            userActivities.add(newData);

            logger.fine("Replaced old activity with new data for UserID: " + userId);

            EventHandlerMethods.handleUserDataEvent("userActivity", userActivities, userId);

            logger.info("User activity update event handled successfully for UserID: " + userId);

        } catch (SystemBusyException | BadRequestException | NotFoundException e) {

            logger.warning("Error during activity update for UserID: " + userId + ": " + e.getMessage());

            throw e;
        }
    }

    private UserActivity getUserActivity(List<UserActivity> userActivities, String activityId) {
        for (UserActivity userActivity : userActivities) {
            if (userActivity.getId().equals(activityId)) {
                logger.fine("Found matching activity with ID: " + activityId);
                return userActivity;
            }
        }

        logger.fine("No matching activity found with ID: " + activityId);
        return null;
    }


    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Collecting data event triggered for UserName: " + collectEvent.getUserName()+"on user activity service");

        try {
            if (collectEvent.getUserType() == UserType.NEW_USER) {
                EventHandlerMethods.handleUserDataEvent("userActivity", new Object(), collectEvent.getUserName());
                logger.info("Handled data event for new user: " + collectEvent.getUserName());
                return;
            }
            List<UserActivity> userActivities = getUserActivity(collectEvent.getUserName());
            logger.fine("Fetched user activities for UserName: " + collectEvent.getUserName());

            if (userActivities.isEmpty()) {
                logger.warning("No activities found for UserName: " + collectEvent.getUserName());
                throw new NotFoundException("No activities found for user: " + collectEvent.getUserName());
            }

            EventHandlerMethods.handleUserDataEvent("userActivity", userActivities, collectEvent.getUserName());
            logger.info("Data collection event handled successfully for UserName: " + collectEvent.getUserName());
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.warning("Error during data collection event for UserName: " + collectEvent.getUserName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Handling delete event for UserName: " + deleteEvent.getUserName()+"on user activity service");
        try {

            List<UserActivity> userActivities = getUserActivity(deleteEvent.getUserName());
            logger.fine("Fetched user activities for UserName: " + deleteEvent.getUserName());

            if (userActivities.isEmpty()) {
                logger.warning("No activities found for UserName: " + deleteEvent.getUserName());
                throw new NotFoundException("No activities found for user: " + deleteEvent.getUserName());
            }

            userActivities.clear();
            EventHandlerMethods.handleUserDataEvent("userActivity", new Object(), deleteEvent.getUserName());
            logger.info("User activities cleared and delete event handled for UserName: " + deleteEvent.getUserName());

        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.warning("Error during delete event for UserName: " + deleteEvent.getUserName() + ": " + e.getMessage());
            throw e;
        }

    }


}
