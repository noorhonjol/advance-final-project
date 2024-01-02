package payment;

import Events.CreationCollectEvent;
import Events.EventHandlerMethods;
import com.google.common.eventbus.Subscribe;
import iam.UserType;


import java.util.List;

public class EventDrivenPaymentService extends PaymentServiceDecorator{

    public EventDrivenPaymentService(IPayment payment) {
        super(payment);

    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent){
        if(collectEvent.getUserType()== UserType.REGULAR_USER||collectEvent.getUserType()==UserType.NEW_USER) {
            return;
        }

        List<Transaction> transactions=getTransactions(collectEvent.getUserName());

        if(transactions.isEmpty()){

            return;
        }
        EventHandlerMethods.handleUserDataEvent("payment-info",transactions,collectEvent.getUserName());
    }
//    @Subscribe
//    void handleDeleteEvent(DeleteEvent deleteEvent){
//
//        List<Transaction> transactions=getTransactions(deleteEvent.getUserName());
//
//        if(transactions.isEmpty()){
//            return;
//        }
//
//        transactions.clear();
//
//        EventHandlerMethods.handleUserDataEvent("payment-info","",deleteEvent.getUserName());
//    }
//


}
