package payment;

import Events.EventHandlerMethods;

import java.util.List;

public abstract class PaymentServiceDecorator implements IPayment{
    private final IPayment payment;
    public  PaymentServiceDecorator(IPayment payment){

        this.payment=payment;
    }
    @Override
    public void pay(Transaction transaction) {
        payment.pay(transaction);
        EventHandlerMethods.handleUserDataEvent("payment-info",getBalance(transaction.getUserName()),transaction.getUserName());
    }

    @Override
    public double getBalance(String userName) {
        return payment.getBalance(userName);
    }

    @Override
    public void removeTransaction(String userName, String id) {
        payment.removeTransaction(userName,id);
        EventHandlerMethods.handleUserDataEvent("payment-info",getBalance(userName),userName);
    }

    @Override
    public List<Transaction> getTransactions(String userName) {
        return payment.getTransactions(userName);
    }

}
