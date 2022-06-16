package com.memy.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StoryFeedData{

	@SerializedName("updated_on")
	private String updatedOn;

	@SerializedName("comments")
	private int comments;

	@SerializedName("publishas")
	private String publishas;

	@SerializedName("author")
	private int author;

	@SerializedName("profile")
	private int profile;

	@SerializedName("title")
	private String title;

	@SerializedName("content")
	private String content;

	@SerializedName("likes_count")
	private int likesCount;

	@SerializedName("created_on")
	private String created_on;

	@SerializedName("comments_count")
	private int commentsCount;

	@SerializedName("files")
	private List<FeedListFileObj> files;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	@SerializedName("views")
	private int views;

	@SerializedName("likes")
	private int likes;

	@SerializedName("status")
	private int status;

	@SerializedName("self_author")
	private FeedOwnerObj self_author;

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public String getPublishas() {
		return publishas;
	}

	public void setPublishas(String publishas) {
		this.publishas = publishas;
	}

	public int getAuthor() {
		return author;
	}

	public void setAuthor(int author) {
		this.author = author;
	}

	public int getProfile() {
		return profile;
	}

	public void setProfile(int profile) {
		this.profile = profile;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public String getCreatedOn() {
		return created_on;
	}

	public void setCreatedOn(String createdOn) {
		this.created_on = createdOn;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public List<FeedListFileObj> getFiles() {
		return files;
	}

	public void setFiles(List<FeedListFileObj> files) {
		this.files = files;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public FeedOwnerObj getSelf_author() {
		return self_author;
	}

	public void setSelf_author(FeedOwnerObj self_author) {
		this.self_author = self_author;
	}
}