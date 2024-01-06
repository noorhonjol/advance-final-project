package MessageQueue;

import org.bson.Document;

import java.util.List;
import java.util.Queue;

public interface IMessageQueue {
    void produce(Object event);
    void consume(Object subscriber);
    void removeConsumer(Object subscriber);

}