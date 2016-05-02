package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Comment;
import model.Video;
import model.Viewer;

/**
 * Data Access Object for the Comment table.
 * Encapsulates all of the relevant SQL commands.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class CommentDAO {
	private Connection conn;
	private DatabaseManager dbm;

	public CommentDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
	}

	/**
	 * Create the Comment table via SQL
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table COMMENT("
				+ " commentId integer not null, "
				+ " video integer not null, "
				+ "viewer integer not null, "
				+ "content varchar(1000) not null, "
				+ "created varchar(20) not null, "
				+ "primary key(commentId))";
		stmt.executeUpdate(s);
	}

	/**
	 * Modify the Comment table to add foreign key constraints (needs to happen
	 * after the other tables have been created)
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void addConstraints(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "alter table COMMENT add constraint fk_viewercomment "
				+ "foreign key(viewer) references VIEWER on delete cascade";
		stmt.executeUpdate(s);
				s = "alter table COMMENT add constraint fk_videocomment "
					+"foreign key(video) references VIDEO on delete cascade";
		stmt.executeUpdate(s);
	}

	/**
	 * Retrieve a Comment object given its key.
	 * 
	 * @param commentId
	 * @return the Comment object, or null if not found
	 */
	public Comment find(int commentId) {
		try {
			String qry = "select commentId,content,created,video,viewer from COMMENT where commentId = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, commentId);
			ResultSet rs = pstmt.executeQuery();

			// return null if course doesn't exist
			if (!rs.next())
				return null;

			int cId = rs.getInt("commentId");
			String content = rs.getString("content");
			String created = rs.getString("created");
			int videoId = rs.getInt("video");
			int viewerId = rs.getInt("viewer");
			rs.close();
			
			Video video = dbm.findVideo(videoId);
			Viewer viewer = dbm.findViewer(viewerId);

			Comment comment = new Comment(this, cId, content, created, video, viewer);

			return comment;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding video", e);
		}
	}

	/**
	 * Add a new Comment with the given attributes.
	 * 
	 * @param commentId
	 * @param content
	 * @param created
	 * @param video
	 * @param viewer
	 * @return the new Comment object, or null if key already exists
	 */
	public Comment insert(int commentId, String content, String created, Video video, Viewer viewer ) {
		try {
			// make sure that the commentId is currently unused
			if (find(commentId) != null)
				return null;

			String cmd = "insert into COMMENT(commentId, content, created, video, viewer) "
					+ "values(?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, commentId);
			pstmt.setString(2, content);
			pstmt.setString(3, created);
			pstmt.setInt(4, video.getVideoId());
			pstmt.setInt(5, viewer.getViewerId());
			pstmt.executeUpdate();

			Comment comment = new Comment(this, commentId, content, created, video, viewer);

			return comment;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new course", e);
		}
	}

	/**
	 * Content was changed in the model object, so propagate the change to the
	 * database.
	 * 
	 * @param commentId
	 * @param content
	 */
	public void changeContent(int commentId, String content) {
		try {
			String cmd = "update COMMENT set Content = ? where commentId = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, content);
			pstmt.setInt(2, commentId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing title", e);
		}
	}


	/**
	 * Clear all data from the Comment table.
	 * 
	 * @throws SQLException
	 */
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from VIDEO";
		stmt.executeUpdate(s);
	}
}
