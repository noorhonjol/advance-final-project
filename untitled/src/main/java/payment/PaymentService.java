package payment;

import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import exceptions.Util;
import java.util.*;
import java.util.logging.Logger;

public class PaymentService implements IPayment {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
    private static final Map<String, List<Transaction>> transactionMap = new HashMap<>();

    @Override
    public void pay(Transaction transaction) {
        try {
            transactionMap.computeIfAbsent(transaction.getUserName(), key -> new ArrayList<>()).add(transaction);
        } catch (Exception e) {
            logger.severe("Error during payment: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getBalance(String userName) {
        try {
            if (transactionMap.containsKey(userName)) {
                return transactionMap.get(userName).stream().mapToDouble(Transaction::getAmount).sum();
            }
            return 0;
        } catch (Exception e) {
            logger.severe("Error getting balance for user: " + userName + ". Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeTransaction(String userName, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            Thread.sleep(100);
            Util.validateUserName(userName);
            if (!transactionMap.containsKey(userName)) {
                throw new NotFoundException("User does not exist");
            }
            Iterator<Transaction> iterator = transactionMap.get(userName).iterator();
            while (iterator.hasNext()) {
                Transaction transaction = iterator.next();
                if (transaction.getId().equals(id)) {
                    iterator.remove();
                }
            }
        } catch (InterruptedException e) {
            logger.warning("Thread interrupted during removeTransaction: " + e.getMessage());
            throw new SystemBusyException("System is busy, try again later");
        } catch (BadRequestException | NotFoundException e) {
            logger.warning("Error during removeTransaction for user: " + userName + ": " + e.getMessage());
            throw e;
        }
    }
    @Override
    public List<Transaction> getTransactions(String userName) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            Util.validateUserName(userName);
            if (!transactionMap.containsKey(userName)) {
                throw new NotFoundException("User does not exist");
            }
            return transactionMap.get(userName);
        } catch (BadRequestException | NotFoundException e) {
            logger.warning("Error during getTransactions for user: " + userName + ": " + e.getMessage());
            throw e;
        }
    }
}
