package modreq;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TicketHandler {
	private java.sql.Connection conn;
	private java.sql.Statement stat;
	public static modreq plugin = (modreq) Bukkit.getPluginManager().getPlugin("ModReq");
	public final Logger logger = Logger.getLogger("Minecraft");

	public TicketHandler(){//create database if it does not yet exist
		try {
			Class.forName("org.sqlite.JDBC");
			if(plugin.getConfig().getBoolean("use-mysql")) {
				String ip = plugin.getConfig().getString("mysql.ip");
				String user = plugin.getConfig().getString("mysql.user"); 
				String pass = plugin.getConfig().getString("mysql.pass");
				
				conn = DriverManager.getConnection("jdbc:mysql://"+ip, user, pass);
				stat = conn.createStatement();
				stat.execute("CREATE TABLE IF NOT EXISTS requests (id INT, submitter TEXT, message TEXT, date TEXT, status TEXT, comment TEXT, location TEXT, staff TEXT)");
			}
			else {
			conn = DriverManager.getConnection("jdbc:sqlite:plugins/ModReq/DataBase.sql");
			stat = conn.createStatement();
			stat.execute("CREATE TABLE IF NOT EXISTS requests (id int, submitter String, message String, date String, status String, comment String, location String, staff String)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe("[ModReq] no connection could be made with the database. Shutting down plugin D:");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
	public int getTicketsFromPlayer(Player p, String target, String status) throws SQLException {//returns the amount of tickets send by a player
		ArrayList<Integer> tickets = new ArrayList<Integer>();
		ResultSet result = stat.executeQuery("SELECT * FROM requests WHERE submitter = '"+target+"' AND status = '"+status+"' limit 5");
		while(result.next()) {
			
				tickets.add(result.getInt(1));
			
		}
		int i=0;
		for(; i<tickets.size(); i++) {
			
		}
		return i;
	}
	public ArrayList<Ticket> getTicketsByPlayer(Player p, String target) throws SQLException{//returns an arraylist containing all the tickets that a player has submitted
		ArrayList<Integer> tickets = new ArrayList<Integer>();
		ArrayList<Ticket> value = new ArrayList<Ticket>();
		ResultSet result = stat.executeQuery("SELECT * FROM requests WHERE submitter = '"+target+"'");
		
		
		while(result.next()) {
			if(tickets.size() >= 5) {
				tickets.remove(0);
				tickets.add(result.getInt(1));
			}
			else {
				tickets.add(result.getInt(1));
			}	
			
		}
		int i=0;
		for(; i<tickets.size(); i++) {
			value.add(getTicket(tickets.get(i)));
		}
		return value;
	}
	public boolean hasClaimed(Player p) {
		try {
		ResultSet result = stat.executeQuery("SELECT * FROM requests WHERE staff = '"+p.getName()+"' AND status = '"+Status.CLAIMED.getStatusString()+"' limit 5");
		
			if(result.next()) {
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}
	public void sendPlayerPage(int page, String status, Player p) {//send the -----List-of-STATUS-Requests----- 
		try {
			ArrayList<Integer> tickets = new ArrayList<Integer>();
			int nmbr = page *10;
			ResultSet result;
			if(status.equalsIgnoreCase("open")) {
				if(plugin.getConfig().getBoolean("show-claimed-tickets-in-open-list") == true) {
					result = stat.executeQuery("SELECT * FROM requests WHERE status = 'open' or status = 'claimed' limit "+nmbr);
				}
				else {
					result = stat.executeQuery("SELECT * FROM requests WHERE status = '"+status+"' limit "+nmbr);
					}
			}
			else {
			result = stat.executeQuery("SELECT * FROM requests WHERE status = '"+status+"' limit "+nmbr);
			}
			while(result.next()) {
				if(result.getRow() > nmbr-10) {
					tickets.add(result.getInt(1));
				}
			}
			p.sendMessage(ChatColor.GOLD+"-----List-of-"+status+"-Requests-----");
			for(int i=0; i<tickets.size(); i++) {
				getTicket(tickets.get(i)).sendSummarytoPlayer(p);
			}
			p.sendMessage(ChatColor.GOLD + "do /check <page> to see more");
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		
		}	
	
	}    
    public int getTicketCount() {//get the total amount of tickets
		try {
			ResultSet rs = stat.executeQuery("SELECT id FROM requests ");
			int i = 0;
			while(rs.next()) {
			 i++;
			}
			rs.close();
			return i;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		
	}
    public int getTicketAmount(Status status) {
    	String statusString = status.getStatusString();
    	try {
			ResultSet rs = stat.executeQuery("SELECT id FROM requests WHERE status = '"+statusString+"'");
			int i = 0;
			while(rs.next()) {
			 i++;
			}
			rs.close();
			return i;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
    	
    }
	public void addTicket(String submitter, String message, String date, String status, String location) throws SQLException {//add a new ticket to the database
		PreparedStatement prep = conn.prepareStatement("INSERT INTO requests VALUES (?, ?, ?, ?, ?, ?,?,?)");
		prep.setInt(1, getTicketCount() +1);
		prep.setString(2, submitter);
		prep.setString(3, message);
		prep.setString(4, date);
		prep.setString(5, status);
		prep.setString(6, "no comments yet");
		prep.setString(7, location);
		prep.setString(8, "no staff member yet");
		prep.addBatch();
		
		//conn.setAutoCommit(false); 
        prep.executeBatch(); 
        //conn.setAutoCommit(true); 
		
	}
	public Ticket getTicket(int i) {//returns the Ticket WHERE id=i
			try {
				ResultSet result = stat.executeQuery("SELECT * FROM requests WHERE id = '"+i+"'");
				result.next();
				String status = result.getString(5);
				String submitter = result.getString(2);
				String date = result.getString(4);
				String location = result.getString(7);
				String message = result.getString(3);
				String comment = result.getString(6);
				String staff = result.getString(8);
				Ticket ticket = new Ticket(plugin,i, submitter, message, date, Status.getByString(status), comment,location,staff);
				result.close();
				return ticket;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			
			}	
			
		return null;
		
	}
	public void updateTicket(Ticket t) throws SQLException {//updates the status, staff AND comment of tickt t
		int id = t.getId();
		PreparedStatement prep = conn.prepareStatement("UPDATE requests SET status = ?, staff = ?, comment = ? WHERE id = "+id+"");
		
		String status = t.getStatus().getStatusString();
		String comment = t.getComment();
		String staff = t.getStaff();
		
		prep.setString(1, status);
		prep.setString(2, staff);
		prep.setString(3, comment);
		prep.addBatch();
		prep.executeBatch();
	}
	public int getOpenTicketsAmount() {
		int i = 0;
			try {
				ResultSet result = stat.executeQuery("SELECT id FROM requests WHERE status = 'open'");
				while(result.next()) {
					i++;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			
			}	
		return i;
	}

}
