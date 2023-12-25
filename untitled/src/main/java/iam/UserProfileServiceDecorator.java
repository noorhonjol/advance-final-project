package iam;

public class UserProfileServiceDecorator implements IUserService {
    private final IUserService userService;
    public UserProfileServiceDecorator(IUserService userService){
        this.userService=userService;
    }
    @Override
    public void addUser(UserProfile user) {
        userService.addUser(user);
    }

    @Override
    public void updateUser(UserProfile user) {
        userService.updateUser(user);
    }

    @Override
    public void deleteUser(String userName) {
        userService.deleteUser(userName);
    }

    @Override
    public UserProfile getUser(String userName) {
        return userService.getUser(userName);
    }


}