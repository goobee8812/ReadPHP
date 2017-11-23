package com.example.administrator.readphp;

/**
 * Created by Administrator on 2017/11/22.
 */

public class Topics {
    private String postsCount;
    private String  id;
    private String name;

    public Topics(String postsCount, String id, String name) {
        this.postsCount = postsCount;
        this.id = id;
        this.name = name;
    }

    public String getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(String postsCount) {
        this.postsCount = postsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
