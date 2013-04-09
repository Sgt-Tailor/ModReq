package modreq.commands;

import java.sql.SQLException;

import modreq.Comment;
import modreq.CommentType;
import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;
import modreq.managers.TicketHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public DoneCommand(ModReq instance) {
	plugin = instance;
    }

    @command
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if (sender instanceof Player) {
	    Player p = (Player) sender;
	    if (p.hasPermission("modreq.close")) {
		if (args.length > 0) {
		    int id;
		    try {
			id = Integer.parseInt(args[0]);
		    } catch (Exception e) {
			p.sendMessage(ChatColor.RED
				+ args[0]
				+ " "
				+ plugin.Messages.getString("no-number",
					"is not a number"));
			return;
		    }
		    if (tickets.getTicketCount() < id) {
			p.sendMessage(ChatColor.RED
				+ plugin.Messages.getString("no-ticket",
					"That ticket does not exist"));
			return;
		    } else {
			String comment = Utils.join(args, " ", 1);
			Ticket t = tickets.getTicketById(id);

			Status status = Status.CLOSED;
			String staff = sender.getName();

			String currenstatus = t.getStatus().getStatusString();
			String currentstaff = t.getStaff();

			if (!currenstatus.equals("open")) {
			    if (!currentstaff.equals(staff)
				    && !sender
					    .hasPermission("modreq.overwrite.close")) {
				p.sendMessage(ChatColor.RED
					+ plugin.Messages
						.getString("can-not-close",
							"You can not close that ticket"));
				return;
			    }
			}

			t.addComment(new Comment(sender.getName(), comment,
				CommentType.CLOSE));
			t.setStaff(staff);
			t.setStatus(status);
			try {
			    t.update();
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			p.sendMessage(ChatColor.GREEN
				+ plugin.Messages.getString("ticket-closed",
					"Ticket closed"));
			if (comment == null || comment.equals("")) {
			    t.sendMessageToSubmitter(ChatColor.AQUA
				    + p.getName()
				    + ChatColor.GREEN
				    + " "
				    + plugin.Messages.getString(
					    "closed-ticket",
					    "just closed your ModReq"));
			} else {
			    t.sendMessageToSubmitter(ChatColor.AQUA
				    + p.getName()
				    + ChatColor.GREEN
				    + " "
				    + plugin.Messages
					    .getString(
						    "closed-ticket-withmessage",
						    "just closed your ModReq with the comment")
				    + ": ");
			    t.sendMessageToSubmitter(ChatColor.GRAY + comment);
			}
			return;
		    }
		}
	    }
	}
    }

}
