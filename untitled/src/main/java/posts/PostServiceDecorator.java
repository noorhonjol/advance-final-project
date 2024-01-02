package posts;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class PostServiceDecorator implements IPostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceDecorator.class);
    private final IPostService postService;

    public PostServiceDecorator(IPostService postService) {
        this.postService = postService;
    }

    @Override
    public void addPost(Post post) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            List<Post> posts = getPosts(post.getAuthor());
            posts.add(post);
            EventHandlerMethods.handleUserDataEvent("posts", posts, post.getAuthor());
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error in addPost: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Post> getPosts(String author) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            return postService.getPosts(author);
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error in getPosts: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deletePost(String author, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            postService.deletePost(author, id);
            EventHandlerMethods.handleUserDataEvent("posts", getPosts(author), author);
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error in deletePost: " + e.getMessage(), e);
            throw e;
        }
    }

    abstract public void updatePost(String userId, String postId, Post newData) throws SystemBusyException, BadRequestException, NotFoundException;
}
