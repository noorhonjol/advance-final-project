package posts;

import Events.CreationCollectEvent;
import Events.DeleteEvent;
import Events.EventHandlerMethods;
import com.google.common.eventbus.Subscribe;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;

import java.util.List;
import java.util.logging.Logger;

public class EventDrivenPostService extends PostServiceDecorator {
    private static final Logger logger = Logger.getLogger(EventDrivenPostService.class.getName());
    public EventDrivenPostService(IPostService postService) {
        super(postService);
    }

    @Override
    public void updatePost(String userId, String postId, Post newData) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Updating post. UserID: " + userId + ", PostID: " + postId);
        try {
            List<Post> posts = getPosts(userId);

            Post post = getPost(postId, posts);
            if (post == null) {
                logger.warning("No post found with PostID: " + postId + " for UserID: " + userId);
                throw new NotFoundException("No posts found for user: " + userId);
            }

            post.setAuthor(newData.getAuthor());
            post.setTitle(newData.getTitle());
            post.setBody(newData.getBody());
            post.setDate(newData.getDate());

            EventHandlerMethods.handleUserDataEvent("posts", posts, newData.getAuthor());
            logger.info("Post updated successfully for UserID: " + userId);

        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.severe("Error in updatePost for UserID: " + userId + ", PostID: " + postId + ": " + e.getMessage());
            throw e;
        }
    }


    private Post getPost(String postId, List<Post> posts) {
        for (Post post : posts) {
            if (post.getId().equals(postId)) {
                return post;
            }
        }
        return null;
    }

    @Subscribe
    void CollectDataEvent(CreationCollectEvent collectEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Handling CollectDataEvent for UserName: " + collectEvent.getUserName());
        try {
            List<Post> posts = getPosts(collectEvent.getUserName());

            if (posts.isEmpty()) {
                logger.warning("No posts found for UserName: " + collectEvent.getUserName());
                throw new NotFoundException("No posts found for user: " + collectEvent.getUserName());
            }

            EventHandlerMethods.handleUserDataEvent("posts", posts, collectEvent.getUserName());
            logger.info("CollectDataEvent handled successfully for UserName: " + collectEvent.getUserName());

        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.severe("Error in CollectDataEvent for UserName: " + collectEvent.getUserName() + ": " + e.getMessage());
            throw e;
        }
    }
    @Subscribe
    void handleDeleteEvent(DeleteEvent deleteEvent) throws SystemBusyException, BadRequestException, NotFoundException {
        logger.info("Handling DeleteEvent for UserName: " + deleteEvent.getUserName()+" in post service ");
        try {
            List<Post> posts = getPosts(deleteEvent.getUserName());

            if (posts.isEmpty()) {
                logger.warning("No posts found for UserName: " + deleteEvent.getUserName());
                throw new NotFoundException("No posts found for user: " + deleteEvent.getUserName());
            }

            posts.clear();
            EventHandlerMethods.handleUserDataEvent("posts", new Object(), deleteEvent.getUserName());
            logger.info("DeleteEvent handled, all posts cleared for UserName: " + deleteEvent.getUserName());

        } catch (BadRequestException | NotFoundException | SystemBusyException e) {
            logger.severe("Error in handleDeleteEvent for UserName: " + deleteEvent.getUserName() + ": " + e.getMessage());
            throw e;
        }
    }

}