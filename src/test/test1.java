package test;


import dao.DatabaseManager;
import model.Video;
import model.Viewer;

public class test1 {

	public static void main(String[] args) {
		DatabaseManager dbm = new DatabaseManager();
		
		dbm.clearTables();
		
		Video first = dbm.insertVideo("Guy goes super saiyan", "Super Saiyan Kid", 1);
		Video second = dbm.insertVideo("Dora goes to find the half medallion", "Dora the Explorer", 2);
		Video third = dbm.insertVideo("idk", "WATCH THIS!!!", 3);

		Viewer cheyne = dbm.insertViewer(1, "Cheyne Funakoshi", "cheynefunakoshi@gmail.com");
		Viewer sarib = dbm.insertViewer(2, "Sarib Haroon", "saribharoon@gmail.com");
		Viewer fabian = dbm.insertViewer(3, "Fabian Herrera", "fabian@gmail.com");
		
		
		
		dbm.insertComment(1, "This is hilarious!", "04/15/2016", first, cheyne);
		dbm.insertComment(2, "Dora isn't real guys.", "03/22/2016", second, sarib);
		dbm.insertComment(3, "Swiper no swiping!", "01/1/2016", second, sarib );
		dbm.insertComment(4, "What am I wathcing right now...", "12/01/2015", third, fabian);
		dbm.insertComment(5, "Can I be a super saiyan too?", "04/10/2016", first, fabian);
		
		dbm.insertPlaylist(1, "MyPlaylist", cheyne);
		dbm.insertPlaylist(1, "PumpUp JAMZZZZ", sarib);
		dbm.insertPlaylist(1, "Shower songs", fabian);

		dbm.commit();
		
		dbm.close();
		
		System.out.println("Done");
		
	}

}
