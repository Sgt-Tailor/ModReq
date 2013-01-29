package modreq;

import java.sql.SQLException;

import managers.TicketHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Ticket
{
	private int id;
	private String submitter;
	private String message;
	private String date;
	private Status status;
	private String comment;
	private String location;
	private String staff;
	private String sub;
	private String dt;
	private String sta;
	private String com;
	private String loc;
	private String staf;
	private String request;
	private TicketHandler tickets;
	
	public Ticket(modreq plugin,int idp, String submitt, String messa, String date, Status status, String comm, String loc, String sta)	{
		submitter = submitt;
		id = idp;
		staff = sta;
		this.date = date;
		message = messa;
		this.status = status;
		location = loc;
		comment = comm;
		
		tickets = plugin.getTicketHandler();
		this.loc = plugin.Messages.getString("ticket.location", "Location");
		this.sub = plugin.Messages.getString("ticket.submitter", "Submitter");
		this.dt = plugin.Messages.getString("ticket.date", "Date of Request");
		this.sta = plugin.Messages.getString("ticket.status", "Status");
		this.com = plugin.Messages.getString("ticket.comment", "Comment");
		this.request = plugin.Messages.getString("ticket.request", "Request");
		this.staf = plugin.Messages.getString("ticket.staff", "Staff member");
		
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
	public Status getStatus() {
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
		String world = location.split(" @ ")[0];
		String rest = location.split(" @ ")[1];
		String x = rest.split(" ")[0];
		String y = rest.split(" ")[1];
		String z = rest.split(" ")[2];
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
		if((( modreq )Bukkit.getPluginManager().getPlugin("ModReq")).getConfig().getString("use-nickname").equalsIgnoreCase("true")){	
			if(playerIsOnline()) {
				submitter = Bukkit.getPlayer(submitter).getDisplayName();
			}
		}
		if(status == Status.CLAIMED) {
			summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+ " " + date+" "+namecolor+submitter+" "+ChatColor.GRAY+summessage+"..." + ChatColor.RED + " [Claimed]";
		}
		else {
			summary = ChatColor.GOLD + "#"+id+ ChatColor.AQUA+ " " + date+" "+namecolor+submitter+" "+ChatColor.GRAY+summessage+"...";
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
		if((( modreq )Bukkit.getPluginManager().getPlugin("ModReq")).getConfig().getString("use-nickname").equalsIgnoreCase("true")){	
			if(playerIsOnline()) {
				submitter = Bukkit.getPlayer(submitter).getDisplayName();
			}
		}
		p.sendMessage(ChatColor.GOLD + "---Info-about-ticket-#"+id+"---");
		p.sendMessage(ChatColor.AQUA + this.sta + ": " + ChatColor.GRAY + status);
		p.sendMessage(ChatColor.AQUA + this.sub+": " + ChatColor.GRAY + submitter);
		p.sendMessage(ChatColor.AQUA + this.loc+": " + ChatColor.GRAY + location);
		p.sendMessage(ChatColor.AQUA + this.staf+": " + ChatColor.GRAY + staff);
		p.sendMessage(ChatColor.AQUA + this.dt+": " + ChatColor.GRAY + date);
		p.sendMessage(ChatColor.AQUA + this.request+": " + ChatColor.GRAY + message);
		p.sendMessage(ChatColor.AQUA + this.com+": " + ChatColor.GRAY + comment);
	}
	
	private boolean playerIsOnline() {
		for(Player p: Bukkit.getOnlinePlayers()) {
			if(p.getName().equalsIgnoreCase(submitter)) {
				return true;
			}
		}
			
		return false;
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
	public void setStatus(Status newstatus) {
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
	public void sendMessageToSubmitter(String message) {//sends a message to the submitter of a ticket if he is online
		Player[] op = Bukkit.getOnlinePlayers();
		for(int i = 0; i<op.length; i++) {
			if(op[i].getName().equals(submitter)) {
				if(op[i].isOnline()) {
					op[i].sendMessage(message);
					return;
				}
			}
		}
		return;
	}
	
}
