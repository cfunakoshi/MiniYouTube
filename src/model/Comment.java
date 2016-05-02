package model;

import dao.CommentDAO;

/**
 * Model object for a row in the Comment table.
 * Accesses the underlying database through a CommentAO.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class Comment {
	private CommentDAO dao;
	private int commentId;
	private String content;
	private String created;
	private Video video;
	private Viewer viewer;

	public Comment(CommentDAO dao, int commentId, String content, String created, Video video, Viewer viewer) {
		this.dao = dao;
		this.commentId = commentId; 
		this.content = content;
		this.created = created;
		this.video = video;
		this.viewer = viewer;
	}
	
	public int getCommentId() {
		return commentId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		dao.changeContent(commentId, content);
	}
	
	public String getCreated() {
		return created;
	}
	
	public Video getVideo() {
		return video;
	}
	
	public Viewer getViewer() {
		return viewer;
	}
}

