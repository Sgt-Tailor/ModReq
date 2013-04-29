/*
	Modreq Minecraft/Bukkit server ticket system
    Copyright (C) 2013 Sven Wiltink

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package modreq.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import modreq.Comment;
import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TicketHandler {
    public static ModReq plugin = ModReq.getInstance();
    public final Logger logger = Logger.getLogger("Minecraft");
    private Connection connection;

    private Connection getConnection() {
	try {
	   	if(connection != null) {
	   	    
	   	    if(connection.isClosed() == false) {
	   		logger.info("reusing old connection");
	   		return connection;
	   	    }
	   	}
	   	logger.info("creating new connection");
		Class.forName("org.sqlite.JDBC");
		if (plugin.getConfig().getBoolean("use-mysql")) {
		    String ip = plugin.getConfig().getString("mysql.ip");
		    String user = plugin.getConfig().getString("mysql.user");
		    String pass = plugin.getConfig().getString("mysql.pass");

		    connection = DriverManager.getConnection("jdbc:mysql://"
			    + ip, user, pass);
		    Statement stat = connection.createStatement();
		    stat.execute("CREATE TABLE IF NOT EXISTS requests (id INT, submitter TEXT, message TEXT, date TEXT, status TEXT, location TEXT, staff TEXT)");
		    stat.execute("CREATE TABLE IF NOT EXISTS comments (id INT, commenter TEXT, message TEXT, date TEXT)");
		    KillConnection();
		    return connection;
		} else {
		    connection = DriverManager
			    .getConnection("jdbc:sqlite:plugins/ModReq/DataBase.sql");
		    Statement stat = connection.createStatement();
		    stat.execute("CREATE TABLE IF NOT EXISTS requests (id int, submitter String, message String, date String, status String, location String, staff String)");
		    stat.execute("CREATE TABLE IF NOT EXISTS comments (id int, commenter String, message String, date String)");
		    KillConnection();
		    return connection;
		}

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.severe("[ModReq] no connection could be made with the database. Shutting down plugin D:");
	    plugin.getServer().getPluginManager().disablePlugin(plugin);
	    return null;
	}

    }

    private void KillConnection() {
	Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

	    @Override
	    public void run() {
		try {
		    logger.info("Killing connection");
		    connection.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }

	}, 2L);
    }

    public void clearTickets() {
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();
	    stat.execute("DROP TABLE requests");
	    stat.execute("DROP TABLE comments");
	} catch (Exception e) {

	}
    }

    public int getTicketsFromPlayer(Player p, String target, Status status)
	    throws SQLException {// returns the amount of tickets send by a
				 // player

	Connection conn = getConnection();
	Statement stat = conn.createStatement();

	ArrayList<Integer> tickets = new ArrayList<Integer>();
	ResultSet result = stat
		.executeQuery("SELECT * FROM requests WHERE submitter = '"
			+ target + "' AND status = '"
			+ status.getStatusString() + "'");
	while (result.next()) {

	    tickets.add(result.getInt(1));

	}
	int i = 0;
	for (; i < tickets.size(); i++) {

	}
	return i;
    }

    public ArrayList<Ticket> getTicketsByPlayer(Player p, String target)
	    throws SQLException {// returns an arraylist containing all the
				 // tickets that a player has submitted
	Connection conn = getConnection();
	Statement stat = conn.createStatement();
	ArrayList<Integer> tickets = new ArrayList<Integer>();
	ArrayList<Ticket> value = new ArrayList<Ticket>();
	ResultSet result = stat
		.executeQuery("SELECT * FROM requests WHERE submitter = '"
			+ target + "'");

	while (result.next()) {
	    if (tickets.size() >= 5) {
		tickets.remove(0);
		tickets.add(result.getInt(1));
	    } else {
		tickets.add(result.getInt(1));
	    }

	}
	int i = 0;
	for (; i < tickets.size(); i++) {
	    value.add(getTicketById(tickets.get(i)));
	}
	return value;
    }

    public boolean hasClaimed(Player p) {
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();

	    ResultSet result = stat
		    .executeQuery("SELECT * FROM requests WHERE staff = '"
			    + p.getName() + "' AND status = '"
			    + Status.CLAIMED.getStatusString() + "' limit 5");

	    if (result.next()) {
		return true;
	    }
	} catch (SQLException e) {
	}

	return false;
    }

    public void sendPlayerPage(int page, Status status, Player p) {// send the
								   // -----List-of-STATUS-Requests-----
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();
	    ArrayList<Integer> tickets = new ArrayList<Integer>();
	    int nmbr = page * 10;
	    ResultSet result;
	    if (status.getStatusString().equals("open")) {
		String a = "status = 'open'";
		if (plugin.getConfig().getBoolean(
			"show-claimed-tickets-in-open-list")) {
		    a = a + " or status = 'claimed'";
		}
		if (plugin.getConfig().getBoolean(
			"show-pending-tickets-in-open-list")) {
		    a = a + " or status = 'pending'";
		}
		result = stat.executeQuery("SELECT * FROM requests WHERE " + a
			+ " limit " + nmbr);

	    } else {
		result = stat
			.executeQuery("SELECT * FROM requests WHERE status = '"
				+ status.getStatusString() + "' limit " + nmbr);
	    }
	    while (result.next()) {
		if (result.getRow() > nmbr - 10) {
		    tickets.add(result.getInt(1));
		}
	    }
	    p.sendMessage(ChatColor.GOLD + "-----List-of-"
		    + status.getStatusString() + "-Requests-----");
	    for (int i = 0; i < tickets.size(); i++) {
		getTicketById(tickets.get(i)).sendSummarytoPlayer(p);
	    }
	    p.sendMessage(ChatColor.GOLD + "do /check <page> to see more");
	    return;
	} catch (SQLException e) {
	    e.printStackTrace();

	}

    }

    public int getTicketCount() {// get the total amount of tickets
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();
	    ResultSet rs = stat.executeQuery("SELECT id FROM requests ");
	    int i = 0;
	    while (rs.next()) {
		i++;
	    }
	    rs.close();
	    return i;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return 0;

    }

    public int getTicketAmount(Status status) {
	String statusString = status.getStatusString();
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();
	    ResultSet rs = stat
		    .executeQuery("SELECT id FROM requests WHERE status = '"
			    + statusString + "'");
	    int i = 0;
	    while (rs.next()) {
		i++;
	    }
	    rs.close();
	    return i;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return 0;

    }

    public void addTicket(String submitter, String message, String date,
	    Status status, String location) throws SQLException {// add a new
								 // ticket to
								 // the database
	Connection conn = getConnection();

	PreparedStatement prep = conn
		.prepareStatement("INSERT INTO requests VALUES (?, ?, ?, ?, ?,?,?)");
	prep.setInt(1, getTicketCount() + 1);
	prep.setString(2, submitter);
	prep.setString(3, message);
	prep.setString(4, date);
	prep.setString(5, status.getStatusString());
	prep.setString(6, location);
	prep.setString(7, "no staff member yet");
	prep.addBatch();

	prep.executeBatch();

    }

    public Ticket getTicketById(int i) {// returns the Ticket WHERE id=i
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();

	    ResultSet result = stat
		    .executeQuery("SELECT * FROM requests WHERE id = '" + i
			    + "'");

	    result.next();
	    String status = result.getString(5);
	    String submitter = result.getString(2);
	    String date = result.getString(4);
	    String location = result.getString(6);
	    String message = result.getString(3);
	    String staff = result.getString(7);
	    Ticket ticket = new Ticket(plugin, i, submitter, message, date,
		    Status.getByString(status), location, staff);

	    stat.close();
	    addCommentsToTicket(conn, ticket);
	    return ticket;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();

	}

	return null;

    }

    public void updateTicket(Ticket t) throws SQLException {// updates the
							    // status, staff AND
							    // comment of tickt
							    // t
	Connection conn = getConnection();

	int id = t.getId();
	PreparedStatement prep = conn
		.prepareStatement("UPDATE requests SET status = ?, staff = ? WHERE id = "
			+ id + "");

	String status = t.getStatus().getStatusString();
	String staff = t.getStaff();

	prep.setString(1, status);
	prep.setString(2, staff);
	prep.addBatch();
	prep.executeBatch();

	updateComments(conn, t);
    }

    public int getOpenTicketsAmount() {
	int i = 0;
	try {
	    Connection conn = getConnection();
	    Statement stat = conn.createStatement();
	    ResultSet result = stat
		    .executeQuery("SELECT id FROM requests WHERE status = 'open'");
	    while (result.next()) {
		i++;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();

	}
	return i;
    }

    private void addCommentsToTicket(Connection conn, Ticket t)
	    throws SQLException {
	Statement stat = conn.createStatement();
	ResultSet rs = stat.executeQuery("SELECT * FROM comments WHERE id = '"
		+ t.getId() + "'");
	while (rs.next()) {
	    String commenter = rs.getString(2);
	    String comment = rs.getString(3);
	    String date = rs.getString(4);

	    Comment c = new Comment(commenter, comment, date);
	    t.addComment(c);

	}
	rs.close();
	stat.close();
    }

    private void updateComments(Connection conn, Ticket t) throws SQLException {
	if (t.getComments().size() == 0) {
	    return;
	}
	PreparedStatement prep = conn
		.prepareStatement("INSERT INTO comments VALUES (?, ?, ?, ?)");
	Statement stat = conn.createStatement();
	ResultSet rs = stat.executeQuery("SELECT * FROM comments WHERE id = '"
		+ t.getId() + "'");
	Comment A = new Comment();
	while (rs.next()) {
	    String commenter = rs.getString(2);
	    String comment = rs.getString(3);
	    String date = rs.getString(4);

	    A = new Comment(commenter, comment, date);
	}
	stat.close();
	Comment B = t.getComments().get(t.getComments().size() - 1);
	if (A.isValid() == false) {
	    prep.setInt(1, t.getId());
	    prep.setString(2, B.getCommenter());
	    prep.setString(3, B.getComment());
	    prep.setString(4, B.getDate());
	    prep.addBatch();
	    prep.executeBatch();

	    return;
	}

	if (A.equalsComment(B)) {
	    return;
	}
	prep.setInt(1, t.getId());
	prep.setString(2, B.getCommenter());
	prep.setString(3, B.getComment());
	prep.setString(4, B.getDate());
	prep.addBatch();
	prep.executeBatch();
	return;

    }

}
