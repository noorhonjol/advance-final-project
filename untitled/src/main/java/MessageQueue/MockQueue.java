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
        eventBus.post(event);
        logger.info("Event produced: " + event.toString());
    }

    @Override
    public void consume(Object subscriber) {
        eventBus.register(subscriber);
        logger.info("Consumer registered: " + subscriber.toString());
    }

    @Override
    public void removeConsumer(Object subscriber) {
        eventBus.unregister(subscriber);
        logger.info("Consumer unregistered: " + subscriber.toString());
    }
}
