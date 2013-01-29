package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import korik.SubCommandExecutor;
import managers.TicketHandler;
import modreq.modreq;

public class TicketCommand extends SubCommandExecutor{
    private modreq plugin;
    private TicketHandler tickets;
    
    public TicketCommand(modreq instance) {
	plugin = instance;
    }
    
    @command
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player) {
		if(sender.hasPermission("modreq.check")) {
			if(args.length == 1) {
				int id;
				try{id = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					sender.sendMessage(ChatColor.RED + args[0] +" "+ plugin.Messages.getString("no-number", "is not a number"));
					return;
				}
				if(tickets.getTicketCount() < id) {
					sender.sendMessage(ChatColor.RED + plugin.Messages.getString("no-ticket","That ticket does not exist"));
					return;
				}
				else {
					tickets.getTicketById(id).sendMessageToPlayer((Player) sender);
					return;
				}
			}
		}
	}
    }

}
