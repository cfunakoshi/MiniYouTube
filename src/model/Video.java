package model;

import java.util.Collection;

import dao.VideoDAO;

/**
 * Model object for a row in the Video table.
 * Accesses the underlying database through a VideoDAO.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class Video {
	private VideoDAO dao;
	private int videoId;
	private String title;
	private String description;
	private Collection<Comment> comment;
	

	public Video(VideoDAO dao, int videoId, String title, String description) {
		this.dao = dao;
		this.videoId = videoId; 
		this.title = title;
		this.description = description;
	}
	
	public String toString() {
		return title;
	}
	
	public int getVideoId() {
		return videoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		dao.changeTitle(videoId, title);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Collection<Comment> getComment() {
		if (comment == null) comment = dao.getComment(videoId);
		return comment;
	}
}
