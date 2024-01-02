package posts;

import Events.EventHandlerMethods;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import exceptions.SystemBusyException;

import java.util.List;

public abstract class PostServiceDecorator implements IPostService{
    private final IPostService postService;
    public PostServiceDecorator(IPostService postService){
        this.postService=postService;
    }


    @Override
    public void addPost(Post post) throws SystemBusyException, BadRequestException, NotFoundException {
        List<Post> posts=getPosts(post.getAuthor());

        posts.add(post);

        EventHandlerMethods.handleUserDataEvent("posts",posts,post.getAuthor());
    }

    @Override
    public List<Post> getPosts(String author) throws SystemBusyException, BadRequestException, NotFoundException {
        return postService.getPosts(author);
    }

    @Override
    public void deletePost(String author, String id) throws SystemBusyException, BadRequestException, NotFoundException {

        postService.deletePost(author,id);

        EventHandlerMethods.handleUserDataEvent("posts",getPosts(author),author);
    }

    abstract public void updatePost(String userId,String postId,Post newData) throws SystemBusyException, BadRequestException, NotFoundException;
}