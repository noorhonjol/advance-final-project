package org.main;


import CollectData.DataCollect;
import DataExport.DataExport;
import Database.IDataBase;
import Database.MongoDBSingleton;
import activity.IUserActivityService;
import activity.UserActivity;
import activity.UserActivityService;
import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import iam.IUserService;
import iam.UserProfile;
import iam.UserService;
import iam.UserType;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import payment.IPayment;
import payment.PaymentService;
import payment.Transaction;
import posts.IPostService;
import posts.Post;
import posts.PostService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Application {

    private static final IUserActivityService userActivityService = new UserActivityService();
    private static final IPayment paymentService = new PaymentService();
    private static final IUserService userService = new UserService();
    private static final IPostService postService = new PostService();
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException, GeneralSecurityException {
//        generateRandomData();

        logger.info("Application Started: ");
        //TODO Your application starts here. Do not Change the existing code

        Scanner scanner = new Scanner(System.in);
        System.out.println("How do you want to get your Data:");
        System.out.println("1. Export data and download directly");
        System.out.println("2. Upload data to cloud storage and get a link.");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        long startTime = System.currentTimeMillis();

        DataCollect dataCollect = new DataCollect();
        DataExport dataExport=new DataExport(dataCollect);
        switch (choice) {
            case 1:
                String fileName = dataExport.getPathOfProcessedData("username");
                System.out.println("Data exported to file: " + fileName);
                break;
            case 2:
                String cloudLink = dataExport.exportAndUploadData("username", "GoogleDrive");
                System.out.println("Data uploaded to cloud. This is the Link: " + cloudLink);
                break;
            default:
                System.out.println("Invalid choice.");
        }
        long endTime = System.currentTimeMillis();
        logger.info("Data export for user completed in {} ms", (endTime - startTime));
        IDataBase database=MongoDBSingleton.getInstance();
//        Document newUser = new Document("username", "john_doe")
//                .append("email", "john@example.com")
//                .append("name", "John Doe");
//        database.insert("advance-course", "test", newUser);
        var res=database.findByUsername("admin","zeft","");

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
