package MessageQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockQueue implements IMessageQueue {
    private static MockQueue instance;
    private final Map<String, List<Object>> queues = new HashMap<>();

    private MockQueue() {
    }

    public static synchronized MockQueue getInstance() {
        if (instance == null) {
            instance = new MockQueue();
        }
        return instance;
    }

    @Override
    public void produce(String queueName, Object message) {
        queues.computeIfAbsent(queueName, k -> new ArrayList<>()).add(message);
        System.out.println("Produced to " + queueName + ": " + message);
    }

    @Override
    public List<Object> consume(String queueName) {
        return queues.getOrDefault(queueName, new ArrayList<>());
    }
}
