package MessageQueue;

import com.google.common.eventbus.EventBus;
import org.bson.Document;

import java.util.*;

public class MockQueue implements IMessageQueue {
    private static MockQueue instance = null;
    private final EventBus eventBus;

    private MockQueue() {
        eventBus = new EventBus();
    }

    public static synchronized MockQueue getInstance() {
        if (instance == null) {
            instance = new MockQueue();
        }
        return instance;
    }

    @Override
    public void produce(Object event) {
        eventBus.post(event);
    }

    @Override
    public void consume(Object subscriber) {
        eventBus.register(subscriber);
    }

    @Override
    public void removeConsumer(Object subscriber) {
        eventBus.unregister(subscriber);
    }


}
