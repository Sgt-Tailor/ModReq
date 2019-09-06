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

import modreq.repository.TicketRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Ticket {

    private int id;
    private String submitter;
    private UUID submitterUUID;
    private String message;
    private Instant date;
    private Status status;
    private String location;
    private String staff;
    private UUID staffUUID;

    private TicketRepository ticketRepository;
    private List<Comment> comments;

    public Ticket(int id, String submitter, UUID submitterUUID, String message, Instant date, Status status, String location, String staff, UUID staffUUID) {
        this.submitter = submitter;
        this.submitterUUID = submitterUUID;
        this.id = id;
        this.staff = staff;
        this.staffUUID = staffUUID;
        this.date = date;
        this.message = message;
        this.status = status;
        this.location = location;

        this.ticketRepository = ModReq.getInstance().getTicketRepository();
        this.comments = new ArrayList<>();
    }

    /**
     * This is used to get the message that the submitter send.
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
    public Instant getDate() {
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

    public String getLocationString() {
        return location;
    }

    /**
     * This is used to send a the summary of a ticket to a player an example can
     * be #id2 Mon 1 May Sgt_Tailor I need my house rolled back, ...
     *
     * @return
     */
    public void sendSummarytoPlayer(Player p) {
        ChatColor namecolor = Bukkit.getPlayer(submitterUUID) == null ? ChatColor.RED : ChatColor.GREEN;

        String summessage = message;
        if (summessage.length() > 15) {
            summessage = summessage.substring(0, 15);
        }
        String summary;
        if (Bukkit.getPluginManager().getPlugin("ModReq")
                .getConfig().getString("use-nickname").equalsIgnoreCase("true")) {
            if (playerIsOnline()) {
                submitter = Bukkit.getPlayer(submitter).getDisplayName();
            }
        }

        String finalStatusString1 = status.getStatusString();
        String finalSummessage = summessage;
        Message.sendToPlayer(MessageType.STATUS_SUMMARY, p, new HashMap<String, String>() {{
            put("ticketId", Integer.toString(id));
            put("date", ModReq.getDateTimeFormatter().format(date));
            put("comments", Integer.toString(comments.size()));
            put("submitter", namecolor + submitter);
            put("message", finalSummessage);
            put("status", finalStatusString1);
        }});
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

        String finalSummessage = summessage;
        Message.sendToPlayer(MessageType.STATUS_SUMMARY, p, new HashMap<String, String>() {{
            put("ticketId", Integer.toString(id));
            put("date", ModReq.getDateTimeFormatter().format(date));
            put("comments", Integer.toString(comments.size()));
            put("message", finalSummessage);
            put("status", status.getStatusString());
        }});

    }

    /**
     * This is used to send all the ticket info to a player
     *
     * @return
     */
    public void sendMessageToPlayer(Player p) {
        if (Bukkit.getPluginManager().getPlugin("ModReq")
                .getConfig().getString("use-nickname").equalsIgnoreCase("true")) {
            if (playerIsOnline()) {
                submitter = Bukkit.getPlayer(submitter).getDisplayName();
            }
        }
        ModReq plugin = ModReq.getInstance();
        String location = plugin.Messages.getString("ticket.location", "Location");
        String submitter = plugin.Messages.getString("ticket.submitter", "Submitter");
        String dateOfRequest = plugin.Messages.getString("ticket.date", "Date of Request");
        String status = plugin.Messages.getString("ticket.status", "Status");
        String comment = plugin.Messages.getString("ticket.comment", "Comment");
        String request = plugin.Messages.getString("ticket.request", "Request");
        String staff = plugin.Messages.getString("ticket.staff", "Staff member");

        Message.sendToPlayer(MessageType.TICKET_HEADER, p, Integer.toString(id));
        p.sendMessage(ChatColor.AQUA + status + ": " + ChatColor.GRAY + this.status);
        p.sendMessage(ChatColor.AQUA + submitter + ": " + ChatColor.GRAY + this.submitter);

        if (p.hasPermission("modreq.tp-id") || p.getName().equals(this.submitter)) {
            p.sendMessage(ChatColor.AQUA + location + ": " + ChatColor.GRAY + this.location);
        }

        p.sendMessage(ChatColor.AQUA + staff + ": " + ChatColor.GRAY + (this.staff == null ? "-" : this.staff));
        p.sendMessage(ChatColor.AQUA + dateOfRequest + ": " + ChatColor.GRAY + ModReq.getDateTimeFormatter().format(this.date));
        p.sendMessage(ChatColor.AQUA + request + ": " + ChatColor.GRAY + this.message);
        p.sendMessage(ChatColor.AQUA + comment + ":");

        sendComments(p);
    }

    private void sendComments(Player p) {
        int number = comments.size();
        for (Comment c : comments) {
            String commenter = c.getCommenter();
            Instant date = c.getDate();
            String comment = c.getComment();
            comment = ChatColor.translateAlternateColorCodes('&', comment);
            p.sendMessage(ChatColor.GOLD + "#" + number + " "
                    + ChatColor.AQUA + ModReq.getDateTimeFormatter().format(date) + " " + ChatColor.GOLD + commenter
                    + ": " + ChatColor.GRAY + comment);
            number--;
        }

    }

    private boolean playerIsOnline() {
        Player p = Bukkit.getPlayerExact(submitter);
        return p != null && p.isOnline();
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new RuntimeException("id already set on ticket");
        }

        this.id = id;
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
        ticketRepository.updateTicket(this);
    }

    public void sendMessageToSubmitter(String message) {
        Player p = Bukkit.getPlayerExact(submitter);
        if (p == null || !p.isOnline()) {
            return;
        }

        p.sendMessage(message);
    }

    public List<Comment> getComments() {
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

    public void addComment(Comment c) {
        comments.add(c);
    }

    public void insertComment(Comment c) {
        comments.add(0, c);
    }

    public void addDefaultComment(Player p, CommentType c) {
        Comment comment = new Comment(p.getName(), p.getUniqueId(), c.getDefaultComment(), c);
        addComment(comment);
    }

    public void notifyStaff(String notification) {
        Player p = Bukkit.getPlayerExact(staff);
        if (p == null || !p.isOnline()) {
            return;
        }
        p.sendMessage(notification);

    }

    public UUID getSubmitterUUID() {
        return submitterUUID;
    }

    public void setSubmitterUUID(UUID submitterUUID) {
        this.submitterUUID = submitterUUID;
    }

    public UUID getStaffUUID() {
        return staffUUID;
    }

    public void setStaffUUID(UUID staffUUID) {
        this.staffUUID = staffUUID;
    }
}
