package payment;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import com.google.common.eventbus.Subscribe;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import iam.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventDrivenPaymentService extends PaymentServiceDecorator {
    private static final Logger logger = LoggerFactory.getLogger(EventDrivenPaymentService.class);

    public EventDrivenPaymentService(IPayment payment) {
        super(payment);
    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Processing collect from payment for UserName: " + collectEvent.getUserName());
        try {
            if (collectEvent.getUserType() == UserType.REGULAR_USER || collectEvent.getUserType() == UserType.NEW_USER) {
                EventHandlerMethods.handleUserDataEvent("payment-info", new Object(), collectEvent.getUserName());
                logger.info("Data event handled for new or regular user: " + collectEvent.getUserName());
            } else {
                List<Transaction> transactions = getTransactions(collectEvent.getUserName());
                if (transactions.isEmpty()) {
                    logger.info("No transactions found for user: " + collectEvent.getUserName() + ". No action needed.");
                    return;
                }
                EventHandlerMethods.handleUserDataEvent("payment-info", transactions, collectEvent.getUserName());
                logger.info("Data event handled with transactions for user: " + collectEvent.getUserName());
            }
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during CollectDataEvent for UserName: " + collectEvent.getUserName() + ": " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unhandled error during CollectDataEvent for UserName: " + collectEvent.getUserName() + ": " + e.getMessage(), e);
            throw new RuntimeException("Unexpected error during data collection: " + e.getMessage(), e);
        }
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Processing delete data from payment info for UserName: " + deleteEvent.getUserName()+"in payment service");
        try {
            List<Transaction> transactions = getTransactions(deleteEvent.getUserName());
            if (transactions.isEmpty()) {
                logger.info("No transactions to delete for user: " + deleteEvent.getUserName());
                return;
            }
            transactions.clear();
            logger.info("Transactions cleared for user: " + deleteEvent.getUserName());
            EventHandlerMethods.handleUserDataEvent("payment-info", new Object(), deleteEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during handleDeleteEvent for UserName: " + deleteEvent.getUserName(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unhandled error during handleDeleteEvent for UserName: " + deleteEvent.getUserName(), e);
            throw new RuntimeException("Unexpected error during delete event: " + e.getMessage(), e);
        }
    }

}
