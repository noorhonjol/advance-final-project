package activity;

import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import exceptions.Util;
import java.util.*;
import java.util.logging.Logger;

public class UserActivityService implements IUserActivityService {
    private static final Logger logger = Logger.getLogger(UserActivityService.class.getName());
    private static final Map<String, List<UserActivity>> userActivityMap = new HashMap<>();

    @Override
    public void addUserActivity(UserActivity userActivity) throws BadRequestException {
        try {
            if (userActivity == null || userActivity.getUserId() == null) {
                throw new BadRequestException("User activity and user ID must not be null");
            }
            userActivityMap.computeIfAbsent(userActivity.getUserId(), key -> new ArrayList<>()).add(userActivity);
        } catch (BadRequestException e) {
            logger.warning("Error during adding user activity: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<UserActivity> getUserActivity(String userId) throws SystemBusyException, NotFoundException, BadRequestException {
        try {
            Util.validateUserName(userId);
            if (!userActivityMap.containsKey(userId)) {
                throw new NotFoundException("User does not exist with ID: " + userId);
            }
            return new ArrayList<>(userActivityMap.get(userId));
        } catch (BadRequestException | NotFoundException e) {
            logger.warning("Error during getting user activity: " + e.getMessage());
            throw e;
        }
    }

    public void removeUserActivity(String userId, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            Util.validateUserName(userId);
            if (!userActivityMap.containsKey(userId)) {
                throw new NotFoundException("User does not exist with ID: " + userId);
            }
            Iterator<UserActivity> iterator = userActivityMap.get(userId).iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                UserActivity activity = iterator.next();
                if (activity.getId().equals(id)) {
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new NotFoundException("Activity not found with ID: " + id);
            }
        } catch (BadRequestException | NotFoundException e) {
            logger.warning("Error during removing user activity: " + e.getMessage());
            throw e;
        } catch (SystemBusyException e) {
            logger.severe("System is currently busy: " + e.getMessage());
            throw e;
        }
    }
}
