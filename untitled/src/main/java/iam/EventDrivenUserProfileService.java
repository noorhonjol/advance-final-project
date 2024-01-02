package iam;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import com.google.common.eventbus.Subscribe;
import dataDeletion.DeleteType;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDrivenUserProfileService extends UserProfileServiceDecorator {
    private static final Logger logger = LoggerFactory.getLogger(EventDrivenUserProfileService.class);

    public EventDrivenUserProfileService(IUserService userService) {
        super(userService);
    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, NotFoundException, BadRequestException {
        try {
            UserProfile userProfile = getUser(collectEvent.getUserName());

            if (userProfile == null) {
                throw new NotFoundException("User profile not found for: " + collectEvent.getUserName());
            }

            EventHandlerMethods.handleUserDataEvent("user-profile", userProfile, collectEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during CollectDataEvent: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unhandled error during CollectDataEvent: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, NotFoundException, BadRequestException {
        try {
            if (deleteEvent.getDeleteType() == DeleteType.soft) {
                return;
            }

            deleteUser(deleteEvent.getUserName());
            EventHandlerMethods.handleUserDataEvent("user-profile", new Object(), deleteEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during handleDeleteEvent: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unhandled error during handleDeleteEvent: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
