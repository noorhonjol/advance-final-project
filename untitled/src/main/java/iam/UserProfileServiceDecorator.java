package iam;
import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserProfileServiceDecorator implements IUserService {
    private final IUserService userService;
    private static final Logger logger = LoggerFactory.getLogger(EventDrivenUserProfileService.class);

    public UserProfileServiceDecorator(IUserService userService){
        this.userService=userService;
    }
    @Override
    public void addUser(UserProfile user) {
        try {
            userService.addUser(user);
            EventHandlerMethods.handleUserDataEvent("user-profile", user, user.getUserName());
            logger.info("User added successfully: " + user.getUserName());
        } catch (Exception e) {
            logger.error("Error while adding user: " + user.getUserName(), e);
            throw new RuntimeException("Failed to add user: " + user.getUserName(), e);
        }
    }

    @Override
    public void updateUser(UserProfile user) throws NotFoundException, SystemBusyException, BadRequestException {
        try {
            userService.updateUser(user);
            EventHandlerMethods.handleUserDataEvent("user-profile", user, user.getUserName());
            logger.info("User updated successfully: " + user.getUserName());
        } catch (NotFoundException | SystemBusyException | BadRequestException e) {
            logger.error("Error during updating user: " + user.getUserName(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during updating user: " + user.getUserName(), e);
            throw new RuntimeException("Failed to update user: " + user.getUserName(), e);
        }
    }


    @Override
    public void deleteUser(String userName) throws SystemBusyException, NotFoundException, BadRequestException {
        try {
            userService.deleteUser(userName);
            EventHandlerMethods.handleUserDataEvent("user-profile", "", userName);
            logger.info("User deleted successfully: " + userName);
        } catch (NotFoundException | SystemBusyException | BadRequestException e) {
            logger.error("Error during deleting user: " + userName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during deleting user: " + userName, e);
            throw new RuntimeException("Failed to delete user: " + userName, e);
        }
    }

    @Override
    public UserProfile getUser(String userName) throws SystemBusyException, NotFoundException, BadRequestException {
        try {
            UserProfile user = userService.getUser(userName);
            logger.info("User retrieved successfully: " + userName);
            return user;
        } catch (NotFoundException | SystemBusyException | BadRequestException e) {
            logger.error("Error during retrieving user: " + userName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during retrieving user: " + userName, e);
            throw new RuntimeException("Failed to retrieve user: " + userName, e);
        }
    }

}