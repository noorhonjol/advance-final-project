package iam;
import Events.CheckUserAvailabilityEvent;
import Events.EventHandlerMethods;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;


public class UserProfileServiceDecorator implements IUserService {
    protected final IUserService userService;
    public UserProfileServiceDecorator(IUserService userService){
        this.userService=userService;
    }
    @Override
    public void addUser(UserProfile user) {
        MockQueue.getInstance().produce(new CheckUserAvailabilityEvent(user));
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


