package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import managers.TicketHandler;
import modreq.modreq;
import korik.SubCommandExecutor;

public class ClearticketsCommand extends SubCommandExecutor{
    private modreq plugin;
    private TicketHandler tickets;
    
    public ClearticketsCommand(modreq instance) {
	plugin = instance;
    }
    
    @command
    public void Null(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender.hasPermission("modreq.cleartickets")) {
	    tickets.clearTickets();
	    sender.sendMessage(ChatColor.GREEN + "All tickets have been removed");
	    return ;	
	}
    }

}
