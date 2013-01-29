package commands;

import managers.TicketHandler;
import modreq.Ticket;
import modreq.modreq;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import korik.SubCommandExecutor;

public class TpIdCommand extends SubCommandExecutor{
    private modreq plugin;
    private TicketHandler tickets;
    public TpIdCommand(modreq instance) {
	plugin = instance;
	
    }
    @command
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player){
		Player p = (Player)sender;
		if(p.hasPermission("modreq.tp-id")){
			if(args.length == 1){
				int id;
				try {
					id = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					p.sendMessage(ChatColor.RED + args[0] +" "+ plugin.Messages.getString("no-number", "is not a number"));
					return ;
				}
				if(tickets.getTicketCount() < id) {
					p.sendMessage(ChatColor.RED + plugin.Messages.getString("no-ticket","That ticket does not exist"));
					return ;
				}
				else {
					Ticket t = tickets.getTicketById(id);
					Location loc = t.getLocation();
					p.teleport(loc);
					p.sendMessage(ChatColor.GREEN + plugin.Messages.getString("ticket-teleport1","You have been teleported"));
					t.sendMessageToSubmitter(ChatColor.GREEN + p.getName() + " "+plugin.Messages.getString("ticket-teleport2","just teleported to your ModReq"));
					return ;
				}
			}
		}
	}
    }

}
