package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import model.Viewer;
import model.Comment;

/**
 * Data Access Object for the Viewer table.
 * Encapsulates all of the relevant SQL commands.
 * Based on Sciore, Section 9.1.
 *
 * @author Muhammad Haroon, Cheyne Funakoshi
 */
public class ViewerDAO {
        private Connection conn;
        private DatabaseManager dbm;

        public ViewerDAO(Connection conn, DatabaseManager dbm) {
                this.conn = conn;
                this.dbm = dbm;
        }

        /**
         * Create the Viewer table via SQL
         *
         * @param conn
         * @throws SQLException
         */
        static void create(Connection conn) throws SQLException {
                Statement stmt = conn.createStatement();
                String s = "create table VIEWER("
                				+ "viewerId integer, "
                                + "fullName varchar(100) not null, "
                                + "email varchar(100) not null, "
                                + "primary key(viewerId))";
                stmt.executeUpdate(s);
        }

        /**
         * Modify the Viewer table to add foreign key constraints (needs to happen
         * after the other tables have been created)
         *
         * @param conn
         * @throws SQLException
         */
        static void addConstraints(Connection conn) throws SQLException {
        }

        /**
         * Retrieve a Viewer object given its key.
         *
         * @param viewerId
         */
        public Viewer find(int viewerId) {
    		try {
    			String qry = "select fullName, email from VIEWER where viewerId = ?";
    			PreparedStatement pstmt = conn.prepareStatement(qry);
    			pstmt.setInt(1, viewerId);
    			ResultSet rs = pstmt.executeQuery();

    			// return null if video doesn't exist
    			if (!rs.next())
    				return null;

    			String fullName = rs.getString("fullName");
    			String email = rs.getString("email");
    			rs.close();

    			Viewer viewer = new Viewer(this, viewerId, fullName, email);

    			return viewer;
    		} catch (SQLException e) {
    			dbm.cleanup();
    			throw new RuntimeException("error finding viewer", e);
    		}
    	}

    	/**
    	 * Add a new Viewer with the given attributes.
    	 * 
    	 * @param viewerId
    	 * @param fullName
    	 * @param email
    	 * @return the new Viewer object, or null if key already exists
    	 */
    	public Viewer insert(String fullName, String email, int viewerId) {
    		try {
    			if (find(viewerId) != null)
    				return null;

    			String cmd = "insert into VIEWER(viewerId, fullName, email) "
    					+ "values(?, ?, ?)";
    			PreparedStatement pstmt = conn.prepareStatement(cmd);
    			pstmt.setInt(1, viewerId);
    			pstmt.setString(2, fullName);
    			pstmt.setString(3, email);
    			pstmt.executeUpdate();

    			Viewer viewer = new Viewer(this, viewerId, fullName, email);

    			return viewer;
    		} catch (SQLException e) {
    			dbm.cleanup();
    			throw new RuntimeException("error inserting new viewer", e);
    		}
    	}

    	/**
    	 * fullName was changed in the model object, so propagate the change to the
    	 * database.
    	 * 
    	 * @param viewerId
    	 * @param fullName
    	 */
    	public void changeFullName(int viewerId, String fullName) {
    		try {
    			String cmd = "update VIEWER set fullName = ? where Viewer = ?";
    			PreparedStatement pstmt = conn.prepareStatement(cmd);
    			pstmt.setString(1, fullName);
    			pstmt.setInt(2, viewerId);
    			pstmt.executeUpdate();
    		} catch (SQLException e) {
    			dbm.cleanup();
    			throw new RuntimeException("error changing fullName", e);
    		}
    	}

    	
    	/**
    	 * Retrieve a Collection of all Comments in the given Viewer.
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
    			throw new RuntimeException("error getting Viewer comment", e);
    		}
    	}

    	/**
    	 * Clear all data from the Viewer table.
    	 * 
    	 * @throws SQLException
    	 */
    	void clear() throws SQLException {
    		Statement stmt = conn.createStatement();
    		String s = "delete from VIEWER";
    		stmt.executeUpdate(s);
    	}
    }