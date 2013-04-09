package modreq.commands;

import modreq.ModReq;
import modreq.korik.SubCommandExecutor;
import modreq.managers.TicketHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ClearticketsCommand extends SubCommandExecutor {
    private ModReq plugin;
    private TicketHandler tickets;

    public ClearticketsCommand(ModReq instance) {
	plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if (sender.hasPermission("modreq.cleartickets")) {
	    tickets.clearTickets();
	    sender.sendMessage(ChatColor.GREEN
		    + "All tickets have been removed");
	    return;
	}
    }

}
