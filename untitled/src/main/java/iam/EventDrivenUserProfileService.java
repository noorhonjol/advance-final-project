package iam;

import Events.AddUserEvent;
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
        logger.info("Processing CollectDataEvent for UserName: " + collectEvent.getUserName()+"in UserProfileService");
        try {
            UserProfile userProfile = getUser(collectEvent.getUserName());

            if (userProfile == null) {
                logger.warn("User profile not found during CollectDataEvent for: " + collectEvent.getUserName());
                throw new NotFoundException("User profile not found for: " + collectEvent.getUserName());
            }

            EventHandlerMethods.handleUserDataEvent("user-profile", userProfile, collectEvent.getUserName());
            logger.info("CollectDataEvent processed successfully for UserName: " + collectEvent.getUserName());

        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during CollectDataEvent for UserName: " + collectEvent.getUserName() + ": " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unhandled error during CollectDataEvent for UserName: " + collectEvent.getUserName() + ": " + e.getMessage(), e);
            throw new RuntimeException("Unexpected error during CollectDataEvent: " + e.getMessage(), e);
        }
    }

    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, NotFoundException, BadRequestException {
        logger.info("Processing handleDeleteEvent for UserName: " + deleteEvent.getUserName()+"in UserProfileService");
        try {
            if (deleteEvent.getDeleteType() == DeleteType.soft) {
                logger.info("Skipping user deletion for soft delete event for UserName: " + deleteEvent.getUserName());
                return;
            }

            deleteUser(deleteEvent.getUserName());
            EventHandlerMethods.handleUserDataEvent("user-profile", new Object(), deleteEvent.getUserName());
            logger.info("User deletion processed for handleDeleteEvent for UserName: " + deleteEvent.getUserName());

        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during handleDeleteEvent for UserName: " + deleteEvent.getUserName() + ": " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unhandled error during handleDeleteEvent for UserName: " + deleteEvent.getUserName() + ": " + e.getMessage(), e);
            throw new RuntimeException("Unexpected error during handleDeleteEvent: " + e.getMessage(), e);
        }
    }
    @Subscribe
    void handleUserAddEvent(AddUserEvent userEvent) {

        String userName = userEvent.getUserProfile().getUserName();

        logger.info("Handling AddUserEvent for user: " + userName);


        userService.addUser(userEvent.getUserProfile());
        logger.info("User successfully added: " + userName);

        EventHandlerMethods.handleUserDataEvent("user-profile", userEvent.getUserProfile(), userName);

        logger.info("UserDataEvent handled for user: " + userName);

    }



}