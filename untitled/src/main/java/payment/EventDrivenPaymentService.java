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
        try {
            if (collectEvent.getUserType() == UserType.REGULAR_USER || collectEvent.getUserType() == UserType.NEW_USER) {
                EventHandlerMethods.handleUserDataEvent("payment-info", new Object(), collectEvent.getUserName());
                return;
            }
            List<Transaction> transactions = getTransactions(collectEvent.getUserName());
            if (transactions.isEmpty()) {
                return;
            }
            EventHandlerMethods.handleUserDataEvent("payment-info", transactions, collectEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during CollectDataEvent for user: " + collectEvent.getUserName(), e);
            throw e;
        }
    }

    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            List<Transaction> transactions = getTransactions(deleteEvent.getUserName());
            if (transactions.isEmpty()) {
                return;
            }
            transactions.clear();
            EventHandlerMethods.handleUserDataEvent("payment-info", new Object(), deleteEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.error("Error during handleDeleteEvent for user: " + deleteEvent.getUserName(), e);
            throw e;
        }
    }
}
