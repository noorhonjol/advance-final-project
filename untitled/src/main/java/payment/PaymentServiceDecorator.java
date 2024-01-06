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
        logger.info("Initiating payment for UserName: " + transaction.getUserName());
        try {
            payment.pay(transaction);
            double balance = getBalance(transaction.getUserName());
            EventHandlerMethods.handleUserDataEvent("payment-info", balance, transaction.getUserName());
            logger.info("Payment completed for UserName: " + transaction.getUserName() + ", New Balance: " + balance);
        } catch (Exception e) {
            logger.error("Error during payment process for UserName: " + transaction.getUserName(), e);
            throw new RuntimeException("Payment processing failed for UserName: " + transaction.getUserName(), e);
        }
    }

    @Override
    public double getBalance(String userName) {
        logger.info("Retrieving balance for UserName: " + userName);
        double balance = payment.getBalance(userName);
        logger.info("Balance retrieved for UserName: " + userName + ": " + balance);
        return balance;
    }

    @Override
    public void removeTransaction(String userName, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Removing transaction with ID: " + id + " for UserName: " + userName);
        try {
            payment.removeTransaction(userName, id);
            double balance = getBalance(userName);
            EventHandlerMethods.handleUserDataEvent("payment-info", balance, userName);
            logger.info("Transaction removed for UserName: " + userName + ", Updated Balance: " + balance);
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error removing transaction for UserName " + userName + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Transaction> getTransactions(String userName) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Fetching transactions for UserName: " + userName);
        try {
            List<Transaction> transactions = payment.getTransactions(userName);
            logger.info("Transactions fetched successfully for UserName: " + userName);
            return transactions;
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error getting transactions for UserName " + userName + ": " + e.getMessage(), e);
            throw e;
        }
    }

}