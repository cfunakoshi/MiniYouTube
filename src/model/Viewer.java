package model;

import java.util.Collection;

import dao.ViewerDAO;

/**
 * Model object for a row in the Viewer table.
 * Accesses the underlying database through a ViewerDAO.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class Viewer {
	private ViewerDAO dao;
	private int viewerId;
	private String fullName;
	private String email;
	private Collection<Comment> comment;
	

	public Viewer(ViewerDAO dao, int viewerId, String fullName, String email) {
		this.dao = dao;
		this.viewerId = viewerId; 
		this.fullName = fullName;
		this.email = email;
	}
	
	public int getViewerId() {
		return viewerId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
		dao.changeFullName(viewerId, fullName);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setDescription(String email) {
		this.email = email;
	}
	
	public Collection<Comment> getComment() {
		if (comment == null) comment = dao.getComment(viewerId);
		return comment;
	}
}

