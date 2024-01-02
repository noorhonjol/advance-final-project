package posts;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import MessageQueue.IMessageQueue;
import MessageQueue.MockQueue;
import com.google.common.eventbus.Subscribe;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import payment.Transaction;

import java.util.List;
import java.util.logging.Logger;

public class EventDrivenPostService extends PostServiceDecorator {
    private static final Logger logger = Logger.getLogger(EventDrivenPostService.class.getName());
    private static final IMessageQueue eventQueue = MockQueue.getInstance();

    public EventDrivenPostService(IPostService postService) {
        super(postService);
    }

    @Override
    public void updatePost(String userId, String postId, Post newData) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            List<Post> posts = getPosts(userId);

            if (posts.isEmpty()) {
                throw new NotFoundException("No posts found for user: " + userId);
            }
            boolean postUpdated = false;
            for (Post post : posts) {
                if (post.getId().equals(postId)) {
                    post.setAuthor(newData.getAuthor());
                    post.setTitle(newData.getTitle());
                    post.setBody(newData.getBody());
                    post.setDate(newData.getDate());
                    postUpdated = true;
                    break;
                }
            }

            if (!postUpdated) {
                throw new NotFoundException("Post not found with ID: " + postId);
            }

            EventHandlerMethods.handleUserDataEvent("posts", posts, newData.getAuthor());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.severe("Error in updatePost: " + e.getMessage());
            throw e;
        }
    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            List<Post> posts = getPosts(collectEvent.getUserName());

            if (posts.isEmpty()) {
                throw new NotFoundException("No posts found for user: " + collectEvent.getUserName());
            }

            EventHandlerMethods.handleUserDataEvent("posts", posts, collectEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.severe("Error in CollectDataEvent: " + e.getMessage());
            throw e;
        }
    }

    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            List<Post> posts = getPosts(deleteEvent.getUserName());

            if (posts.isEmpty()) {
                throw new NotFoundException("No posts found for user: " + deleteEvent.getUserName());
            }

            posts.clear();

            EventHandlerMethods.handleUserDataEvent("posts", new Object(), deleteEvent.getUserName());
        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.severe("Error in handleDeleteEvent: " + e.getMessage());
            throw e;
        }
    }
}
