package activity;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import com.google.common.eventbus.Subscribe;


import java.util.List;

import Events.EventHandlerMethods;
import iam.UserType;

public class EventDrivenUserActivityService extends UserActivityServiceDecorator {
    public EventDrivenUserActivityService(IUserActivityService userActivityService) {
        super(userActivityService);
    }

    @Override
    void update(String userId, UserActivity newData) {


    }
    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent){
        if(collectEvent.getUserType()== UserType.NEW_USER) {
            return;
        }
        List<UserActivity> userActivities=getUserActivity(collectEvent.getUserName());

        if(userActivities.isEmpty()){

            return;
        }

        EventHandlerMethods.handleUserDataEvent("userActivity",userActivities,collectEvent.getUserName());
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent){

        List<UserActivity> userActivities=getUserActivity(deleteEvent.getUserName());

        if(userActivities.isEmpty()){
            return;
        }

        userActivities.clear();

        EventHandlerMethods.handleUserDataEvent("userActivity","",deleteEvent.getUserName());
    }




}
