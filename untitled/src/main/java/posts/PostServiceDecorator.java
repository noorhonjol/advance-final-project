package posts;

import Events.EventHandlerMethods;

import java.util.List;

public abstract class PostServiceDecorator implements IPostService{
    private final IPostService postService;
    public PostServiceDecorator(IPostService postService){
        this.postService=postService;
    }


    @Override
    public void addPost(Post post) {
        List<Post> posts=getPosts(post.getAuthor());

        posts.add(post);

        EventHandlerMethods.handleUserDataEvent("posts",posts,post.getAuthor());
    }

    @Override
    public List<Post> getPosts(String author) {
        return postService.getPosts(author);
    }

    @Override
    public void deletePost(String author, String id) {

        postService.deletePost(author,id);

        EventHandlerMethods.handleUserDataEvent("posts",getPosts(author),author);
    }

    abstract public void updatePost(String userId,String postId,Post newData);
}