package modreq.commands;

import java.sql.SQLException;

import modreq.CommentType;
import modreq.ModReq;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.managers.TicketHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpIdCommand extends SubCommandExecutor {
    private ModReq plugin;
    private TicketHandler tickets;

    public TpIdCommand(ModReq instance) {
	plugin = instance;

    }

    @command
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if (sender instanceof Player) {
	    Player p = (Player) sender;
	    if (p.hasPermission("modreq.tp-id")) {
		if (args.length == 1) {
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
			Ticket t = tickets.getTicketById(id);
			t.addDefaultComment(p, CommentType.TP);
			try {
			    t.update();
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			Location loc = t.getLocation();
			p.teleport(loc);
			p.sendMessage(ChatColor.GREEN
				+ plugin.Messages.getString("ticket-teleport1",
					"You have been teleported"));
			t.sendMessageToSubmitter(ChatColor.GREEN
				+ p.getName()
				+ " "
				+ plugin.Messages.getString("ticket-teleport2",
					"just teleported to your ModReq"));
			return;
		    }
		}
	    }
	}
    }

}
