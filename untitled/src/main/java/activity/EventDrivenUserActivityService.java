package activity;

import Events.CreationCollectEvent;

import Events.DeleteEvent;
import com.google.common.eventbus.Subscribe;


import java.util.List;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import iam.UserType;

public class EventDrivenUserActivityService extends UserActivityServiceDecorator {


    public EventDrivenUserActivityService(IUserActivityService userActivityService) {
        super(userActivityService);

    }

    @Override
    void update(String userId, String activityId,UserActivity newData) throws SystemBusyException, BadRequestException, NotFoundException {
        List<UserActivity> userActivities=getUserActivity(userId);
        if(userActivities.isEmpty()){
            return;
        }
        for (UserActivity userActivity: userActivities) {
            if(userActivity.getId().equals(activityId)){
                userActivities.remove(userActivity);
                break;
            }
        }
        userActivities.add(newData);
        EventHandlerMethods.handleUserDataEvent("userActivities",userActivities,userId);
    }
    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {

        if(collectEvent.getUserType()== UserType.NEW_USER) {
            EventHandlerMethods.handleUserDataEvent("userActivities",new Object(),collectEvent.getUserName());
            return;
        }
        List<UserActivity> userActivities=getUserActivity(collectEvent.getUserName());

        if(userActivities.isEmpty()){

            return;
        }
        EventHandlerMethods.handleUserDataEvent("userActivities",userActivities,collectEvent.getUserName());
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {

        List<UserActivity> userActivities=getUserActivity(deleteEvent.getUserName());

        if(userActivities.isEmpty()){
            return;
        }

        userActivities.clear();

        EventHandlerMethods.handleUserDataEvent("userActivities",new Object(),deleteEvent.getUserName());
    }




}
