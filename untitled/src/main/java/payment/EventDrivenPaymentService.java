package payment;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import com.google.common.eventbus.Subscribe;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import iam.UserType;


import java.util.List;

public class EventDrivenPaymentService extends PaymentServiceDecorator{

    public EventDrivenPaymentService(IPayment payment) {
        super(payment);

    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        if(collectEvent.getUserType()== UserType.REGULAR_USER||collectEvent.getUserType()==UserType.NEW_USER) {
            EventHandlerMethods.handleUserDataEvent("payment-info",new Object(),collectEvent.getUserName());

            return;
        }

        List<Transaction> transactions=getTransactions(collectEvent.getUserName());

        if(transactions.isEmpty()){

            return;
        }
        EventHandlerMethods.handleUserDataEvent("payment-info",transactions,collectEvent.getUserName());
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {

        List<Transaction> transactions=getTransactions(deleteEvent.getUserName());

        if(transactions.isEmpty()){
            return;
        }

        transactions.clear();

        EventHandlerMethods.handleUserDataEvent("payment-info",new Object(),deleteEvent.getUserName());
    }



}
