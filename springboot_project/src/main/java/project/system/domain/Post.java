package project.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/*
@author WL
@CreateDate 2020-7-21
@update
@description 公众号文章实体类
*/
@ApiModel("公众号文章")
public class Post {
    @ApiModelProperty("文章id")
    private Integer postId;
    @ApiModelProperty("文章所属公众号id")
    private Integer publicaccountId;
    @ApiModelProperty("文章所属公众号的名字")
    private String publicaccountName;
    @ApiModelProperty("文章发布日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postDate;
    @ApiModelProperty("文章附带图片")
    private String postImage;
    @ApiModelProperty("文章列表展示的简介")
    private String postBrief;
    @ApiModelProperty("文章标题")
    private String postTitle;
    @ApiModelProperty("文章内容")
    private String postBody;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getPublicaccountId() {
        return publicaccountId;
    }

    public void setPublicaccountId(Integer publicaccountId) {
        this.publicaccountId = publicaccountId;
    }

    public String getPublicaccountName() {
        return publicaccountName;
    }

    public void setPublicaccountName(String publicaccountName) {
        this.publicaccountName = publicaccountName == null ? null : publicaccountName.trim();
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage == null ? null : postImage.trim();
    }

    public String getPostBrief() {
        return postBrief;
    }

    public void setPostBrief(String postBrief) {
        this.postBrief = postBrief == null ? null : postBrief.trim();
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle == null ? null : postTitle.trim();
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody == null ? null : postBody.trim();
    }
}
