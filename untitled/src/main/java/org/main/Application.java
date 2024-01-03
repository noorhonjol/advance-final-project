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

    private static final int MAX_ATTEMPTS = 3;
    private static final String CLOUD_STORAGE = "GoogleDrive";
    private static final EventDrivenPaymentService paymentServiceWithEvent=new EventDrivenPaymentService(paymentService);
    private static final EventDrivenPostService postServiceWithEvent=new EventDrivenPostService(postService);
    private static final EventDrivenUserProfileService userServiceWithEvent=new EventDrivenUserProfileService(userService);
    private static final EventDrivenUserActivityService activityServiceWithEvent= new EventDrivenUserActivityService(userActivityService);
    private static final DataCreation creation=new DataCreation();
    private static final DataCollect dataCollector = new DataCollect();
    private static final DataExport dataExport=new DataExport(dataCollector);
    private static final IMessageQueue messageQueue= MockQueue.getInstance();

    private static final DataDeletion dataDeletion=new DataDeletion();
    private static final Scanner scanner=new Scanner(System.in);


    public static void main(String[] args) {
        generateRandomData();

        logger.info("Application Started: ");
        Instant start = Instant.now();
        System.out.println("Application Started: " + start);


        long startTime = System.currentTimeMillis();

        //TODO Your application starts here. Do not Change the existing code

        try {
            initializeMessageQueue();


            processUserRequest(requestUsername());


            exportWithRetry();

//            deleteMenu();

        }  catch (NotFoundException | InterruptedException e) {
            logger.error("Something went wrong: " + e.getMessage());
            return;
        }

        long endTime = System.currentTimeMillis();

        logger.info("Data export for user completed in {} ms", (endTime - startTime));

        //TODO Your application ends here. Do not Change the existing code

        logger.info("Application Ended: ");
    }
    private static void initializeMessageQueue() {
        messageQueue.consume(paymentServiceWithEvent);
        messageQueue.consume(postServiceWithEvent);
        messageQueue.consume(userServiceWithEvent);
        messageQueue.consume(activityServiceWithEvent);
        messageQueue.consume(dataCollector);
        messageQueue.consume(creation);    }

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

    public static void deleteMenu() {

        System.out.println("How do you want to delete your data:");

        System.out.println("1. Soft delete ");
        System.out.println("2. Hard delete ");
        System.out.print("Enter your choice: ");

        Scanner scanner = new Scanner(System.in);
        int deleteChoice = scanner.nextInt();


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

    }

    private static String requestUsername() {
        System.out.println("Enter your username: ");
        System.out.println("Note: You can use any of the following usernames: user0, user1, user2, user3, .... user99");
        return scanner.nextLine();
    }

    private static void processUserRequest(String userName) throws InterruptedException, NotFoundException {
        try {

            setLoginUserName(userName);
            if (!attemptDataProcessing()) {
                System.out.println("Failed to process after " + MAX_ATTEMPTS + " attempts.");

            }

        } catch (NotFoundException  e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
    private static boolean attemptDataProcessing() throws InterruptedException, NotFoundException {
        int attempt = 0;
        while (attempt < MAX_ATTEMPTS) {
            try {
                UserProfile profile = userServiceWithEvent.getUser(getLoginUserName());
                creation.requestToCollectData(profile);
                return true;
            } catch (SystemBusyException  e) {
                System.out.println("System is busy, please try again.");
                Thread.sleep(1000);
                attempt++;
            } catch (BadRequestException e) {
                System.out.println("Bad request: " + e.getMessage());
                return false;
            }catch (NotFoundException e) {
                System.out.println("User not found: " + e.getMessage());
                throw e;
            }
        }
        return false;
    }

    private static void exportDataToFile() throws SystemBusyException, NotFoundException {
        logger.info("User chose to export data to file for user: {}", getLoginUserName());
        String fileName = dataExport.getPathOfProcessedData(getLoginUserName());
        System.out.println("Data exported to file: " + fileName);
    }

    private static void uploadDataToCloud() {
        logger.info("User chose to upload data to cloud for user: {}", getLoginUserName());
        String cloudLink = dataExport.exportAndUploadData(getLoginUserName(), CLOUD_STORAGE);
        System.out.println("Data uploaded to cloud. This is the Link: " + cloudLink);
    }
    private static void exportWithRetry() throws InterruptedException {
        System.out.println("How do you want to get your Data:");
        System.out.println("1. Export data and download directly");
        System.out.println("2. Upload data to cloud storage and get a link.");
        int choice = scanner.nextInt();

        int attempt = 0;
        while (attempt < MAX_ATTEMPTS) {
            try {

                if(choice==1) {
                    exportDataToFile();
                } else if (choice==2) {
                    uploadDataToCloud();
                }
                return;
            } catch (SystemBusyException e) {
                System.out.println("System is busy, please try again.");
                Thread.sleep(1000);
                attempt++;
            } catch (NotFoundException e) {
                System.out.println("User not found: " + e.getMessage());
                return;
            }
        }
    }



}