package iam;

import CreationAndMetaData.IDataCreation;
import Database.MongoDBSingleton;
import MessageQueue.MockQueue;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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


