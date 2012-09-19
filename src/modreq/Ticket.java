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
	public String getMessage() {
		return message;
	}
	public String getStaff() {
		return staff;
	}
	public String getSubmitter() {
		return submitter;
	}
	public String getDate() {
		return date;
	}
	public int getId() {
		return id;
	}
	public String getStatus() {
		return status;
	}
	public String getComment() {
		return comment;
	}
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
		String summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+date+" "+namecolor+submitter+" "+ChatColor.GRAY+summessage+"...";
		p.sendMessage(summary);
	}
	public void sendStatus(Player p) {
		String summessage = message;
		if(summessage.length() > 15) {
			summessage = summessage.substring(0,15);
		}
		String summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+date+ChatColor.DARK_GREEN+" ["+status+"]"+" "+ChatColor.GRAY+summessage+"...";
		p.sendMessage(summary);
	
	}
	public void sendMessageToPlayer(Player p) {
		p.sendMessage(ChatColor.GOLD + "---Info-about-ticked-#"+id+"---");
		p.sendMessage(ChatColor.AQUA + "status: " + ChatColor.GRAY + status);
		p.sendMessage(ChatColor.AQUA + "sender: " + ChatColor.GRAY + submitter);
		p.sendMessage(ChatColor.AQUA + "staff member: " + ChatColor.GRAY + staff);
		p.sendMessage(ChatColor.AQUA + "location: " + ChatColor.GRAY + location);
		p.sendMessage(ChatColor.AQUA + "date of request: " + ChatColor.GRAY + date);
		p.sendMessage(ChatColor.AQUA + "request: " + ChatColor.GRAY + message);
		p.sendMessage(ChatColor.AQUA + "comment: " + ChatColor.GRAY + comment);
	}
	
	//the set methods start here
	
	public void setComment(String newcomment) {
		comment = newcomment;
	}
	public void setStaff(String newstaff) {
		staff = newstaff;
	}
	public void setStatus(String newstatus) {
		status = newstatus;
		
	}
	public void update() throws SQLException {
		tickets.updateTicket(this);
	}
	
}
