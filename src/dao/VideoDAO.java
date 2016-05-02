package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import model.Video;
import model.Comment;

/**
 * Data Access Object for the Video table.
 * Encapsulates all of the relevant SQL commands.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class VideoDAO {
	private Connection conn;
	private DatabaseManager dbm;

	public VideoDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
	}

	/**
	 * Create the Video table via SQL
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table VIDEO("
				+ "videoId integer, "
				+ "title varchar(500) not null, "
				+ "description varchar(1000) not null, "
				+ "primary key(videoId))";
		stmt.executeUpdate(s);
	}

	/**
	 * Modify the Video table to add foreign key constraints (needs to happen
	 * after the other tables have been created)
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void addConstraints(Connection conn) throws SQLException {
	}

	/**
	 * Retrieve a Video object given its key.
	 * 
	 * @param videoId
	 * @return the Video object, or null if not found
	 */
	public Video find(int videoId) {
		try {
			String qry = "select title from VIDEO where videoId = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, videoId);
			ResultSet rs = pstmt.executeQuery();

			// return null if video doesn't exist
			if (!rs.next())
				return null;

			String title = rs.getString("title");
			String description = rs.getString("description");
			rs.close();

			Video video = new Video(this, videoId, title, description);

			return video;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding video", e);
		}
	}

	/**
	 * Add a new Video with the given attributes.
	 * 
	 * @param description
	 * @param title
	 * @param videoId
	 * @return the new Video object, or null if key already exists
	 */
	public Video insert(String description, String title, int videoId) {
		try {
			if (find(videoId) != null)
				return null;

			String cmd = "insert into VIDEO(videoId, title, description) "
					+ "values(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, videoId);
			pstmt.setString(2, title);
			pstmt.setString(3, description);
			pstmt.executeUpdate();

			Video video = new Video(this, videoId, title, description);

			return video;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new video", e);
		}
	}

	/**
	 * Title was changed in the model object, so propagate the change to the
	 * database.
	 * 
	 * @param videoId
	 * @param title
	 */
	public void changeTitle(int videoId, String title) {
		try {
			String cmd = "update VIDEO set title = ? where videoId = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, title);
			pstmt.setInt(2, videoId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing title", e);
		}
	}

	
	/**
	 * Retrieve a Collection of all Comment in the given Video.
	 * 
	 * @param commentId
	 * @return the Collection
	 */
	public Collection<Comment> getComment(int commentId) {
		try {
			Collection<Comment> comment = new ArrayList<Comment>();
			String qry = "select commentId from COMMENT where commentId = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, commentId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int cId = rs.getInt("commentId");
				comment.add(dbm.findComment(cId));
			}
			rs.close();
			return comment;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error getting video's comment", e);
		}
	}

	/**
	 * Clear all data from the Video table.
	 * 
	 * @throws SQLException
	 */
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from VIDEO";
		stmt.executeUpdate(s);
	}
}