package modreq;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Ticket
{
	public int id;
	public String submitter;
	public String message;
	public String date;
	public String status;
	public String comment;
	public String location;
	public String staff;
	public TicketHandler tickets = new TicketHandler();
	
	public Ticket(int idp, String submitt, String messa, String date, String status, String comm, String loc, String sta)	{
		submitter = submitt;
		id = idp;
		staff = sta;
		this.date = date;
		message = messa;
		this.status = status;
		location = loc;
		comment = comm;
		
	}
	/**
	 * This is used to get the message that the sumbmitter send.
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * This is used to get the staff member's name that is currently working on the request.
	 * @return
	 */
	public String getStaff() {
		return staff;
	}
	/**
	 * This is used to get the name of the submitter
	 * @return
	 */
	public String getSubmitter() {
		return submitter;
	}
	/**
	 * This is used to get the date of the request
	 * @return
	 */
	public String getDate() {
		return date;
	}
	/**
	 * This is used to get the ticket id
	 * @return
	 */
	public int getId() {
		return id;
	}
	/**
	 * This is used to get the current status of the ticket
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * This is used to get the latest comment on the ticket
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * This is used to get the location of the request.
	 * The format is worldname x y z
	 * @return
	 */
	public Location getLocation() {
		String world = location.split(" ")[0];
		String x = location.split(" ")[1];
		String y = location.split(" ")[2];
		String z = location.split(" ")[3];
		World w = Bukkit.getServer().getWorld(world);
		double xx = Integer.parseInt(x);
		double yy = Integer.parseInt(y);
		double zz = Integer.parseInt(z);
		Location loc = new Location(w,xx,yy,zz);
		return loc;
	}
	/**
	 * This is used to send a the summary of a ticket to a player
	 * an example can be #id2 Mon 1 May Sgt_Tailor I need my house rolled back, ...
	 * @return
	 */
	public void sendSummarytoPlayer(Player p) {
		ChatColor namecolor = ChatColor.RED;
		Player[] list = Bukkit.getServer().getOnlinePlayers();
		int l = list.length;
		int n = 0;
		while(n<l){
			Player op = list[n];
			if(op.getName().equals(submitter)){
				if(op.isOnline()) {
					namecolor = ChatColor.GREEN;
				}
			}
			n++;
		}
		String summessage = message;
		if(summessage.length() > 15) {
			summessage = summessage.substring(0,15);
		}
		String summary;
		if(status.equalsIgnoreCase("claimed")) {
			summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+date+" "+namecolor+submitter+" "+ChatColor.GRAY+summessage+"..." + ChatColor.RED + " [Claimed]";
		}
		else {
			summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+date+" "+namecolor+submitter+" "+ChatColor.GRAY+summessage+"...";
		}
		
		p.sendMessage(summary);
	}
	/**
	 * This is used to send the status of a ticket to a player
	 * an example can be #3 [closed] I need my house rolled back, ...
	 * @return
	 */
	public void sendStatus(Player p) {
		String summessage = message;
		if(summessage.length() > 15) {
			summessage = summessage.substring(0,15);
		}
		String summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+date+ChatColor.DARK_GREEN+" ["+status+"]"+" "+ChatColor.GRAY+summessage+"...";
		p.sendMessage(summary);
	
	}
	/**
	 * This is used to send all the ticket info to a player
	 * @return
	 */
	public void sendMessageToPlayer(Player p) {
		p.sendMessage(ChatColor.GOLD + "---Info-about-ticket-#"+id+"---");
		p.sendMessage(ChatColor.AQUA + "status: " + ChatColor.GRAY + status);
		p.sendMessage(ChatColor.AQUA + "sender: " + ChatColor.GRAY + submitter);
		p.sendMessage(ChatColor.AQUA + "staff member: " + ChatColor.GRAY + staff);
		p.sendMessage(ChatColor.AQUA + "location: " + ChatColor.GRAY + location);
		p.sendMessage(ChatColor.AQUA + "date of request: " + ChatColor.GRAY + date);
		p.sendMessage(ChatColor.AQUA + "request: " + ChatColor.GRAY + message);
		p.sendMessage(ChatColor.AQUA + "comment: " + ChatColor.GRAY + comment);
	}
	
	//the set methods start here
	/**
	 * This is used to set a new comment
	 * The ticket must be updated for any changes to apply
	 * @return
	 */
	public void setComment(String newcomment) {
		comment = newcomment;
	}
	/**
	 * This is used to set a new staff member
	 * The ticket must be updated for any changes to apply
	 * @return
	 */
	public void setStaff(String newstaff) {
		staff = newstaff;
	}
	/**
	 * This is used to set a new status
	 * The ticket must be updated for any changes to apply
	 * @return
	 */
	public void setStatus(String newstatus) {
		status = newstatus;
		
	}
	/**
	 * This is used to update a ticket
	 * The ticket must be updated for any changes to apply
	 * @return
	 */
	public void update() throws SQLException {
		tickets.updateTicket(this);
	}
	/**
	 * @author Sgt_Tailor
	 * @since 2.0
	 * @param name
	 * @param achternaam
	 * @return
	 */
	public String ditIsVoorJul(String name, String achternaam) {
		//deze functie doet niets
		return name + achternaam;
	}
	
}
