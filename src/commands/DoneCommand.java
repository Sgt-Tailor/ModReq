package commands;

import java.sql.SQLException;

import korik.SubCommandExecutor;
import korik.Utils;
import managers.TicketHandler;
import modreq.Status;
import modreq.Ticket;
import modreq.modreq;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneCommand extends SubCommandExecutor{
    
    private modreq plugin;
    private TicketHandler tickets;
    public DoneCommand(modreq instance) {
	plugin = instance;
    }
    @command
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player){
		Player p = (Player)sender;
		if(p.hasPermission("modreq.close")){
			if(args.length>0){
				int id;
				try{id = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					p.sendMessage(ChatColor.RED + args[0] + " "+ plugin.Messages.getString("no-number", "is not a number"));
					return;
				}
				if(tickets.getTicketCount() < id) {
					p.sendMessage(ChatColor.RED + plugin.Messages.getString("no-ticket","That ticket does not exist"));
					return;
				}
				else {
					String comment = Utils.join(args, " ", 1);
					Ticket t = tickets.getTicketById(id);
					
					Status status = Status.CLOSED;
					String staff = sender.getName();
					
					String currenstatus = t.getStatus().getStatusString();
					String currentstaff = t.getStaff();
					
					if(!currenstatus.equals("open")) {
						if(!currentstaff.equals(staff)) {
							p.sendMessage(ChatColor.RED + plugin.Messages.getString("can-not-close","You can not close that ticket"));
							return;
						}
					}
					
					
					t.setComment(comment);
					t.setStaff(staff);
					t.setStatus(status);
					try {
						t.update();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					p.sendMessage(ChatColor.GREEN + plugin.Messages.getString("ticket-closed", "Ticket closed"));
					if(comment == null || comment.equals("")) {
						t.sendMessageToSubmitter(ChatColor.GREEN + p.getName() + " "+ plugin.Messages.getString("closed-ticket", "just closed your ModReq"));
					}
					else {
						t.sendMessageToSubmitter(ChatColor.GREEN + p.getName() + " "+plugin.Messages.getString("closed-ticket-withmessage","just closed your ModReq with the comment") + ": ");
						t.sendMessageToSubmitter(ChatColor.GRAY + comment);
					}
					return;
				}
			}
		}
	}	
    }

}
