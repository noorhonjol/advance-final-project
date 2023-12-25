package payment;

import java.util.List;

public abstract class PaymentServiceDecorator implements IPayment{
    private final IPayment payment;
    public  PaymentServiceDecorator(IPayment payment){

        this.payment=payment;
    }
    @Override
    public void pay(Transaction transaction) {
        payment.pay(transaction);
    }

    @Override
    public double getBalance(String userName) {
        return payment.getBalance(userName);
    }

    @Override
    public void removeTransaction(String userName, String id) {
        payment.removeTransaction(userName,id);
    }

    @Override
    public List<Transaction> getTransactions(String userName) {
        return payment.getTransactions(userName);
    }

    public abstract void updatePayment(String userName,IPayment newData);

}
