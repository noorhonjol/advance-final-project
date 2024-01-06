package iam;

import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import exceptions.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UserService implements IUserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private static final Map<String, UserProfile> users = new HashMap<>();

    @Override
    public void addUser(UserProfile user) {
        users.put(user.getUserName(), user);
    }

    @Override
    public void updateUser(UserProfile user) throws NotFoundException, SystemBusyException, BadRequestException {
        try {
            Util.validateUserName(user.getUserName());
            if (!users.containsKey(user.getUserName())) {
                throw new NotFoundException("User does not exist");
            }
            users.put(user.getUserName(), user);
        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.severe("Error in updateUser: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteUser(String userName) throws NotFoundException, SystemBusyException, BadRequestException {
        try {
            Util.validateUserName(userName);
            if (!users.containsKey(userName)) {
                throw new NotFoundException("User does not exist");
            }
            users.remove(userName);
        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.severe("Error in deleteUser: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public UserProfile getUser(String userName) throws NotFoundException, SystemBusyException, BadRequestException {
        try {
            Util.validateUserName(userName);
            if (!users.containsKey(userName)) {
                throw new NotFoundException("User does not exist");
            }
            return users.get(userName);
        } catch (BadRequestException | SystemBusyException | NotFoundException e) {
            logger.severe("Error in getUser: " + e.getMessage());
            throw e;
        }
    }
}

