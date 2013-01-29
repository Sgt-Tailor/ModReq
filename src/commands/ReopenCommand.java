package commands;

import java.sql.SQLException;

import managers.TicketHandler;
import modreq.Status;
import modreq.Ticket;
import modreq.modreq;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import korik.SubCommandExecutor;
import korik.Utils;

public class ReopenCommand extends SubCommandExecutor{
    private modreq plugin;
    private TicketHandler tickets;
    public ReopenCommand(modreq instance) {
	plugin = instance;
    }
    @command
    public void Integer(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player){
		Player p = (Player)sender;
		if(p.hasPermission("modreq.reopen")){
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
					String comment = Utils.join(args, " ", 1);
					Ticket t = tickets.getTicketById(id);
					
					Status status = Status.OPEN;
					String staff = sender.getName();
					
					t.setComment(comment);
					t.setStaff(staff);
					t.setStatus(status);
					try {
						t.update();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					p.sendMessage(ChatColor.GREEN + plugin.Messages.getString("ticket-re-opened","Ticket re-opened"));
					if(comment.equals("")) {
						t.sendMessageToSubmitter(ChatColor.GREEN + p.getName() + " "+plugin.Messages.getString("reopen-ticket","just re-opened your ModReq"));
					}
					else {
						t.sendMessageToSubmitter( ChatColor.GREEN + p.getName() + " "+ plugin.Messages.getString("reopen-with-comment","just re-opened your ModReq with the comment: "+ChatColor.GRAY + comment));
					}
					return;
				}
			}
		}
	}
    }

}
