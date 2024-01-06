package MessageQueue;

import com.google.common.eventbus.EventBus;
import java.util.logging.Logger;

public class MockQueue implements IMessageQueue {
    private static MockQueue instance = null;
    private final EventBus eventBus;
    private static final Logger logger = Logger.getLogger(MockQueue.class.getName());

    private MockQueue() {
        eventBus = new EventBus();
        logger.info("MockQueue initialized");
    }

    public static synchronized MockQueue getInstance() {
        if (instance == null) {
            instance = new MockQueue();
            logger.info("New instance of MockQueue created");
        }
        return instance;
    }

    @Override
    public void produce(Object event) {
        try {
            eventBus.post(event);
            logger.info("Event produced and posted to EventBus: " + event.getClass().getName());
        } catch (Exception e) {
            logger.severe("Error while producing event: " + e.getMessage() + ", Event: " + event.getClass().getName());
            throw new RuntimeException("Failed to produce event: " + event.getClass().getName(), e);
        }
    }
    @Override
    public void consume(Object subscriber) {
        try {
            eventBus.register(subscriber);
            logger.info("Consumer registered to EventBus: " + subscriber.getClass().getName());
        } catch (Exception e) {
            logger.severe("Error while registering consumer: " + e.getMessage() + ", Consumer: " + subscriber.getClass().getName());
            throw new RuntimeException("Failed to register consumer: " + subscriber.getClass().getName(), e);
        }
    }
    @Override
    public void removeConsumer(Object subscriber) {
        try {
            eventBus.unregister(subscriber);
            logger.info("Consumer unregistered from EventBus: " + subscriber.getClass().getName());
        } catch (Exception e) {
            logger.severe("Error while unregistering consumer: " + e.getMessage() + ", Consumer: " + subscriber.getClass().getName());
            throw new RuntimeException("Failed to unregister consumer: " + subscriber.getClass().getName(), e);
        }
    }

}