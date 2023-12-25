package posts;

import java.util.List;

public abstract class PostServiceDecorator implements IPostService{
    private final IPostService postService;
    public PostServiceDecorator(IPostService postService){
        this.postService=postService;
    }


    @Override
    public void addPost(Post post) {
        postService.addPost(post);
    }

    @Override
    public List<Post> getPosts(String author) {
        return postService.getPosts(author);
    }

    @Override
    public void deletePost(String author, String id) {
        postService.deletePost(author,id);
    }

    abstract public void updatePost(String id,Post newData);
}