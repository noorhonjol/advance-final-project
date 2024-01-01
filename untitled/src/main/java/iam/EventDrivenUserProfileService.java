package iam;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import com.google.common.eventbus.Subscribe;
import dataDeletion.DeleteType;

public class EventDrivenUserProfileService extends UserProfileServiceDecorator {
    public EventDrivenUserProfileService(IUserService userService) {
        super(userService);
    }
    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent){

        UserProfile userProfile=getUser(collectEvent.getUserName());

        if(userProfile==null){
            return;
        }

        EventHandlerMethods.handleUserDataEvent("user-profile",userProfile,collectEvent.getUserName());
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent){

        if(deleteEvent.getDeleteType()== DeleteType.soft){
            return;
        }
        deleteUser(deleteEvent.getUserName());
        EventHandlerMethods.handleUserDataEvent("user-profile","",deleteEvent.getUserName());
    }

}