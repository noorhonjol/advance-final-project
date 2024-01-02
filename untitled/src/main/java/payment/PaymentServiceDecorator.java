package payment;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class PaymentServiceDecorator implements IPayment {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceDecorator.class);
    private final IPayment payment;

    public PaymentServiceDecorator(IPayment payment) {
        this.payment = payment;
    }

    @Override
    public void pay(Transaction transaction) {
        payment.pay(transaction);
        EventHandlerMethods.handleUserDataEvent("payment-info", getBalance(transaction.getUserName()), transaction.getUserName());
    }

    @Override
    public double getBalance(String userName) {
        return payment.getBalance(userName);
    }

    @Override
    public void removeTransaction(String userName, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            payment.removeTransaction(userName, id);
            EventHandlerMethods.handleUserDataEvent("payment-info", getBalance(userName), userName);
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error removing transaction for user " + userName + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Transaction> getTransactions(String userName) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            return payment.getTransactions(userName);
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error getting transactions for user " + userName + ": " + e.getMessage(), e);
            throw e;
        }
    }
}
