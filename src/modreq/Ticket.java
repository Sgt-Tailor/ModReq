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
package modreq;

import java.sql.SQLException;
import java.util.ArrayList;

import modreq.managers.TicketHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Ticket {
    private int id;
    private String submitter;
    private String message;
    private String date;
    private Status status;
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

    private ArrayList<Comment> comments;

    public Ticket(ModReq plugin, int idp, String submitt, String messa,
	    String date, Status status, String loc, String sta) {
	submitter = submitt;
	id = idp;
	staff = sta;
	this.date = date;
	message = messa;
	this.status = status;
	location = loc;

	tickets = plugin.getTicketHandler();
	this.loc = plugin.Messages.getString("ticket.location", "Location");
	this.sub = plugin.Messages.getString("ticket.submitter", "Submitter");
	this.dt = plugin.Messages.getString("ticket.date", "Date of Request");
	this.sta = plugin.Messages.getString("ticket.status", "Status");
	this.com = plugin.Messages.getString("ticket.comment", "Comment");
	this.request = plugin.Messages.getString("ticket.request", "Request");
	this.staf = plugin.Messages.getString("ticket.staff", "Staff member");
	comments = new ArrayList<Comment>();
    }

    /**
     * This is used to get the message that the sumbmitter send.
     * 
     * @return
     */
    public String getMessage() {
	return message;
    }

    /**
     * This is used to get the staff member's name that is currently working on
     * the request.
     * 
     * @return
     */
    public String getStaff() {
	return staff;
    }

    /**
     * This is used to get the name of the submitter
     * 
     * @return
     */
    public String getSubmitter() {
	return submitter;
    }

    /**
     * This is used to get the date of the request
     * 
     * @return
     */
    public String getDate() {
	return date;
    }

    /**
     * This is used to get the ticket id
     * 
     * @return
     */
    public int getId() {
	return id;
    }

    /**
     * This is used to get the current status of the ticket
     * 
     * @return
     */
    public Status getStatus() {
	return status;
    }

    /**
     * This is used to get the location of the request. The format is worldname
     * x y z
     * 
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
	Location loc = new Location(w, xx, yy, zz);
	return loc;
    }

    /**
     * This is used to send a the summary of a ticket to a player an example can
     * be #id2 Mon 1 May Sgt_Tailor I need my house rolled back, ...
     * 
     * @return
     */
    public void sendSummarytoPlayer(Player p) {
	ChatColor namecolor = ChatColor.RED;
	Player[] list = Bukkit.getServer().getOnlinePlayers();
	int l = list.length;
	int n = 0;
	while (n < l) {
	    Player op = list[n];
	    if (op.getName().equals(submitter)) {
		if (op.isOnline()) {
		    namecolor = ChatColor.GREEN;
		}
	    }
	    n++;
	}
	String summessage = message;
	if (summessage.length() > 15) {
	    summessage = summessage.substring(0, 15);
	}
	String summary;
	if (((ModReq) Bukkit.getPluginManager().getPlugin("ModReq"))
		.getConfig().getString("use-nickname").equalsIgnoreCase("true")) {
	    if (playerIsOnline()) {
		submitter = Bukkit.getPlayer(submitter).getDisplayName();
	    }
	}
	summary = ChatColor.GOLD + "#" + id + ChatColor.AQUA + " " + date
		    + " [" + Integer.toString(comments.size()) + "] "
		    + namecolor + submitter + " " + ChatColor.GRAY + summessage
		    + "...";
	if(status.equals(Status.CLAIMED)) {
	    summary = summary +" "+ ChatColor.RED + " [Claimed]";
	}
	if(status.equals(Status.PENDING)) {
	    summary = summary +" "+ ChatColor.RED + " [Pending]";
	}

	p.sendMessage(summary);
    }

    /**
     * This is used to send the status of a ticket to a player an example can be
     * #3 [closed] I need my house rolled back, ...
     * 
     * @return
     */
    public void sendStatus(Player p) {
	String summessage = message;
	if (summessage.length() > 15) {
	    summessage = summessage.substring(0, 15);
	}
	String summary = ChatColor.GOLD + "#" + id + ChatColor.AQUA + " "
		+ date + " " + "[" + Integer.toString(comments.size()) + "]"
		+ " " + ChatColor.DARK_GREEN + " [" + status + "]" + " "
		+ ChatColor.GRAY + summessage + "...";
	p.sendMessage(summary);

    }

    /**
     * This is used to send all the ticket info to a player
     * 
     * @return
     */
    public void sendMessageToPlayer(Player p) {
	if (((ModReq) Bukkit.getPluginManager().getPlugin("ModReq"))
		.getConfig().getString("use-nickname").equalsIgnoreCase("true")) {
	    if (playerIsOnline()) {
		submitter = Bukkit.getPlayer(submitter).getDisplayName();
	    }
	}
	p.sendMessage(ChatColor.GOLD + "---Info-about-ticket-#" + id + "---");
	p.sendMessage(ChatColor.AQUA + this.sta + ": " + ChatColor.GRAY
		+ status);
	p.sendMessage(ChatColor.AQUA + this.sub + ": " + ChatColor.GRAY
		+ submitter);
	if (p.hasPermission("modreq.tp-id") || p.getName().equals(submitter)) {
	    p.sendMessage(ChatColor.AQUA + this.loc + ": " + ChatColor.GRAY
		    + location);
	}
	p.sendMessage(ChatColor.AQUA + this.staf + ": " + ChatColor.GRAY
		+ staff);
	p.sendMessage(ChatColor.AQUA + this.dt + ": " + ChatColor.GRAY + date);
	p.sendMessage(ChatColor.AQUA + this.request + ": " + ChatColor.GRAY
		+ message);
	p.sendMessage(ChatColor.AQUA + this.com + ":");

	sendComments(p);
    }

    private void sendComments(Player p) {
	int i = comments.size() - 1;//
	if (i == -1) {
	    return;
	}
	while (i >= 0) {
	    Comment c = comments.get(i);
	    String commenter = c.getCommenter();
	    String date = c.getDate();
	    String comment = c.getComment();
	    p.sendMessage(ChatColor.GOLD + "#" + Integer.toString(i + 1) + " "
		    + ChatColor.AQUA + date + " " + ChatColor.GOLD + commenter
		    + ": " + ChatColor.GRAY + comment);
	    i--;
	}

    }

    private boolean playerIsOnline() {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    if (p.getName().equalsIgnoreCase(submitter)) {
		return true;
	    }
	}

	return false;
    }

    /**
     * This is used to set a new staff member The ticket must be updated for any
     * changes to apply
     * 
     * @return
     */
    public void setStaff(String newstaff) {
	staff = newstaff;
    }

    /**
     * This is used to set a new status The ticket must be updated for any
     * changes to apply
     * 
     * @return
     */
    public void setStatus(Status newstatus) {
	status = newstatus;
    }

    /**
     * This is used to update a ticket The ticket must be updated for any
     * changes to apply
     * 
     * @return
     */
    public void update() throws SQLException {
	tickets.updateTicket(this);
    }

    public void sendMessageToSubmitter(String message) {// sends a message to
							// the submitter of a
							// ticket if he is
							// online
	Player[] op = Bukkit.getOnlinePlayers();
	for (int i = 0; i < op.length; i++) {
	    if (op[i].getName().equals(submitter)) {
		if (op[i].isOnline()) {
		    op[i].sendMessage(message);
		    return;
		}
	    }
	}
	return;
    }

    public ArrayList<Comment> getComments() {
	return comments;
    }

    public ArrayList<Comment> getCommentsBy(String name) {
	ArrayList<Comment> a = new ArrayList<Comment>();
	for (Comment c : comments) {
	    if (c.getCommenter().contains(name)) {
		a.add(c);
	    }
	}
	return a;
    }

    public Comment getComment(int number) {
	if (number < comments.size()) {
	    return comments.get(number - 1);
	}
	return null;
    }

    public void addComment(Comment c) {
	comments.add(c);
    }

    public void deleteComment(int i) {
	if (i < comments.size()) {
	    comments.remove(i - 1);
	}
    }

    public void addDefaultComment(Player p, CommentType c) {
	Comment comment = new Comment(p.getName(), c.getDefaultComment(), c);
	addComment(comment);
    }

    public void notifyStaff(String notification) {
	Player[] op = Bukkit.getOnlinePlayers();
	for (int i = 0; i < op.length; i++) {
	    if (op[i].getName().equals(staff)) {
		op[i].sendMessage(notification);
		return;
	    }
	}
    }
}
