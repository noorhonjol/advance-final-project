package MessageQueue;

import java.util.List;

public interface IMessageQueue<T> {

    void produce(String queueName,Object message);

    List<Object> consume(String queueName);

    //now its Object I will find solution for it in other time

}