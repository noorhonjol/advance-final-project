package iam;
import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;


public class UserProfileServiceDecorator implements IUserService {
    private final IUserService userService;
    public UserProfileServiceDecorator(IUserService userService){
        this.userService=userService;
    }
    @Override
    public void addUser(UserProfile user) {
        userService.addUser(user);
        EventHandlerMethods.handleUserDataEvent("user-profile",user,user.getUserName());
    }

    @Override
    public void updateUser(UserProfile user) throws NotFoundException, SystemBusyException, BadRequestException {
        userService.updateUser(user);
        EventHandlerMethods.handleUserDataEvent("user-profile",user,user.getUserName());
    }

    @Override
    public void deleteUser(String userName) throws SystemBusyException, NotFoundException, BadRequestException {
        userService.deleteUser(userName);
        EventHandlerMethods.handleUserDataEvent("user-profile","",userName);
    }

    @Override
    public UserProfile getUser(String userName) throws SystemBusyException, NotFoundException, BadRequestException {
        return userService.getUser(userName);
    }


}


