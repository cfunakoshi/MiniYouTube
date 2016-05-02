package dao;
	
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.derby.jdbc.EmbeddedDriver;

import model.Video;
import model.Comment;
import model.Playlist;
import model.Viewer;

/**
 * This class mediates access to the MiniYouTube database,
 * hiding the lower-level DAO objects from the client.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class DatabaseManager {
	private Driver driver;
	private Connection conn;
	private VideoDAO videoDAO;
	private CommentDAO commentDAO;
	private PlaylistDAO playlistDAO;
	private ViewerDAO viewerDAO;
	
	private final String url = "jdbc:derby:MiniYouTube";

	public DatabaseManager() {
		driver = new EmbeddedDriver();
		
		Properties prop = new Properties();
		prop.put("create", "false");
		
		// try to connect to an existing database
		try {
			conn = driver.connect(url, prop);
			conn.setAutoCommit(false);
		}
		catch(SQLException e) {
			// database doesn't exist, so try creating it
			try {
				prop.put("create", "true");
				conn = driver.connect(url, prop);
				conn.setAutoCommit(false);
				create(conn);
			}
			catch (SQLException e2) {
				throw new RuntimeException("cannot connect to database", e2);
			}
		}
		
		videoDAO = new VideoDAO(conn, this);
		commentDAO = new CommentDAO(conn, this);
		playlistDAO = new PlaylistDAO(conn, this);
		viewerDAO = new ViewerDAO(conn, this);
	}

	/**
	 * Initialize the tables and their constraints in a newly created database
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	private void create(Connection conn) throws SQLException {
		VideoDAO.create(conn);
		CommentDAO.create(conn);
		PlaylistDAO.create(conn);
		ViewerDAO.create(conn);
		VideoDAO.addConstraints(conn);
		CommentDAO.addConstraints(conn);
		PlaylistDAO.addConstraints(conn);
		ViewerDAO.addConstraints(conn);
		conn.commit();
	}

	//***************************************************************
	// Data retrieval functions -- find a model object given its key
	
	public Video findVideo(int videoId) {
		return videoDAO.find(videoId);
	}

	public Comment findComment(int commentId) {
		return commentDAO.find(commentId);
	}
	
	public Playlist findPlaylist(int playlistId) {
		return playlistDAO.find(playlistId);
	}
	
	public Viewer findViewer(int viewerId) {
		return viewerDAO.find(viewerId);
	}


	//***************************************************************
	// Data insertion functions -- create new model object from attributes
	
	public Video insertVideo(String description, String title, int videoId) {
		return videoDAO.insert(description, title, videoId);
	}

	public Comment insertComment(int commentId, String content, String created,  Video video, Viewer viewer) {
		return commentDAO.insert(commentId, content, created, video, viewer);
	}

	public Playlist insertPlaylist(int playlistId, String title, Viewer viewer) {
		return playlistDAO.insert(playlistId, title, viewer);
		
	}
	
	public Viewer insertViewer(int viewerId, String fullName, String email) {
		return viewerDAO.insert(fullName, email, viewerId);
		
	}
	
	

	//***************************************************************
	// Utility functions
	
	/**
	 * Commit changes since last call to commit
	 */
	public void commit() {
		try {
			conn.commit();
		}
		catch(SQLException e) {
			throw new RuntimeException("cannot commit database", e);
		}
	}

	/**
	 * Abort changes since last call to commit, then close connection
	 */
	public void cleanup() {
		try {
			conn.rollback();
			conn.close();
		}
		catch(SQLException e) {
			System.out.println("fatal error: cannot cleanup connection");
		}
	}

	/**
	 * Close connection and shutdown database
	 */
	public void close() {
		try {
			conn.close();
		}
		catch(SQLException e) {
			throw new RuntimeException("cannot close database connection", e);
		}
		
		// Now shutdown the embedded database system -- this is Derby-specific
		try {
			Properties prop = new Properties();
			prop.put("shutdown", "true");
			conn = driver.connect(url, prop);
		} catch (SQLException e) {
			// This is supposed to throw an exception...
			System.out.println("Derby has shut down successfully");
		}
	}

	/**
	 * Clear out all data from database (but leave empty tables)
	 */
	public void clearTables() {
		try {
			// This is not as straightforward as it may seem, because
			// of the cyclic foreign keys -- I had to play with
			// "on delete set null" and "on delete cascade" for a bit
			videoDAO.clear();
			commentDAO.clear();
			playlistDAO.clear();
			viewerDAO.clear();
		} catch (SQLException e) {
			throw new RuntimeException("cannot clear tables", e);
		}
	}
}