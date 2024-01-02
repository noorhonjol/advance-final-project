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
        try {
            List<UserActivity> userActivities = getUserActivity(userId);
            if (userActivities.isEmpty()) {
                throw new NotFoundException("No activities found for user: " + userId);
            }
            UserActivity existingActivity = null;
            for (UserActivity userActivity : userActivities) {
                if (userActivity.getId().equals(activityId)) {
                    existingActivity = userActivity;
                    break;
                }
            }
            if (existingActivity == null) {
                throw new NotFoundException("Activity not found with ID: " + activityId);
            }
            userActivities.remove(existingActivity);
            userActivities.add(newData);
            EventHandlerMethods.handleUserDataEvent("userActivity", userActivities, userId);
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.warning("Error during activity update: " + e.getMessage());
            throw e;
        }
    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            if (collectEvent.getUserType() == UserType.NEW_USER) {
                EventHandlerMethods.handleUserDataEvent("userActivity", new Object(), collectEvent.getUserName());
                return;
            }
            List<UserActivity> userActivities = getUserActivity(collectEvent.getUserName());
            if (userActivities.isEmpty()) {
                throw new NotFoundException("No activities found for user: " + collectEvent.getUserName());
            }
            EventHandlerMethods.handleUserDataEvent("userActivity", userActivities, collectEvent.getUserName());
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.warning("Error during data collection event: " + e.getMessage());
            throw e;
        }
    }

    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            List<UserActivity> userActivities = getUserActivity(deleteEvent.getUserName());
            if (userActivities.isEmpty()) {
                throw new NotFoundException("No activities found for user: " + deleteEvent.getUserName());
            }
            userActivities.clear();
            EventHandlerMethods.handleUserDataEvent("userActivity", new Object(), deleteEvent.getUserName());
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.warning("Error during delete event: " + e.getMessage());
            throw e;
        }
    }
}
