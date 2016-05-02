package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Playlist;
import model.Viewer;

/**
 * Data Access Object for the Playlist table.
 * Encapsulates all of the relevant SQL commands.
 * Based on Sciore, Section 9.1.
 * 
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class PlaylistDAO {
	private Connection conn;
	private DatabaseManager dbm;

	public PlaylistDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
	}

	/**
	 * Create the Playlist table via SQL
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table Playlist("
		+ "playlistId integer, "
		+ "title varchar (500) not null, "
		+ "viewer integer not null, "
		+ "primary key(playlistId))";
		stmt.executeUpdate(s);
	}

	/**
	 * Modify the Playlist table to add foreign key constraints (needs to happen
	 * after the other tables have been created)
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void addConstraints(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "alter table PLAYLIST add constraint fk_viewerplayist "
		+ "foreign key(viewer) references VIEWER on delete cascade";
		stmt.executeUpdate(s);
	}


	/**
	 * Retrieve a Playlist object given its key.
	 * 
	 * @param playlistId
	 * @return the Playlist object, or null if not found
	 */

	public Playlist find(int playlistId) {
		try {
			String qry = "select title, viewer from PLAYLIST where playlistId = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, playlistId);
			ResultSet rs = pstmt.executeQuery();

			// return null if course doesn't exist
			if (!rs.next())
				return null;

			String title = rs.getString("title");
			int viewerId = rs.getInt("viewer");
			rs.close();
			
			Viewer viewer = dbm.findViewer(viewerId);

			Playlist playlist = new Playlist(this, playlistId, title, viewer);

			return playlist;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding playlist", e);
		}
	}

	/**
	 * Add a new Playlist with the given attributes.
	 * 
	 * @param playlistId
	 * @param title
	 * @param viewer
	 * @return the new Playlist object, or null if key already exists
	 */
	public Playlist insert(int playlistId, String title, Viewer viewer ) {
		try {
			// make sure that the commentId is currently unused
			if (find(playlistId) != null)
				return null;

			String cmd = "insert into PLAYLIST(playlistId, title, viewer) "
			+ "values(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, playlistId);
			pstmt.setString(2, title);
			pstmt.setInt(3, viewer.getViewerId());
			pstmt.executeUpdate();

			Playlist playlist = new Playlist(this, playlistId, title, viewer);

			return playlist;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new playlist", e);
		}
	}

	/**
	 * Title was changed in the model object, so propagate the change to the
	 * database.
	 * 
	 * @param playlistId
	 * @param title
	 */
	public void changeTitle(int playlistId, String title) {
		try {
			String cmd = "update PLAYLIST set title = ? where playlistId = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, title);
			pstmt.setInt(2, playlistId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing title", e);
		}
	}


	/**
	 * Clear all data from the Playlist table.
	 * 
	 * @throws SQLException
	 */
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from PLAYLIST";
		stmt.executeUpdate(s);
	}
}
