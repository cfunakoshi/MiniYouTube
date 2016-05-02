package model;

import dao.PlaylistDAO;
import model.Viewer;

/**
 * Model object for a row in the Playlist table.
 * Accesses the underlying database through a PlaylistDAO.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class Playlist {
	private PlaylistDAO dao;
	private int PlaylistId;
	private String title;
	private Viewer viewer;

	public Playlist(PlaylistDAO dao, int PlaylistId, String title, Viewer viewer) {
		this.dao = dao;
		this.PlaylistId = PlaylistId; 
		this.title= title;
		this.viewer = viewer;
	}

	public int getCommentId() {
		return PlaylistId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		dao.changeTitle(PlaylistId, title);
	}
	
	public Viewer getViewer() {
		return viewer;
	}
}
