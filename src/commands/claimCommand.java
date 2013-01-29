package commands;

import java.sql.SQLException;

import korik.SubCommandExecutor;
import managers.TicketHandler;
import modreq.Status;
import modreq.Ticket;
import modreq.modreq;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class claimCommand extends SubCommandExecutor{
    private modreq plugin;
    private TicketHandler tickets;
    public claimCommand(modreq instance) {
	plugin = instance;
    }
    @command
    public void Null(CommandSender sender, String[] args) {
	sender.sendMessage("/claim <id>");
    }
    @command(
	    	maximumArgsLength = 1,
	    	usage = "/claim <id>")
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player){
		Player p = (Player)sender;
		if(p.hasPermission("modreq.claim")){
			if(args.length>0){
				int id;
				try{id = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					p.sendMessage(ChatColor.RED + args[0] +" "+ plugin.Messages.getString("no-number", "is not a number"));
					return;
				}
				if(tickets.getTicketCount() < id) {
					p.sendMessage(ChatColor.RED + plugin.Messages.getString("no-ticket","That ticket does not exist"));
					return;
				}
				else {
					Ticket t = tickets.getTicketById(id);
					
					Status currentstatus = t.getStatus();
												
					Status status = Status.CLAIMED;
					String staff = sender.getName();
					if(!currentstatus.equals(Status.OPEN)) {
						p.sendMessage(ChatColor.RED + plugin.Messages.getString("can-not-claim","You can not claim that ticket"));
						return;
					}
					if(plugin.getConfig().getBoolean("may-claim-multiple", false) == false) {
						if(tickets.hasClaimed((Player) sender)) {
							p.sendMessage(ChatColor.RED + plugin.Messages.getString("can-not-claim","You can not claim that ticket"));
							return;
						}
					}
					
					t.setStaff(staff);
					t.setStatus(status);
					try {
						t.update();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					p.sendMessage(ChatColor.GREEN + plugin.Messages.getString("ticket-claimed","Ticket claimed"));
					t.sendMessageToSubmitter(ChatColor.GREEN + p.getName() + " "+plugin.Messages.getString("ticket-claimed2","just claimed your ModReq"));
					return;
				}
			}
		}
	}	
	else {
	    sender.sendMessage("This command can only be ran as a player");
	}
	
    }
}
