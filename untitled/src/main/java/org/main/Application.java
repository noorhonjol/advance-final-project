package org.main;
import CollectData.DataCollect;
import DataExport.DataExport;
import CreationAndMetaData.DataCreation;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import activity.EventDrivenUserActivityService;
import activity.IUserActivityService;
import activity.UserActivity;
import activity.UserActivityService;
import dataDeletion.DataDeletion;
import dataDeletion.DeleteType;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import exceptions.Util;
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
        int maxAttempts = 3;
        int attempt = 0;
        boolean success = false;
        Scanner scanner = new Scanner(System.in);
        while (attempt < maxAttempts && !success) {
            try {
                System.out.println("Enter your username: ");
                System.out.println("Note: You can use any of the following usernames: user0, user1, user2, user3, .... user99");
                String userName = scanner.nextLine();
                Util.validateUserName(userName);
                setLoginUserName(userName);
                success = true;

            } catch (SystemBusyException e) {
                System.out.println("System is busy, please try again.");
                attempt++;
            } catch (BadRequestException e) {
                System.out.println("Bad request: " + e.getMessage());
                break;
            }
        }
        if (!success) {
            System.out.println("Failed to process after " + maxAttempts + " attempts.");
            return;
        }
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
        messageQueue.consume(creation);

        creation.requestToCollectData(userServiceWithEvent.getUser(getLoginUserName()));
        System.out.println("How do you want to delete your data:");
        System.out.println("1. Soft delete ");
        System.out.println("2. Hard delete ");
        System.out.print("Enter your choice: ");
        int deleteChoice = scanner.nextInt();

        DataDeletion dataDeletion = new DataDeletion();

        switch (deleteChoice) {
            case 1:
                logger.info("User chose soft delete for user: {}", getLoginUserName());
                dataDeletion.deleteData(getLoginUserName(), DeleteType.soft);
                System.out.println("Soft delete initiated for user: " + getLoginUserName());
                break;
            case 2:
                logger.info("User chose hard delete for user: {}", getLoginUserName());
                dataDeletion.deleteData(getLoginUserName(), DeleteType.hard);
                System.out.println("Hard delete initiated for user: " + getLoginUserName());
                break;
            default:
                logger.warn("User made an invalid choice for data deletion");
                System.out.println("Invalid choice.");
        }
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
                logger.info("User chose to export data to file for user: {}", getLoginUserName());
                String fileName = dataExport.getPathOfProcessedData(getLoginUserName());
                System.out.println("Data exported to file: " + fileName);
                break;
            case 2:
                logger.info("User chose to upload data to cloud for user: {}", getLoginUserName());
                String cloudLink = dataExport.exportAndUploadData(getLoginUserName(), "GoogleDrive");
                System.out.println("Data uploaded to cloud. This is the Link: " + cloudLink);
                break;
            default:
                logger.warn("User made an invalid choice");
                System.out.println("Invalid choice.");
        }
        long endTime = System.currentTimeMillis();
        logger.info("Data export for user completed in {} ms", (endTime - startTime));



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
