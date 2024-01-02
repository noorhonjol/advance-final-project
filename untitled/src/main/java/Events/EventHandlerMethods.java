package Events;

import MessageQueue.MockQueue;
import com.google.gson.Gson;
import org.bson.Document;

public class EventHandlerMethods {

    public static void handleUserDataEvent(String documentKey, Object data, String userName) {

        Gson gson = new Gson();

        String json = gson.toJson(data);

        Document userData =Document.parse("{' "+ documentKey+" ': " + json + " }");

        userData.append("userName",userName);

        MockQueue.getInstance().produce(new UserDataEvent(userName,userData));

    }



}
