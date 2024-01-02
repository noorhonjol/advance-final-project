package org.main;


import CollectData.DataCollect;
import CreationAndMetaData.DataCreation;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import activity.EventDrivenUserActivityService;
import activity.IUserActivityService;
import activity.UserActivity;
import activity.UserActivityService;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import iam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import payment.EventDrivenPaymentService;
import payment.IPayment;
import payment.PaymentService;
import payment.Transaction;
import posts.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Application {

    private static final IUserActivityService userActivityService = new UserActivityService();
    private static final IPayment paymentService = new PaymentService();
    private static final IUserService userService = new UserService();
    private static final IPostService postService = new PostService();
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static String loginUserName;



    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException, SystemBusyException, NotFoundException, BadRequestException {
        generateRandomData();

        logger.info("Application Started: ");
        Instant start = Instant.now();
        System.out.println("Application Started: " + start);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        System.out.println("Note: You can use any of the following usernames: user0, user1, user2, user3, .... user99");
        String userName = scanner.nextLine();
        setLoginUserName(userName);
        //TODO Your application starts here. Do not Change the existing code



        var paymentServiceWithEvent=new EventDrivenPaymentService(paymentService);
        var postServiceWithEvent=new EventDrivenPostService(postService);
        var userServiceWithEvent=new EventDrivenUserProfileService(userService);
        var activityServiceWithEvent=new EventDrivenUserActivityService(userActivityService);

        var creation =new DataCreation();

        var dataCollector=new DataCollect();

        IMessageQueue messageQueue= MockQueue.getInstance();
        messageQueue.consume(paymentServiceWithEvent);
        messageQueue.consume(postServiceWithEvent);
        messageQueue.consume(userServiceWithEvent);
        messageQueue.consume(activityServiceWithEvent);
        messageQueue.consume(dataCollector);

        creation.requestToCollectData(userServiceWithEvent.getUser(getLoginUserName()));
//        creation.completePendingStatus(getLoginUserName());
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
            try {
                userActivityService.addUserActivity(new UserActivity("user" + i, "activity" + i + "." + j, Instant.now().toString()));
            } catch (Exception e) {
                System.err.println("Error while generating activity for user" + i);
            }
        }
    }

    private static void generatePayment(int i) {
        for (int j = 0; j < 100; j++) {
            paymentService.pay(new Transaction("user" + i, i * j, "description" + i + "." + j));
        }
    }

    private static void generatePost(int i) {
        for (int j = 0; j < 100; j++) {
            try {
                postService.addPost(new Post("title" + i + "." + j, "body" + i + "." + j, "user" + i, Instant.now().toString()));
            } catch (Exception e) {
                System.err.println("Error while generating post for user" + i);
            }
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
    public static String getLoginUserName() {
        return loginUserName;
    }

    private static void setLoginUserName(String loginUserName) {
        Application.loginUserName = loginUserName;
    }
}
