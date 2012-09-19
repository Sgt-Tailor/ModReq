package modreq;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TicketHandler {
	private java.sql.Connection conn;
	private java.sql.Statement stat;
	public static modreq plugin;
	public final Logger logger = Logger.getLogger("Minecraft");

	public TicketHandler(){//create database if it does not yet exist
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:plugins/ModReq/DataBase.sql");
			stat = conn.createStatement();
			stat.execute("create table if not exists `requests` (id int, submitter string, message string, date string, status string, comment string , location string, staff string)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int getTicketsFromPlayer(Player p, String target, String status) throws SQLException {
		ArrayList<Integer> tickets = new ArrayList<Integer>();
		ResultSet result = stat.executeQuery("select * from 'requests' where submitter = '"+target+"' and status = '"+status+"' limit 5");
		while(result.next()) {
			
				tickets.add(result.getInt(1));
			
		}
		int i=0;
		for(; i<tickets.size(); i++) {
			
		}
		return i;
	}
	public ArrayList<Ticket> getTicketsByPlayer(Player p, String target) throws SQLException{
		ArrayList<Integer> tickets = new ArrayList<Integer>();
		ArrayList<Ticket> value = new ArrayList<Ticket>();
		ResultSet result = stat.executeQuery("select * from 'requests' where submitter = '"+target+"'");
		
		
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
	public void sendPlayerPage(int page, String status, Player p) {
		try {
			ArrayList<Integer> tickets = new ArrayList<Integer>();
			int nmbr = page *10;
			ResultSet result = stat.executeQuery("select * from 'requests' where status = '"+status+"' limit "+nmbr);
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
    public int getTicketCount() {
		try {
			ResultSet rs = stat.executeQuery("select id from 'requests' ");
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
	public void addTicket(String submitter, String message, String date, String status, String location) throws SQLException {
		PreparedStatement prep = conn.prepareStatement("INSERT INTO `requests` VALUES (?, ?, ?, ?, ?, ?,?,?)");
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
	public Ticket getTicket(int i) {//int idp, String submitt, String messa, String date, String status, String comment
			try {
				ResultSet result = stat.executeQuery("select * from 'requests' where id = "+i);
				result.next();
				String status = result.getString(5);
				String submitter = result.getString(2);
				String date = result.getString(4);
				String location = result.getString(7);
				String message = result.getString(3);
				String comment = result.getString(6);
				String staff = result.getString(8);
				Ticket ticket = new Ticket(i, submitter, message, date, status, comment,location,staff);
				result.close();
				return ticket;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			
			}	
			
		return null;
		
	}
	public void updateTicket(Ticket t) throws SQLException {
		int id = t.getId();
		PreparedStatement prep = conn.prepareStatement("UPDATE `requests` set status = ?, staff = ?, comment = ? where id = '"+id+"'");
		
		String status = t.getStatus();
		String comment = t.getComment();
		String staff = t.getStaff();
		
		prep.setString(1, status);
		prep.setString(2, staff);
		prep.setString(3, comment);
		prep.addBatch();
		prep.executeBatch();
	}

}
