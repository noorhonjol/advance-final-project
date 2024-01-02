package org.main;
import CollectData.DataCollect;
import CreationAndMetaData.Creation;
import Events.DeleteEvent;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import activity.EventDrivenUserActivityService;
import activity.IUserActivityService;
import activity.UserActivity;
import activity.UserActivityService;
import dataDeletion.DataDeletion;
import dataDeletion.DeleteType;
import iam.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import payment.EventDrivenPaymentService;
import payment.IPayment;
import payment.PaymentService;
import payment.Transaction;
import posts.*;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class Application {

    private static final IUserActivityService userActivityService = new UserActivityService();
    private static final IPayment paymentService = new PaymentService();
    private static final IUserService userService = new UserService();
    private static final IPostService postService = new PostService();
    private static final Logger logger = LoggerFactory.getLogger(Application.class);



    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        generateRandomData();

        logger.info("Application Started: ");

        //TODO Your application starts here. Do not Change the existing code
        var dataDeletion=new DataDeletion();
        var paymentServiceWithEvent=new EventDrivenPaymentService(paymentService);
        var postServiceWithEvent=new EventDrivenPostService(postService);
        var userServiceWithEvent=new EventDrivenUserProfileService(userService);
        var activityServiceWithEvent=new EventDrivenUserActivityService(userActivityService);

        var creation =new Creation(userServiceWithEvent);

        var dataCollector=new DataCollect();

        IMessageQueue messageQueue= MockQueue.getInstance();
        messageQueue.consume(paymentServiceWithEvent);
        messageQueue.consume(postServiceWithEvent);
        messageQueue.consume(userServiceWithEvent);
        messageQueue.consume(activityServiceWithEvent);
        messageQueue.consume(dataCollector);

        dataDeletion.deleteData("user2", DeleteType.hard);

//        creation.requestToCollectData("user2");



        //TODO Your application ends here. Do not Change the existing code
        logger.info("Application Ended: ");
    }
    private static void generateRandomData() {
        for (int i = 0; i < 100; i++) {
            generateUser(i);
            generatePost(i);
            generatePayment(i);
            generateActivity(i);
        }
        System.out.println("Data Generation Completed");
    }

    private static void generateActivity(int i) {
        for (int j = 0; j < 100; j++) {
            userActivityService.addUserActivity(new UserActivity("user" + i, "activity" + i + "." + j, Instant.now().toString()));
        }
    }

    private static void generatePayment(int i) {
        for (int j = 0; j < 100; j++) {
            paymentService.pay(new Transaction("user" + i, i * j, "description" + i + "." + j));
        }
    }

    private static void generatePost(int i) {
        for (int j = 0; j < 100; j++) {
            postService.addPost(new Post("title" + i + "." + j, "body" + i + "." + j, "user" + i, Instant.now().toString()));
        }
    }

    private static void generateUser(int i) {
        UserProfile user = new UserProfile();
        user.setUserName("user" + i);
        user.setFirstName("first" + i);
        user.setLastName("last" + i);
        user.setPhoneNumber("phone" + i);
        user.setEmail("email" + i);
        user.setPassword("pass" + i);
        user.setRole("role" + i);
        user.setDepartment("department" + i);
        user.setOrganization("organization" + i);
        user.setCountry("country" + i);
        user.setCity("city" + i);
        user.setStreet("street" + i);
        user.setPostalCode("postal" + i);
        user.setBuilding("building" + i);
        user.setUserType(getRandomUserType(i));
        userService.addUser(user);
    }

    private static UserType getRandomUserType(int i) {
        if (i > 0 && i < 3) {
            return UserType.NEW_USER;
        } else if (i > 3 && i < 7) {
            return UserType.REGULAR_USER;
        } else {
            return UserType.PREMIUM_USER;
        }
    }
}
