package posts;

import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;
import exceptions.Util;

import java.util.*;
import java.util.logging.Logger;

public class PostService implements IPostService {
    private static final Logger logger = Logger.getLogger(PostService.class.getName());
    private static final Map<String, List<Post>> posts = new HashMap<>();

    @Override
    public void addPost(Post post) {
        posts.computeIfAbsent(post.getAuthor(), key -> new ArrayList<>()).add(post);
        logger.info("Post added successfully for author: " + post.getAuthor());
    }

    @Override
    public List<Post> getPosts(String author) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            Util.validateUserName(author);
            if (!posts.containsKey(author)) {
                throw new NotFoundException("User does not exist");
            }
            logger.info("Retrieved posts for author: " + author);
            return posts.get(author);
        } catch (BadRequestException | NotFoundException e) {
            logger.severe("Error getting posts for author: " + author + "; Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletePost(String author, String id) throws SystemBusyException, BadRequestException, NotFoundException {
        try {
            Thread.sleep(100);
            Util.validateUserName(author);
            if (!posts.containsKey(author)) {
                throw new NotFoundException("User does not exist");
            }
            List<Post> authorPosts = posts.get(author);
            if (authorPosts != null) {
                Iterator<Post> iterator = authorPosts.iterator();
                while (iterator.hasNext()) {
                    Post post = iterator.next();
                    if (Objects.equals(post.getId(), id)) {
                        iterator.remove();
                        logger.info("Post with id " + id + " deleted for author: " + author);
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.severe("System busy or interrupted during delete operation for author: " + author + "; Error: " + e.getMessage());
            throw new SystemBusyException("System is busy or interrupted");
        } catch (BadRequestException | NotFoundException e) {
            logger.severe("Error deleting post for author: " + author + "; Error: " + e.getMessage());
            throw e;
        }
    }
}
