package posts;


import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import payment.Transaction;

import java.util.List;

public class EventDrivenPostService extends PostServiceDecorator {
    private static final IMessageQueue eventQueue= MockQueue.getInstance();

    public EventDrivenPostService(IPostService postService) {
        super(postService);
    }

    @Override
    public void updatePost(String userId,String postId, Post newData) {
        List<Post> posts=getPosts(userId);

        if(posts.isEmpty()){
            return;
        }
        for(Post post:posts){
            if(post.getId().equals(postId)){
                post.setAuthor(newData.getAuthor());
                post.setTitle(newData.getTitle());
                post.setBody(newData.getBody());
                post.setTitle(newData.getTitle());
                post.setDate(newData.getDate());
                break;
            }
        }


        EventHandlerMethods.handleUserDataEvent("posts",posts,newData.getAuthor());

    }
    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent){

        List<Post> posts=getPosts(collectEvent.getUserName());

        if(posts.isEmpty()){
            return;
        }


        EventHandlerMethods.handleUserDataEvent("posts",posts,collectEvent.getUserName());
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent){

        List<Post> posts=getPosts(deleteEvent.getUserName());

        if(posts.isEmpty()){
            return;
        }

        posts.clear();

        EventHandlerMethods.handleUserDataEvent("posts","",deleteEvent.getUserName());
    }
}